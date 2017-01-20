package com.grabble.android.mantas;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Mantas on 18/01/2017.
 */

@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentedTest {

    Context context;
    ActivityMonitor activityMonitor;
    Instrumentation instrumentation;


    @Rule
    public final ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<LoginActivity>(LoginActivity.class, false, false) {
                @Override
                protected void beforeActivityLaunched() {
                    clearSharedPrefs(InstrumentationRegistry.getTargetContext());
                    super.beforeActivityLaunched();
                }
            };

    @Before
    public void setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        context = InstrumentationRegistry.getTargetContext();
        activityMonitor = instrumentation.addMonitor(MapsActivity.class.getName(), null, false);
    }

    private void clearSharedPrefs(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.apply();
    }

    @Test
    public void alreadyLoggedIn_HasAlreadyLoggedIn_ReturnsTrue() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        rule.launchActivity(intent);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(context.getString(R.string.pref_user_nickname_key), context.getString(R.string.pref_user_nickname_default));
        editor.apply();

        assertTrue(rule.getActivity().alreadyLoggedIn());
    }

    @Test
    public void alreadyLoggedIn_HasNotAlreadyLoggedIn_ReturnsFalse() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        rule.launchActivity(intent);
        assertFalse(rule.getActivity().alreadyLoggedIn());
    }

//    @Test
//    public void alreadyLoggedIn_HasAlreadyLoggedIn_StartsNewActivity() {
//
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        LoginActivity loginActivity = rule.launchActivity(intent);
//
//
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.putString(context.getString(R.string.pref_user_nickname_key), context.getString(R.string.pref_user_nickname_default));
//        editor.apply();
//
//        assertTrue(loginActivity.alreadyLoggedIn());
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Activity mapActivity = instrumentation.waitForMonitorWithTimeout(activityMonitor, 50000);
//        assertNotNull(mapActivity);
//        mapActivity.finish();
//
//    }


}
