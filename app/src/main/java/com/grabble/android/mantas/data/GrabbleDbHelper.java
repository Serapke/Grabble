package com.grabble.android.mantas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grabble.android.mantas.LetterPointValues;
import com.grabble.android.mantas.R;
import com.grabble.android.mantas.data.GrabbleContract.BagEntry;
import com.grabble.android.mantas.data.GrabbleContract.DictionaryEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
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
            " );";

        final String SQL_CREATE_DICTIONARY_TABLE =
            "CREATE TABLE " + DictionaryEntry.TABLE_NAME + " (" +
            DictionaryEntry._ID + " INTEGER PRIMARY KEY," +
            DictionaryEntry.COLUMN_WORD + " TEXT NOT NULL," +
            DictionaryEntry.COLUMN_SCORE + " INTEGER NOT NULL," +
            DictionaryEntry.COLUMN_TIMES_COLLECTED + " INTEGER DEFAULT 0" +
            " );";

        db.execSQL(SQL_CREATE_BAG_TABLE);

        db.execSQL(SQL_CREATE_DICTIONARY_TABLE);
        initializeDictionary(db);
    }

    /**
     *  Populate Dictionary table with dictionary words and their values
     */
    private void initializeDictionary(SQLiteDatabase db) {
        Scanner s;
        try {
            s = new Scanner(context.getAssets().open(String.format(context.getString(R.string.dictionary_filename))));
            while (s.hasNext()) {
                String word  = s.next().toUpperCase();
                ContentValues values = new ContentValues();
                values.put(DictionaryEntry.COLUMN_WORD, word);
                values.put(DictionaryEntry.COLUMN_SCORE, getWordScore(word));

                db.insert(DictionaryEntry.TABLE_NAME, null, values);
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public Cursor getAllLetterCountsInBag(SQLiteDatabase db) {
        String query =
                "SELECT " + BagEntry._ID + ", " +
                BagEntry.COLUMN_NAME_LETTER + ", " +
                "count(" + BagEntry.COLUMN_NAME_LETTER + ") AS count " +
                "FROM " + BagEntry.TABLE_NAME +
                " WHERE " + BagEntry.COLUMN_NAME_USAGE_DATE + " IS NULL" +
                " GROUP BY " + BagEntry.COLUMN_NAME_LETTER;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public long getLettersCount(SQLiteDatabase db) {
        long count = DatabaseUtils.queryNumEntries(db, BagEntry.TABLE_NAME);
        return count;
    }

    public Cursor getLettersCollectedToday(SQLiteDatabase db) {
        String query =
                "SELECT * FROM " + BagEntry.TABLE_NAME +
                " WHERE DATE(" + BagEntry.COLUMN_NAME_COLLECTION_DATE + ")" +
                " >= DATE('now', 'start of day')";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
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


    public Cursor getWord(SQLiteDatabase db, String word) {
        String selection = DictionaryEntry.COLUMN_WORD + " = ?";
        String[] selectionArgs = { word };

        Cursor cursor = db.query(
                DictionaryEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    public boolean isValidWord(SQLiteDatabase db, String word) {
        Cursor cursor = getWord(db, word);

        Log.d(TAG, "Cursor count: " + cursor.getCount());
        return cursor.getCount() > 0;
    }

    public Integer getNumberOfTimesWordWasCollected(SQLiteDatabase db, String word) {
        Cursor cursor = getWord(db, word);
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(DictionaryEntry.COLUMN_TIMES_COLLECTED));
    }

    public Integer getWordScore(SQLiteDatabase db, String word) {
        Cursor cursor = getWord(db, word);
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(DictionaryEntry.COLUMN_SCORE));
    }

    public Cursor getCollectedWords(SQLiteDatabase db) {
        String selection = DictionaryEntry.COLUMN_TIMES_COLLECTED + " > ?";
        String[] selectionArgs = { "0" };

        Cursor cursor = db.query(
                DictionaryEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }
}
