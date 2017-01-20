package com.grabble.android.mantas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.grabble.android.mantas.data.GrabbleDbHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by Mantas on 15/01/2017.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AndroidAchievementsUtilTest {
    Context context;
    AchievementsUtil achv;
    GrabbleDbHelper dbHelper;
    SQLiteDatabase db;

    private static final LatLng NEAR_STARBUCKS_POSITION = new LatLng(55.944039, -3.191871);
    private static final LatLng NEAR_FORREST_HILL_POSITION = new LatLng(55.946039, -3.192269);
    private static final LatLng NEAR_LIDL_POSITION = new LatLng(55.945601, -3.184421);
    private static final LatLng NEAR_LIBRARY_POSITION = new LatLng(55.942729, -3.188931);
    private static final LatLng NEAR_TEVIOT_POSITION = new LatLng(55.944949, -3.188709);
    private static final LatLng NEAR_APPLETON_TOWER = new LatLng(55.944529, -3.186861);

    private static final Calendar EASTER_DATE = new GregorianCalendar(2017, Calendar.APRIL, 16);
    private static final Calendar CHRISTMAS_DATE = new GregorianCalendar(2017, Calendar.DECEMBER, 25);

    private static final Double DISTANCE_FROM_LANDMARK = 50.00;
    private static final Integer NIGHT_LETTERS_LIMIT = 20;
    private static final Integer ALPHABET_SIZE = 26;
    private static final Integer WORDS_COLLECTED_COUNT = 100;
    private static final Integer SCORE = 70;
    private static final Integer FIRST_PLACE = 1;
    private static final Integer TENTH_PLACE = 10;

    private static final String STUDENT_WORD = "STUDENT";
    private static final String WEEKEND_WORD = "WEEKEND";
    private static final String HOLIDAY_WORD = "HOLIDAY";

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        dbHelper = new GrabbleDbHelper(context);
        db = dbHelper.getWritableDatabase();
        achv = new AchievementsUtil(context, db);
    }

    public void testAchievementIsUnlocked(String title) {
        Achievement achievement = dbHelper.getAchievement(db, title);
        assertFalse(achievement.isLocked());
    }

    @Test
    public void testSameWordAchievement() {
        String achievementTitle = context.getString(R.string.achievement_same_word);
        achv.checkSameWordAchievement(2);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testChristmasAchievement() {
        String achievementTitle = context.getString(R.string.achievement_christmas);
        achv.checkChristmasAchievement(CHRISTMAS_DATE);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testEasterAchievement() {
        String achievementTitle = context.getString(R.string.achievement_easter);
        achv.checkEasterAchievement(EASTER_DATE);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testLetterNearStarbucksAchievement() {
        String achievementTitle = context.getString(R.string.achievement_letter_near_starbucks);
        achv.checkLocationAchievements(NEAR_STARBUCKS_POSITION);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testNearForrestHillAchievement() {
        String achievementTitle = context.getString(R.string.achievement_letter_near_forrest_hill);
        achv.checkLocationAchievements(NEAR_FORREST_HILL_POSITION);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testNearLidlAchievement() {
        String achievementTitle = context.getString(R.string.achievement_letter_near_lidl);
        achv.checkLocationAchievements(NEAR_LIDL_POSITION);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testNearMainLibraryAchievement() {
        String achievementTitle = context.getString(R.string.achievement_letter_near_library);
        achv.checkLocationAchievements(NEAR_LIBRARY_POSITION);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testNearTeviotAchievement() {
        String achievementTitle = context.getString(R.string.achievement_letter_near_teviot);
        achv.checkLocationAchievements(NEAR_TEVIOT_POSITION);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testNearAppletonTowerAchievement() {
        String achievementTitle = context.getString(R.string.achievement_letter_near_appleton_tower);
        achv.checkLocationAchievements(NEAR_APPLETON_TOWER);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testNightAchievement() {
        String achievementTitle = context.getString(R.string.achievement_night);
        achv.checkNightAchievement(NIGHT_LETTERS_LIMIT);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testStudentAchievement() {
        String achievementTitle = context.getString(R.string.achievement_student);
        achv.checkStudentAchievement(STUDENT_WORD);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testWeekendAchievement() {
        String achievementTitle = context.getString(R.string.achievement_weekend);
        achv.checkWeekendAchievement(WEEKEND_WORD);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testHolidayAchievement() {
        String achievementTitle = context.getString(R.string.achievement_holiday);
        achv.checkHolidayAchievement(HOLIDAY_WORD);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void test100WordsAchievement() {
        String achievementTitle = context.getString(R.string.achievement_100_words);
        achv.check100WordsAchievement(WORDS_COLLECTED_COUNT);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testFirstAchievement() {
        String achievementTitle = context.getString(R.string.achievement_first);
        achv.checkFirstPlaceAchievement(FIRST_PLACE);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testTop10Achievement() {
        String achievementTitle = context.getString(R.string.achievement_top_10);
        achv.checkTop10Achievement(TENTH_PLACE);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testAlphabetAchievement() {
        String achievementTitle = context.getString(R.string.achievement_alphabet);
        achv.checkAlphabetAchievement(ALPHABET_SIZE);
        testAchievementIsUnlocked(achievementTitle);
    }

    @Test
    public void testWordScoreOver70Achievement() {
        String achievementTitle = context.getString(R.string.achievement_word_score_over_70);
        achv.checkWordScoreOver70Achievement(SCORE);
        testAchievementIsUnlocked(achievementTitle);
    }
}