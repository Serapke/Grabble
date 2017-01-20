package com.grabble.android.mantas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Mantas on 19/01/2017.
 */

@RunWith(AndroidJUnit4.class)
public class BagActivityInstrumentedTest {

    private BagActivity bagActivity;
    private Context context;

    private static final String WORD = "WORD";
    private static final String WORD_WITHOUT_LAST_CHAR = "WOR";

    @Rule
    public ActivityTestRule<BagActivity> bagActivityActivityTestRule = new ActivityTestRule<BagActivity>(
        BagActivity.class);

    @Before
    public void setup() {
        bagActivity = bagActivityActivityTestRule.getActivity();
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void deleteCharacterTest() {
        onView(withId(R.id.collect_word)).perform(typeText(WORD), closeSoftKeyboard());
        onView(withId(R.id.delete_character)).perform(click());

        onView(withId(R.id.collect_word)).check(matches(withText(WORD_WITHOUT_LAST_CHAR)));
    }

    @Test
    public void addLetterToWord() {
        BagActivity.BagFragment bagFragment = new BagActivity.BagFragment();
        bagFragment.editText = new EditText(context);
        bagFragment.editText.setText(WORD_WITHOUT_LAST_CHAR);
        bagFragment.addLetterToWord("D");
        assertEquals(WORD, bagFragment.editText.getText().toString());
    }
}