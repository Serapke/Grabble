package com.grabble.android.mantas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grabble.android.mantas.Achievement;
import com.grabble.android.mantas.LetterPointValues;
import com.grabble.android.mantas.R;
import com.grabble.android.mantas.data.GrabbleContract.BagEntry;
import com.grabble.android.mantas.data.GrabbleContract.DictionaryEntry;
import com.grabble.android.mantas.data.GrabbleContract.AchievementsEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Mantas on 04/01/2017.
 */

public class GrabbleDbHelper extends SQLiteOpenHelper {

    public static final String TAG = GrabbleDbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "grabble.db";

    private Context context;

    public GrabbleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BAG_TABLE =
            "CREATE TABLE " + BagEntry.TABLE_NAME + " (" +
            BagEntry._ID + " INTEGER PRIMARY KEY," +
            BagEntry.COLUMN_NAME_LETTER + " TEXT NOT NULL," +
            BagEntry.COLUMN_NAME_LATITUDE + " REAL NOT NULL," +
            BagEntry.COLUMN_NAME_LONGITUDE + " REAL NOT NULL," +
            BagEntry.COLUMN_NAME_COLLECTION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            BagEntry.COLUMN_NAME_USAGE_DATE + " TIMESTAMP" +
            ");";

        final String SQL_CREATE_DICTIONARY_TABLE =
            "CREATE TABLE " + DictionaryEntry.TABLE_NAME + " (" +
            DictionaryEntry._ID + " INTEGER PRIMARY KEY," +
            DictionaryEntry.COLUMN_WORD + " TEXT NOT NULL," +
            DictionaryEntry.COLUMN_SCORE + " INTEGER NOT NULL," +
            DictionaryEntry.COLUMN_TIMES_COLLECTED + " INTEGER DEFAULT 0" +
            ");";

        final String SQL_CREATE_ACHIEVEMENTS_TABLE =
            "CREATE TABLE " + AchievementsEntry.TABLE_NAME + " (" +
            AchievementsEntry._ID + " INTEGER PRIMARY KEY," +
            AchievementsEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            AchievementsEntry.COLUMN_IMAGE_ID + " INTEGER NOT NULL," +
            AchievementsEntry.COLUMN_UNLOCKED + " INTEGER DEFAULT 0" +
            ");";

        db.execSQL(SQL_CREATE_BAG_TABLE);

        db.execSQL(SQL_CREATE_DICTIONARY_TABLE);
        initializeDictionary(db);

        db.execSQL(SQL_CREATE_ACHIEVEMENTS_TABLE);
        initializeAchievements(db);
    }

    /**
     *  Populates achievements table with achievements and corresponding icons.
     *  Done on installation of the application.
     */
    private void initializeAchievements(SQLiteDatabase db) {
        HashMap<String, Integer> achievements = new HashMap<>();
        achievements.put(context.getString(R.string.achievement_same_word), R.drawable.achievement_same_word);
        achievements.put(context.getString(R.string.achievement_100_words), R.drawable.achievement_100_words);
        achievements.put(context.getString(R.string.achievement_word_score_over_70), R.drawable.achievement_word_score_over_70);

        achievements.put(context.getString(R.string.achievement_letter_near_starbucks), R.drawable.achievement_letter_near_starbucks);
        achievements.put(context.getString(R.string.achievement_letter_near_forrest_hill), R.drawable.achievement_letter_near_forrest_hill);
        achievements.put(context.getString(R.string.achievement_letter_near_lidl), R.drawable.achievement_letter_near_lidl);
        achievements.put(context.getString(R.string.achievement_letter_near_library), R.drawable.achievement_letter_near_library);
        achievements.put(context.getString(R.string.achievement_letter_near_teviot), R.drawable.achievement_letter_near_teviot);
        achievements.put(context.getString(R.string.achievement_letter_near_appleton_tower), R.drawable.achievement_letter_near_appleton_tower);

        achievements.put(context.getString(R.string.achievement_student), R.drawable.achievement_student);
        achievements.put(context.getString(R.string.achievement_weekend), R.drawable.achievement_weekend);
        achievements.put(context.getString(R.string.achievement_holiday), R.drawable.achievement_holiday);

        achievements.put(context.getString(R.string.achievement_first), R.drawable.achievement_first);
        achievements.put(context.getString(R.string.achievement_top_10), R.drawable.achievement_top_10);

        achievements.put(context.getString(R.string.achievement_alphabet), R.drawable.achievement_alphabet);
        achievements.put(context.getString(R.string.achievement_night), R.drawable.achievement_night);

        achievements.put(context.getString(R.string.achievement_easter), R.drawable.achievement_easter);
        achievements.put(context.getString(R.string.achievement_christmas), R.drawable.achievement_christmas);

        Iterator it = achievements.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = (Map.Entry) it.next();
            ContentValues values = new ContentValues();
            values.put(AchievementsEntry.COLUMN_TITLE, pair.getKey());
            values.put(AchievementsEntry.COLUMN_IMAGE_ID, pair.getValue());
            db.insert(AchievementsEntry.TABLE_NAME, null, values);
        }
    }

    /**
     *  Populate Dictionary table with dictionary words and their values
     */
    private void initializeDictionary(SQLiteDatabase db) {
        Scanner s;
        try {
            s = new Scanner(context.getAssets().open(context.getString(R.string.dictionary_filename)));
            while (s.hasNext()) {
                String word  = s.next().toUpperCase();
                ContentValues values = new ContentValues();
                values.put(DictionaryEntry.COLUMN_WORD, word);
                values.put(DictionaryEntry.COLUMN_SCORE, getWordScore(word));

                db.insert(DictionaryEntry.TABLE_NAME, null, values);
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Calculates the score for a given word depending on the values copied from course
     *  description.
     */
    private Integer getWordScore(String word) {
        Integer sum = 0;
        for (Character c : word.toCharArray()) {
            sum += LetterPointValues.getValueByLetter(c);
        }
        return sum;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        final String SQL_DROP_BAG_TABLE =
            "DROP TABLE IF EXISTS " + BagEntry.TABLE_NAME;

        db.execSQL(SQL_DROP_BAG_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     *  Gets all distinct letters from bag table with corresponding number of appearances
     *  in the table as count.
     */
    public Cursor getAllLetterCountsInBag(SQLiteDatabase db) {
        String query =
                "SELECT " + BagEntry._ID + ", " +
                BagEntry.COLUMN_NAME_LETTER + ", " +
                "count(" + BagEntry.COLUMN_NAME_LETTER + ") AS count " +
                "FROM " + BagEntry.TABLE_NAME +
                " WHERE " + BagEntry.COLUMN_NAME_USAGE_DATE + " IS NULL" +
                " GROUP BY " + BagEntry.COLUMN_NAME_LETTER + ";";

        return db.rawQuery(query, null);
    }

    /**
     *  Gets the number of distinct letters in the bag table.
     *
     *  Max result = 26 (the user has all alphabet in the bag)
     */
    public Integer getDistinctLettersCount(SQLiteDatabase db) {
        Integer result;
        String selection = BagEntry.COLUMN_NAME_USAGE_DATE + " IS NULL";
        Cursor cursor = db.query(
                BagEntry.TABLE_NAME,
                null,
                selection,
                null,
                BagEntry.COLUMN_NAME_LETTER,
                null,
                null
        );
        result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     *  Gets the number of letters (not distinct) in the bag.
     */
    public Integer getLettersCount(SQLiteDatabase db) {
        Integer result;
        String selection = BagEntry.COLUMN_NAME_USAGE_DATE + " IS NULL";
        Cursor cursor = db.query(
                BagEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
        );
        result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     *  Gets the letters collected today from 12PM.
     */
    public Cursor getLettersCollectedToday(SQLiteDatabase db) {
        String query =
                "SELECT * FROM " + BagEntry.TABLE_NAME +
                " WHERE DATE(" + BagEntry.COLUMN_NAME_COLLECTION_DATE + ")" +
                " >= DATE('now', 'start of day');";

        return db.rawQuery(query, null);
    }

    public Long getFirstUnusedLetterID(SQLiteDatabase db, String letter) {
        String selection =
                BagEntry.COLUMN_NAME_LETTER + " = ? AND " +
                BagEntry.COLUMN_NAME_USAGE_DATE + " IS NULL";
        String[] selectionArgs = { letter };

        Cursor cursor = db.query(
                BagEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        return cursor.getLong(cursor.getColumnIndex(BagEntry._ID));
    }

    /**
     *  Gets the selected word from the dictionary table.
     *
     *  If there is more than one, returns all.
     */
    public Cursor getWord(SQLiteDatabase db, String word) {
        String selection = DictionaryEntry.COLUMN_WORD + " = ?";
        String[] selectionArgs = { word };

        return db.query(DictionaryEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    /**
     *  Checks if at least one instance of the selected word is in the dictionary table.
     *
     *  Can be multiple identical words because of alternating cases (some words start with
     *  lowercase letter, some with uppercase)
     */
    public boolean isValidWord(SQLiteDatabase db, String word) {
        Cursor cursor = getWord(db, word);

        Log.d(TAG, "Cursor count: " + cursor.getCount());
        return cursor.getCount() > 0;
    }

    /**
     *  Gets the number of times a selected words has been collected.
     *
     *  If the word is found in multiple places in the dictionary table, checks only
     *  the first one (explanation in documentation).
     */
    public Integer getNumberOfTimesWordWasCollected(SQLiteDatabase db, String word) {
        Cursor cursor = getWord(db, word);
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(DictionaryEntry.COLUMN_TIMES_COLLECTED));
    }

    /**
     *  Gets the selected word's score.
     */
    public Integer getWordScore(SQLiteDatabase db, String word) {
        Cursor cursor = getWord(db, word);
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(DictionaryEntry.COLUMN_SCORE));
    }

    public Cursor getCollectedWords(SQLiteDatabase db) {
        String selection = DictionaryEntry.COLUMN_TIMES_COLLECTED + " > ?";
        String[] selectionArgs = { "0" };

        return db.query(DictionaryEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    public Integer getCollectedWordsCount(SQLiteDatabase db) {
        String query =
                "SELECT sum(" + DictionaryEntry.COLUMN_TIMES_COLLECTED + ") AS count " +
                "FROM " + DictionaryEntry.TABLE_NAME + ";";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(context.getString(R.string.column_count)));
    }

    /**
     *  Gets the word with the highest score.
     */
    public Cursor getBestWord(SQLiteDatabase db) {
        String query =
                "SELECT * FROM " + DictionaryEntry.TABLE_NAME +
                " WHERE " + DictionaryEntry.COLUMN_TIMES_COLLECTED + " > ? " +
                " ORDER BY " + DictionaryEntry.COLUMN_SCORE + " DESC;";
        String[] selectionArgs = { "0" };

        return db.rawQuery(query, selectionArgs);
    }

    /**
     *  Gets the achievement with selected title.
     *
     *  Returns Achievement object.
     */
    public Achievement getAchievement(SQLiteDatabase db, String title) {
        String selection = AchievementsEntry.COLUMN_TITLE + " = ?";
        String[] selectionArgs = { title };
        Cursor cursor = db.query(
                AchievementsEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        Integer imageId = cursor.getInt(cursor.getColumnIndex(AchievementsEntry.COLUMN_IMAGE_ID));
        Integer unlocked = cursor.getInt(cursor.getColumnIndex(AchievementsEntry.COLUMN_UNLOCKED));

        return new Achievement(title, imageId, unlocked==1);
    }

    /**
     *  Unlocks the achievement with the selected title.
     */
    public void unlockAchievement(SQLiteDatabase db, String title) {
        ContentValues values = new ContentValues();
        values.put(AchievementsEntry.COLUMN_UNLOCKED, 1);

        String selection = AchievementsEntry.COLUMN_TITLE + " = ?";
        String[] selectionArgs = { title };

        db.update(
                AchievementsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public List<Achievement> getUnlockedAchievements(SQLiteDatabase db) {
        List<Achievement> result = new ArrayList<>();
        String selection = AchievementsEntry.COLUMN_UNLOCKED + " = 1";

        Cursor cursor = db.query(
                AchievementsEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(AchievementsEntry.COLUMN_TITLE));
            Integer imageId = cursor.getInt(cursor.getColumnIndex(AchievementsEntry.COLUMN_IMAGE_ID));
            result.add(new Achievement(title, imageId, true));
        }
        return result;
    }

    /**
     *  Gets the count of all achievements (both locked and unlocked)
     */
    public Long getAchievementsCount(SQLiteDatabase db) {
        return DatabaseUtils.queryNumEntries(db, AchievementsEntry.TABLE_NAME);
    }

    /**
     *  Gets all the letters collected at night (between 22PM and 6AM)
     */
    public Integer getLettersCollectedAtNightCount(SQLiteDatabase db) {
        String query =
                "SELECT * FROM " + BagEntry.TABLE_NAME +
                " WHERE TIME(" + BagEntry.COLUMN_NAME_COLLECTION_DATE + ")" +
                " > TIME('22:00', 'localtime') OR TIME(" + BagEntry.COLUMN_NAME_COLLECTION_DATE + ")" +
                " < TIME('06:00', 'localtime');";
        Cursor cursor = db.rawQuery(query, null);

        return cursor.getCount();
    }
}
