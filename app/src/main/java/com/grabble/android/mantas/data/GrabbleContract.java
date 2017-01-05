package com.grabble.android.mantas.data;

import android.provider.BaseColumns;

/**
 * Created by Mantas on 04/01/2017.
 */

/*
 * Defines table and column names for the Grabble database
 */
public class GrabbleContract {
    public static final class BagEntry implements BaseColumns {
        public static final String TABLE_NAME = "bag";

        public static final String COLUMN_NAME_LETTER = "letter";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
