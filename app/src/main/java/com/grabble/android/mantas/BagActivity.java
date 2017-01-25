package com.grabble.android.mantas;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.grabble.android.mantas.data.GrabbleDbHelper;
import com.grabble.android.mantas.data.GrabbleContract.BagEntry;
import com.grabble.android.mantas.data.GrabbleContract.DictionaryEntry;
import com.grabble.android.mantas.utils.AchievementsUtil;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 *  Shows the user his/hers collected letters and provides
 *  the functionality to collect a word.
 */
public class BagActivity extends FragmentActivity {

    public static final String TAG = BagActivity.class.getSimpleName();

    private static SQLiteDatabase db;
    private static GrabbleDbHelper dbHelper;
    private static AchievementsUtil achv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_bag, new BagFragment())
                    .commit();
        }
        dbHelper = new GrabbleDbHelper(this);
        db = dbHelper.getWritableDatabase();
        achv = new AchievementsUtil(this, db);
    }

    protected  void onStop() {
        super.onStop();
        db.close();
    }

    public static class BagFragment extends Fragment {

        SimpleCursorAdapter lettersAdapter;
        View view;
        EditText editText;

        HashMap<String, Integer> usedLettersCounts = initializeUserLettersCounts();

        public BagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_bag, container, false);

            editText = (EditText) view.findViewById(R.id.collect_word);
            editText.setTextIsSelectable(true);

            GridView bagView = (GridView) view.findViewById(R.id.letters);
            lettersAdapter = new SimpleCursorAdapter(getContext(),
                    R.layout.letter_layout,
                    dbHelper.getAllLetterCountsInBag(db),
                    new String[] { BagEntry.COLUMN_NAME_LETTER, getString(R.string.column_count) },
                    new int[] { R.id.letter_value, R.id.letter_count },
                    0
            );
            bagView.setAdapter(lettersAdapter);
            setOnLetterClickListener(bagView);

            ImageButton deleteCharacterButton = (ImageButton) view.findViewById(R.id.delete_character);
            deleteCharacterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCharacter();
                }
            });

            Button submitButton = (Button) view.findViewById(R.id.submit_word);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitWord(editText.getText().toString());
                }
            });

            return view;
        }

        /**
         *  On letter clicked, append the letter to the end of the word in editText
         *
         *  The letter is not appended if:
         *      - Word in editText already consists of 7 letters
         *      - User doesn't have enough individual letters in the bag (already
         *        used all individual letters to build an existing word in editText
         */
        public void setOnLetterClickListener(GridView bagView) {
            bagView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int i, long id) {
                    // Checks if the editText already consists of 7 letter word
                    if (editText.getText().length() >= 7) {
                        Toast.makeText(
                                getContext(),
                                getString(R.string.toast_too_many_letters),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Cursor cursor = (Cursor) lettersAdapter.getItem(i);
                    String letter = cursor.getString(cursor.getColumnIndex(BagEntry.COLUMN_NAME_LETTER));
                    Integer count = cursor.getInt(cursor.getColumnIndex(getString(R.string.column_count)));

                    // Checks if user has enough individual letters to compose the word
                    if (usedLettersCounts.get(letter) >= count) {
                        Toast.makeText(
                                getContext(),
                                getString(R.string.toast_not_enough_individual_letters) + letter + "'s",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    appendLetterToWord(letter);
                }
            });
        }

        public void appendLetterToWord(String letter) {
            editText.setText(editText.getText() + letter);
            usedLettersCounts.put(letter, usedLettersCounts.get(letter)+1);
        }

        /**
         *  Initialize the dictionary for user letters used to build a word in editText.
         *
         *  Used in order to prevent user using more letters than he/she has.
         */
        private HashMap<String, Integer> initializeUserLettersCounts() {
            HashMap<String, Integer> map = new HashMap<>();
            for (char c = 'A'; c <= 'Z'; c++) {
                map.put(Character.toString(c), 0);
            }
            return map;
        }

        /**
         *  Submits the word for review
         *
         *  If the word does not consist of 7 letters,
         *  notifies the user that only 7 letter words are acceptable
         *  If the word is not in dictionary,
         *  notifies the user.
         *  Else:
         *      1) get the submitted word score
         *      2) get the number of times the word was collected
         *      3) update user score
         *      4) remove letters used for the word from the bag
         *      5) update the number of times the word was collected
         *      6) update user place (through remote server database)
         *      7) check if unlocked any achievements
         *      8) prepare view for another submission
         */
        private void submitWord(String word) {
            Integer wordScore;
            Integer wordTimesCollected;
            Integer userScore;

            if (editText.getText().length() != 7) {
                Toast.makeText(
                        getContext(),
                        getString(R.string.toast_not_enough_letters),
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Trying to submit a word: " + word);
                if (dbHelper.isValidWord(db, word)) {
                    wordScore = dbHelper.getWordScore(db, word);
                    wordTimesCollected = dbHelper.getNumberOfTimesWordWasCollected(db, word)+1;
                    userScore = updateScore(wordScore);

                    removeLettersFromBag(word);
                    updateWordTimesCollected(word, wordTimesCollected);
                    updatePlace(userScore);
                    achv.checkWordAchievements(word, wordScore, wordTimesCollected);

                    prepareWordCollectedView(word);

                } else {
                    Toast.makeText(
                            getContext(),
                            getString(R.string.toast_no_such_word),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         *  Updates the letters adapter by getting unused letters from the updated database table,
         *  clears the editText and notifies the user about successful word collection
         */
        private void prepareWordCollectedView(String word) {
            lettersAdapter.changeCursor(dbHelper.getAllLetterCountsInBag(db));
            editText.setText("");
            Toast.makeText(
                    getContext(),
                    getString(R.string.toast_successful_word_collection) + " " + word,
                    Toast.LENGTH_SHORT).show();
        }

        /**
         *  Sends the server updated user score and gets the updated user place
         */
        private void updatePlace(Integer score) {
            UserUpdatePlaceTask userUpdatePlaceTask = new UserUpdatePlaceTask(getContext(), score);
            userUpdatePlaceTask.execute();
        }

        /**
         *  Updates the user score by adding score of the collected word
         */
        protected Integer updateScore(Integer wordScore) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Integer oldScore = sharedPrefs.getInt(
                    getString(R.string.pref_user_score_key),
                    0);
            Integer newScore = oldScore + wordScore;
            editor.putInt(getString(R.string.pref_user_score_key), newScore);
            editor.apply();
            return newScore;
        }

        /**
         *  Update the number of times the word was collected
         */
        private void updateWordTimesCollected(String word, Integer wordTimesCollected) {
            ContentValues values = new ContentValues();
            values.put(
                    DictionaryEntry.COLUMN_TIMES_COLLECTED,
                    wordTimesCollected
            );

            String selection = DictionaryEntry.COLUMN_WORD + " = ?";
            String[] selectionArgs = { word };

            db.update(
                    DictionaryEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
        }

        /**
         *  For every letter in a composed word, add usage_date indicating that
         *  the letter has been used.
         */
        private void removeLettersFromBag(String word) {
            for (char c : word.toCharArray()) {
                String id = Long.toString(dbHelper.getFirstUnusedLetterID(db, Character.toString(c)));

                ContentValues values = new ContentValues();
                values.put(BagEntry.COLUMN_NAME_USAGE_DATE, getDateTime());

                String selection = BagEntry._ID  + " = ?";
                String[] selectionArgs = { id };

                db.update(
                    BagEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
                );
            }
        }

        /**
         *  Returns the current time in specified format (yyyy-MM-dd HH:mm:ss) as a string
         */
        private String getDateTime() {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    getString(R.string.date_format), Locale.getDefault());
            Date date = new Date();
            return dateFormat.format(date);
        }

        /**
         *  If editText is not empty, deletes a single character from editText and
         *  updates the usedLettersCount map by reducing the count of deleted letter
         */
        private void deleteCharacter() {
            String text = editText.getText().toString();
            if (text.isEmpty()) return;
            editText.setText(text.substring(0, text.length()-1));
            String deletedCharacter = text.substring(text.length()-1);
            if (usedLettersCounts.containsKey(deletedCharacter)) {
                usedLettersCounts.put(deletedCharacter, usedLettersCounts.get(deletedCharacter) - 1);
            }
        }
    }
}
