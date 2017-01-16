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
        public static final String COLUMN_NAME_COLLECTION_DATE = "collection_date";
        public static final String COLUMN_NAME_USAGE_DATE = "usage_date";
    }

    public static final class DictionaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "dictionary";

        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_TIMES_COLLECTED = "times_collected";
    }

    public static final class AchievementsEntry implements BaseColumns {
        public static final String TABLE_NAME = "achievements";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_ID = "image_id";
        public static final String COLUMN_UNLOCKED = "unlocked";
    }
}
