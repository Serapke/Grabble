package com.grabble.android.mantas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.grabble.android.mantas.utils.TaskUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mantas on 04/01/2017.
 */

public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

    private final String TAG = UserLoginTask.class.getSimpleName();

    private final String nickname;
    private final LoginActivity loginActivity;
    private TaskUtil taskUtil;

    UserLoginTask(LoginActivity loginActivity, String nickname) {
        this.nickname = nickname;
        this.loginActivity = loginActivity;
        this.taskUtil = new TaskUtil(loginActivity);
    }

    /**
     *  Sends a request to the server with nickname in JSON format.
     *
     *  If the user was successfully created in the server and returned with HTTP_OK status code,
     *  parses the response JSON and saves user info to SharedPreferences,
     *  returns HTTP_OK (200) status code.
     *
     *  Else returns the appropriate status code, identifying the possible issue.
     */
    @Override
    protected Integer doInBackground(Void... params) {

        Integer statusCode;
        JSONObject response;

        // Checks if user is connected to any network
        if (!taskUtil.isOnline()) return HttpURLConnection.HTTP_NOT_FOUND;

        try {
            HttpURLConnection urlConnection = setupHttpURLConnection();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            postNickname(out);

            statusCode = urlConnection.getResponseCode();
            Log.d(TAG, "Server responded with code: " + statusCode);

            if (statusCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                response = readStream(in);
                Log.d(TAG, "Server responded with JSON: " + response.toString());
                saveUserInfo(response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_BAD_REQUEST;
        } catch (IOException e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_NOT_FOUND;
        } catch (JSONException e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_BAD_REQUEST;
        }

        return statusCode;
    }

    private HttpURLConnection setupHttpURLConnection() throws IOException {
        URL url = new URL(loginActivity.getString(R.string.server_user_create));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        return urlConnection;
    }

    private JSONObject readStream(BufferedReader in) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line + "\n");
        }
        in.close();
        return new JSONObject(sb.toString());
    }

    /**
     *  Save user info to SharedPreferences
     */
    private void saveUserInfo(JSONObject json) throws JSONException {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(loginActivity);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(
                loginActivity.getString(R.string.pref_user_nickname_key),
                json.getString(loginActivity.getString(R.string.json_user_nickname_key)));
        editor.putString(
                loginActivity.getString(R.string.pref_user_auth_token_key),
                json.getString(loginActivity.getString(R.string.json_user_auth_token_key)));
        editor.putInt(
                loginActivity.getString(R.string.pref_user_score_key),
                json.getInt(loginActivity.getString(R.string.json_user_score_key)));
        editor.putString(
                loginActivity.getString(R.string.pref_user_place_key),
                json.getString(loginActivity.getString(R.string.json_user_place_key)));
        editor.putString(loginActivity.getString(
                R.string.pref_user_created_at),
                json.getString(loginActivity.getString(R.string.json_user_created_at_key)));
        editor.putString(
                loginActivity.getString(R.string.pref_number_of_users_key),
                json.getString(loginActivity.getString(R.string.json_user_place_key)));
        editor.apply();
    }

    /**
     *  Adds nickname to the request
     */
    private void postNickname(OutputStreamWriter out) throws JSONException, IOException {
        JSONObject user = new JSONObject();
        JSONObject userInfo = new JSONObject();
        userInfo.put(loginActivity.getString(R.string.json_user_nickname_key), this.nickname);
        user.put(loginActivity.getString(R.string.json_user_key), userInfo);
        out.write(user.toString());
        out.flush();
    }

    /**
     *  If successfully created user in the server, starts MapsActivity,
     *  notifies if the given nickname already exists, or cannot
     *  connect to the server.
     */
    @Override
    protected void onPostExecute(final Integer statusCode) {
        loginActivity.unlockLoginAttempt();

        if (statusCode.equals(HttpURLConnection.HTTP_CREATED)) {
            Intent intent = new Intent(loginActivity.getApplicationContext(), MapsActivity.class);
            loginActivity.startActivity(intent);
        } else if (statusCode.equals(HttpURLConnection.HTTP_CONFLICT)) {
            Toast.makeText(
                    loginActivity,
                    loginActivity.getString(R.string.error_nickname_already_exists),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(
                    loginActivity,
                    loginActivity.getString(R.string.error_cannot_connect_to_server),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        loginActivity.unlockLoginAttempt();
    }
}