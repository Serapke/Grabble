package com.grabble.android.mantas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.grabble.android.mantas.utils.TaskUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mantas on 14/01/2017.
 */

public class FetchLeaderboardTask extends AsyncTask<Void, Void, List<User>> {

    private final String TAG = FetchLeaderboardTask.class.getSimpleName();

    private final Context context;
    private LeaderboardAdapter leaderboardAdapter;
    private TaskUtil taskUtil;

    FetchLeaderboardTask(Context context, LeaderboardAdapter leaderboardAdapter) {
        this.context = context;
        this.leaderboardAdapter = leaderboardAdapter;
        this.taskUtil = new TaskUtil(context);
    }

    /**
     *  Connects to the server and if the response status code is 200, returns the updated
     *  user list from the server. Else returns null and leaderboard adapter is not updated.
     */
    @Override
    protected List<User> doInBackground(Void... params) {
        JSONArray response;
        Integer statusCode;
        List<User> users = null;

        // Checks if user is connected to any network
        if (!taskUtil.isOnline()) return users;

        try {
            HttpURLConnection urlConnection = setupHttpURLConnection();
            statusCode = urlConnection.getResponseCode();
            Log.d(TAG, "Server responded with code: " + statusCode);

            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                response = readStream(in);
                Log.d(TAG, "Server responded with JSON: " + response.toString());
                users = getLeaderboardDataFromJson(response);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     *  Given JSONArray with user information, parses it to a list of Users
     */
    protected List<User> getLeaderboardDataFromJson(JSONArray response) throws JSONException {
        List<User> leaderboardData = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject userJson = response.getJSONObject(i);
            User user = new User(
                    userJson.getLong(context.getString(R.string.json_user_id_key)),
                    userJson.getString(context.getString(R.string.json_user_nickname_key)),
                    userJson.getInt(context.getString(R.string.json_user_score_key)),
                    userJson.getInt(context.getString(R.string.json_user_place_key)));
            leaderboardData.add(user);

        }
        return leaderboardData;
    }

    /**
     *  Setups HttpURLConnection to server with GET request method,
     *  accepting JSON responses
     */
    private HttpURLConnection setupHttpURLConnection() throws IOException {
        URL url = new URL(context.getString(R.string.server_get_leaderboard));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("GET");
        return urlConnection;
    }

    /**
     *  Reads the stream from server response and converts it JSONArray object
     *  for further parsing
     */
    private JSONArray readStream(BufferedReader in) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line).append("\n");
        }
        in.close();
        return new JSONArray(sb.toString());
    }

    /**
     *  If the server responded with user leaderboard data, clears the leaderboard
     *  adapter and fills it with new data. Also, updates the number of users preference.
     */
    @Override
    protected void onPostExecute(List<User> result) {
        if (result != null && leaderboardAdapter != null) {
            leaderboardAdapter.clear();
            for (User user : result) {
                leaderboardAdapter.add(user);
            }
            updateNumberOfUsersPref(result.size());
        }
    }

    /**
     *  Given a new user count updates the corresponding default SharedPreferences entry
     */
    protected void updateNumberOfUsersPref(Integer size) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(
                context.getString(R.string.pref_number_of_users_key),
                size.toString());
        editor.apply();
    }
}
