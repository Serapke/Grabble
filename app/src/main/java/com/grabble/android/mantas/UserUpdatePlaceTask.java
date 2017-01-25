package com.grabble.android.mantas;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.grabble.android.mantas.data.GrabbleDbHelper;
import com.grabble.android.mantas.utils.AchievementsUtil;
import com.grabble.android.mantas.utils.TaskUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mantas on 14/01/2017.
 */

public class UserUpdatePlaceTask extends AsyncTask<Void, Void, Integer> {

    private final String TAG = UserLoginTask.class.getSimpleName();

    private final Integer score;
    private final Context context;
    GrabbleDbHelper dbHelper;
    SQLiteDatabase db;
    private TaskUtil taskUtil;

    UserUpdatePlaceTask(Context context, Integer score) {
        this.score = score;
        this.context = context;
        dbHelper = new GrabbleDbHelper(context);
        db = dbHelper.getWritableDatabase();
        this.taskUtil = new TaskUtil(context);
    }

    /**
     *  Sends a POST request to server with updated score.
     *
     *  If response code is 200, saves the updated user place.
     */
    @Override
    protected Integer doInBackground(Void... params) {

        Integer place = null;

        Integer statusCode;
        JSONObject response;

        // Checks if user is connected to any network
        if (!taskUtil.isOnline()) return HttpURLConnection.HTTP_NOT_FOUND;

        try {
            HttpURLConnection urlConnection = setupHttpURLConnection();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            postScore(out);

            statusCode = urlConnection.getResponseCode();
            Log.d(TAG, "Server responded with code: " + statusCode);

            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                response = readStream(in);
                Log.d(TAG, "Server responded with JSON: " + response.toString());
                place = saveUserPlace(response);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return place;
    }

    private HttpURLConnection setupHttpURLConnection() throws IOException {
        URL url = new URL(context.getString(R.string.server_update_place));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String auth_token = sharedPrefs.getString(
                context.getString(R.string.pref_user_auth_token_key),
                ""
        );
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Authorization", auth_token);
        return urlConnection;
    }

    private JSONObject readStream(BufferedReader in) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line).append("\n");
        }
        in.close();
        return new JSONObject(sb.toString());
    }

    private Integer saveUserPlace(JSONObject json) throws JSONException {
        String place = json.getString(context.getString(R.string.json_user_place_key));
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(context.getString(
                R.string.pref_user_place_key),
                place);
        editor.apply();
        return Integer.parseInt(place);
    }

    private void postScore(OutputStreamWriter out) throws JSONException, IOException {
        JSONObject user = new JSONObject();
        JSONObject userInfo = new JSONObject();
        userInfo.put(context.getString(R.string.json_user_score_key), this.score);
        user.put(context.getString(R.string.json_user_key), userInfo);
        out.write(user.toString());
        out.flush();
    }

    /**
     *  Check if user can unlock any of the achievements related to user's place in leaderboard
     */
    protected void onPostExecute(Integer place) {
        if (place != null) {
            AchievementsUtil achv = new AchievementsUtil(context, db);
            achv.checkLeaderboardAchievements(place);
        }
    }
}
