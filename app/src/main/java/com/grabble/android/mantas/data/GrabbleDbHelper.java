package com.grabble.android.mantas.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grabble.android.mantas.data.GrabbleContract.BagEntry;

/**
 * Created by Mantas on 04/01/2017.
 */

public class GrabbleDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "grabble.db";

    public GrabbleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BAG_TABLE =
            "CREATE TABLE " + BagEntry.TABLE_NAME + " (" +
            BagEntry._ID + " INTEGER PRIMARY KEY," +
            BagEntry.COLUMN_NAME_LETTER + " TEXT NOT NULL," +
            BagEntry.COLUMN_NAME_LATITUDE + " REAL NOT NULL," +
            BagEntry.COLUMN_NAME_LONGITUDE + " REAL NOT NULL," +
            BagEntry.COLUMN_NAME_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            " );";


        db.execSQL(SQL_CREATE_BAG_TABLE);
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
                " WHERE DATE(" + BagEntry.COLUMN_NAME_DATE + ")" +
                " >= DATE('now', 'start of day')";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}
