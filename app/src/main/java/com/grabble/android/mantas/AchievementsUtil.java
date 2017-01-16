package com.grabble.android.mantas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.grabble.android.mantas.data.GrabbleDbHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Mantas on 15/01/2017.
 */

public class AchievementsUtil {

    private static final String TAG = AchievementsUtil.class.getSimpleName();

    private Context context;
    private static GrabbleDbHelper dbHelper;
    private static SQLiteDatabase db;

    private static final LatLng STARBUCKS_POSITION = new LatLng(55.944032, -3.191879);
    private static final LatLng FORREST_HILL_POSITION = new LatLng(55.946030, -3.192261);
    private static final LatLng LIDL_POSITION = new LatLng(55.945609, -3.184427);
    private static final LatLng LIBRARY_POSITION = new LatLng(55.942722, -3.188938);
    private static final LatLng TEVIOT_POSITION = new LatLng(55.944941, -3.188705);
    private static final LatLng APPLETON_TOWER = new LatLng(55.944526, -3.186866);

    private static final Calendar EASTER_DATE = new GregorianCalendar(2017, Calendar.APRIL, 16);
    private static final Calendar CHRISTMAS_DATE = new GregorianCalendar(2017, Calendar.DECEMBER, 25);

    private static final String STUDENT_WORD = "STUDENT";
    private static final String WEEKEND_WORD = "WEEKEND";
    private static final String HOLIDAY_WORD = "HOLIDAY";

    private static final Double DISTANCE_FROM_LANDMARK = 50.00;
    private static final Integer NIGHT_LETTERS_LIMIT = 20;
    private static final Integer ALPHABET_SIZE = 26;

    public AchievementsUtil(Context context) {
        this.context = context;
        dbHelper = new GrabbleDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void checkSameWordAchievement(Integer timesCollected) {
        String achievementTitle = context.getString(R.string.achievement_same_word);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && timesCollected > 1) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkChristmasAchievement() {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        checkChristmasAchievement(calendar);
    }

    public void checkChristmasAchievement(Calendar date) {
        String achievementTitle = context.getString(R.string.achievement_christmas);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && date.get(Calendar.DAY_OF_YEAR) == CHRISTMAS_DATE.get(Calendar.DAY_OF_YEAR)) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterNearStarbucksAchievement(LatLng letterPosition) {
        String achievementTitle = context.getString(R.string.achievement_letter_near_starbucks);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        double distance = SphericalUtil.computeDistanceBetween(letterPosition, STARBUCKS_POSITION);
        Log.d(TAG, "Distance between Starbucks and letter: " + distance);
        if (achievement.isLocked() && distance < DISTANCE_FROM_LANDMARK) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterNearForrestHillAchievement(LatLng letterPosition) {
        String achievementTitle = context.getString(R.string.achievement_letter_near_forrest_hill);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        double distance = SphericalUtil.computeDistanceBetween(letterPosition, FORREST_HILL_POSITION);
        Log.d(TAG, "Distance between Forrest Hill and letter: " + distance);
        if (achievement.isLocked() && distance < DISTANCE_FROM_LANDMARK) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterNearLidlAchievement(LatLng letterPosition) {
        String achievementTitle = context.getString(R.string.achievement_letter_near_lidl);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        double distance = SphericalUtil.computeDistanceBetween(letterPosition, LIDL_POSITION);
        Log.d(TAG, "Distance between Lidl and letter: " + distance);
        if (achievement.isLocked() && distance < DISTANCE_FROM_LANDMARK) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterNearMainLibraryAchievement(LatLng letterPosition) {
        String achievementTitle = context.getString(R.string.achievement_letter_near_library);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        double distance = SphericalUtil.computeDistanceBetween(letterPosition, LIBRARY_POSITION);
        Log.d(TAG, "Distance between Main Library and letter: " + distance);
        if (achievement.isLocked() && distance < DISTANCE_FROM_LANDMARK) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterNearTeviotAchievement(LatLng letterPosition) {
        String achievementTitle = context.getString(R.string.achievement_letter_near_teviot);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        double distance = SphericalUtil.computeDistanceBetween(letterPosition, TEVIOT_POSITION);
        Log.d(TAG, "Distance between Teviot and letter: " + distance);
        if (achievement.isLocked() && distance < DISTANCE_FROM_LANDMARK) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterNearAppletonTowerAchievement(LatLng letterPosition) {
        String achievementTitle = context.getString(R.string.achievement_letter_near_appleton_tower);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        double distance = SphericalUtil.computeDistanceBetween(letterPosition, APPLETON_TOWER);
        Log.d(TAG, "Distance between Appleton Tower and letter: " + distance);
        if (achievement.isLocked() && distance < DISTANCE_FROM_LANDMARK) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkEasterAchievement() {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        checkEasterAchievement(calendar);
    }

    public void checkEasterAchievement(Calendar date) {
        String achievementTitle = context.getString(R.string.achievement_easter);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && date.get(Calendar.DAY_OF_YEAR) == EASTER_DATE.get(Calendar.DAY_OF_YEAR)) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkNightAchievement() {
        Integer nightLettersCount = dbHelper.getLettersCollectedAtNightCount(db);
        checkNightAchievement(nightLettersCount);
    }

    protected void checkNightAchievement(Integer nightLettersCount) {
        String achievementTitle = context.getString(R.string.achievement_night);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && nightLettersCount > NIGHT_LETTERS_LIMIT) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkStudentAchievement(String word) {
        String achievementTitle = context.getString(R.string.achievement_student);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && word.equals(STUDENT_WORD)) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkWeekendAchievement(String word) {
        String achievementTitle = context.getString(R.string.achievement_weekend);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && word.equals(WEEKEND_WORD)) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkHolidayAchievement(String word) {
        String achievementTitle = context.getString(R.string.achievement_holiday);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && word.equals(HOLIDAY_WORD)) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void check100WordsAchievement() {
        Integer wordsCollectedCount = dbHelper.getCollectedWordsCount(db);
        check100WordsAchievement(wordsCollectedCount);
    }

    protected void check100WordsAchievement(Integer wordsCollectedCount) {
        String achievementTitle = context.getString(R.string.achievement_100_words);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && wordsCollectedCount >= 100) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkFirstAchievement(Integer place) {
        String achievementTitle = context.getString(R.string.achievement_first);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && place == 1) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkTop10Achievement(Integer place) {
        String achievementTitle = context.getString(R.string.achievement_top_10);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && place <= 10) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkAlphabetAchievement() {
        Integer distinctLettersCount = dbHelper.getDistinctLettersCount(db);
        checkAlphabetAchievement(distinctLettersCount);
    }

    public void checkAlphabetAchievement(Integer count) {
        String achievementTitle = context.getString(R.string.achievement_alphabet);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && count == ALPHABET_SIZE) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkWordScoreOver70Achievement(Integer score) {
        String achievementTitle = context.getString(R.string.achievement_word_score_over_70);
        Achievement achievement = dbHelper.getAchievement(db, achievementTitle);
        if (achievement.isLocked() && score >= 70) {
            dbHelper.unlockAchievement(db, achievementTitle);
        }
    }

    public void checkLetterAchievements(LatLng position) {
        checkLetterNearStarbucksAchievement(position);
        checkLetterNearForrestHillAchievement(position);
        checkLetterNearLidlAchievement(position);
        checkLetterNearMainLibraryAchievement(position);
        checkLetterNearTeviotAchievement(position);
        checkLetterNearAppletonTowerAchievement(position);
        checkNightAchievement();
        checkAlphabetAchievement();
    }

    public void checkWordAchievements(String word, Integer wordScore, Integer wordTimesCollected) {
        checkWordScoreOver70Achievement(wordScore);
        checkSameWordAchievement(wordTimesCollected);
        checkChristmasAchievement();
        checkEasterAchievement();
        checkStudentAchievement(word);
        checkWeekendAchievement(word);
        checkHolidayAchievement(word);
        check100WordsAchievement();
    }

    public void checkLeaderboardAchievements(Integer place) {
        checkTop10Achievement(place);
        checkFirstAchievement(place);
    }
}
