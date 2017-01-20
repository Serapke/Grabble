package com.grabble.android.mantas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

/**
 * Created by Mantas on 19/01/2017.
 */

@RunWith(AndroidJUnit4.class)
public class FetchLeaderboardTaskInstrumentedTest {

    Context context;
    FetchLeaderboardTask fetchLeaderboardTask;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        fetchLeaderboardTask = new FetchLeaderboardTask(context, new LeaderboardAdapter(context, 0));
        prepareSharedPrefs(context);
    }

    private void prepareSharedPrefs(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.putString(
                context.getString(R.string.pref_number_of_users_key),
                context.getString(R.string.pref_number_of_users_default));
        editor.apply();
    }


    @Test
    public void updateNumberOfUsersPref_UpdatesPrefsCorrectly() {
        fetchLeaderboardTask.updateNumberOfUsersPref(3);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        assertEquals("3", sharedPrefs.getString(context.getString(R.string.pref_number_of_users_key),
                                context.getString(R.string.pref_number_of_users_default)));
    }
}
