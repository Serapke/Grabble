package com.grabble.android.mantas;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Mantas on 18/01/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MapsActivityInstrumentedTest {

    Instrumentation.ActivityMonitor am;

    @Rule
    public ActivityTestRule<MapsActivity> mapsActivityActivityTestRule =
            new ActivityTestRule<>(MapsActivity.class);

    @Before
    public void setUp() {

    }

    @Test
    public void onBagButtonClick_StartBagActivity() {
        am = InstrumentationRegistry.getInstrumentation().addMonitor(BagActivity.class.getName(), null, false);

        onView(withId(R.id.bagButton)).perform(click());

        BagActivity mapsActivity = (BagActivity) am.waitForActivityWithTimeout(5000);
        assertNotNull(mapsActivity);
        assertEquals(1, am.getHits());
        assertEquals("BagActivity", mapsActivity.getLocalClassName());

        InstrumentationRegistry.getInstrumentation().removeMonitor(am);
    }

    @Test
    public void onLeaderboardButtonClick_StartBagActivity() {
        am = InstrumentationRegistry.getInstrumentation().addMonitor(LeaderboardActivity.class.getName(), null, false);

        onView(withId(R.id.leaderboardButton)).perform(click());

        LeaderboardActivity leaderboardActivity = (LeaderboardActivity) am.waitForActivityWithTimeout(5000);
        assertNotNull(leaderboardActivity);
        assertEquals(1, am.getHits());
        assertEquals("LeaderboardActivity", leaderboardActivity.getLocalClassName());

        InstrumentationRegistry.getInstrumentation().removeMonitor(am);
    }

    @Test
    public void onUserInfoButtonClick_StartBagActivity() {
        am = InstrumentationRegistry.getInstrumentation().addMonitor(UserInfoActivity.class.getName(), null, false);

        onView(withId(R.id.userinfoButton)).perform(click());

        UserInfoActivity userInfoActivity = (UserInfoActivity) am.waitForActivityWithTimeout(5000);
        assertNotNull(userInfoActivity);
        assertEquals(1, am.getHits());
        assertEquals("UserInfoActivity", userInfoActivity.getLocalClassName());

        InstrumentationRegistry.getInstrumentation().removeMonitor(am);
    }
}
