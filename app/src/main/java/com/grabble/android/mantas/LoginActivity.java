package com.grabble.android.mantas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText nicknameView;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        skipIfAlreadyLoggedIn();

         setContentView(R.layout.activity_login);
        // Set up the login form.

        nicknameView = (EditText) findViewById(R.id.nickname);
        loginButton = (Button) findViewById(R.id.sign_in_button);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void skipIfAlreadyLoggedIn() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPrefs.contains(getString(R.string.pref_user_nickname_key))) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (mAuthTask != null) {
            Log.d(TAG, mAuthTask.toString());
            return;
        }

        // Reset errors.
        nicknameView.setError(null);

        // Store values at the time of the login attempt.
        String nickname = nicknameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nickname) || !isNickNameSizeValid(nickname)) {
            nicknameView.setError(getString(R.string.error_invalid_nickname_size));
            focusView = nicknameView;
            cancel = true;
        } else if (!isNickNameCharactersValid(nickname)) {
            nicknameView.setError(getString(R.string.error_invalid_nickname_characters));
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

    private boolean isNickNameSizeValid(String nickname) {
        return nickname.length() >= 4;
    }

    private boolean isNickNameCharactersValid(String nickname) {
        String pattern= "^[a-zA-Z][a-zA-Z0-9]*$";
        return nickname.matches(pattern);
    }
}

