package com.grabble.android.mantas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers signup via nickname.
 *
 * Shown only when no user has signed up after the installation.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private EditText nicknameView;

    String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (alreadyLoggedIn()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            return;
        }
        Log.d(TAG, "Not already logged in");
        setContentView(R.layout.activity_login);

        nicknameView = (EditText) findViewById(R.id.nickname);
        Button loginButton = (Button) findViewById(R.id.sign_in_button);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = nicknameView.getText().toString();
                attemptLogin(nickname);
            }
        });
    }

    protected boolean alreadyLoggedIn() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.contains(getString(R.string.pref_user_nickname_key));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String nickname) {

        if (mAuthTask != null) {
            Log.d(TAG, mAuthTask.toString());
            return;
        }

        // Reset errors.
        nicknameView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (!isNicknameValid(nickname)) {
            nicknameView.setError(getString(R.string.error_invalid_nickname));
            focusView = nicknameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Kick off a background task to perform the user login attempt.
            mAuthTask = new UserLoginTask(this, nickname);
            mAuthTask.execute();
        }
    }

    protected void unlockLoginAttempt() {
        mAuthTask = null;
    }

    /**
     *  A nickname must start with an uppercase letter, have at least 4 and at most 20 characters,
     *  and consist of only alphanumeric characters.
     */
    protected boolean isNicknameValid(String nickname) {
        if (nickname == null) return false;
        String pattern = "^[a-zA-Z][a-zA-Z0-9]{3,19}$";
        return nickname.matches(pattern);
    }
}

