package com.grabble.android.mantas;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    public FetchLeaderboardTask(Context context, LeaderboardAdapter leaderboardAdapter) {
        this.context = context;
        this.leaderboardAdapter = leaderboardAdapter;
    }

    @Override
    protected List<User> doInBackground(Void... params) {

        JSONArray response;
        Integer statusCode;

        List<User> users = new ArrayList<>();

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return users;
    }

    private List<User> getLeaderboardDataFromJson(JSONArray response) throws JSONException {
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

    private HttpURLConnection setupHttpURLConnection() throws IOException {
        URL url = new URL(context.getString(R.string.server_get_leaderboard));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");
        return urlConnection;
    }

    private JSONArray readStream(BufferedReader in) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line + "\n");
        }
        in.close();
        JSONArray json = new JSONArray(sb.toString());
        return json;
    }

    @Override
    protected void onPostExecute(List<User> result) {
        if (result != null && leaderboardAdapter != null) {
            leaderboardAdapter.clear();
            for (User user : result) {
                leaderboardAdapter.add(user);
            }
        }
    }
}
