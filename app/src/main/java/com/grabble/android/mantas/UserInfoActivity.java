package com.grabble.android.mantas;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grabble.android.mantas.data.GrabbleContract.DictionaryEntry;
import com.grabble.android.mantas.data.GrabbleDbHelper;

public class UserInfoActivity extends AppCompatActivity {
    UserInfoPagerAdapter userInfoPagerAdapter;

    ViewPager viewPager;

    private static SQLiteDatabase db;
    private static GrabbleDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userInfoPagerAdapter = new UserInfoPagerAdapter(getSupportFragmentManager(), this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(userInfoPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        dbHelper = new GrabbleDbHelper(this);
        db = dbHelper.getReadableDatabase();
    }

    protected  void onStop() {
        super.onStop();
        db.close();
    }

    public class UserInfoPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 3;

        private String tabTitles[] = new String[] { "User Stats", "Achievements", "Words"};

        private Context context;

        public UserInfoPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Fragment.instantiate(context, UserStatsFragment.class.getName());
                case 1:
                    return Fragment.instantiate(context, AchievementsFragment.class.getName());
                case 2:
                    return Fragment.instantiate(context, WordsCompletedFragment.class.getName());
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    public static class UserStatsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String nickname = sharedPrefs.getString(
                    getString(R.string.pref_user_nickname_key),
                    getString(R.string.pref_user_nickname_default));
            String placeText = getPlaceText(sharedPrefs);
            Integer collectedWordsCount = dbHelper.getCollectedWordsCount(db);
            String bestWordText = getBestWordText();
            String letterInBagCountText = getLetterInBagCount();

            View rootView = inflater.inflate(R.layout.fragment_section_user_stats, container, false);
            ((TextView) rootView.findViewById(R.id.nickname)).setText(nickname);
            ((TextView) rootView.findViewById(R.id.place)).setText(placeText);
            ((TextView) rootView.findViewById(R.id.wordCount)).setText(collectedWordsCount.toString());
            ((TextView) rootView.findViewById(R.id.bestWord)).setText(bestWordText);
            ((TextView) rootView.findViewById(R.id.letterInBagCount)).setText(letterInBagCountText);

            return rootView;
        }

        private String getLetterInBagCount() {
            long lettersCount = dbHelper.getLettersCount(db);
            return String.format("%d letters", lettersCount);
        }

        private String getBestWordText() {
            Cursor cursor = dbHelper.getBestWord(db);
            cursor.moveToFirst();
            String bestWord = cursor.getString(cursor.getColumnIndex(DictionaryEntry.COLUMN_WORD));
            Integer bestWordScore = cursor.getInt(cursor.getColumnIndex(DictionaryEntry.COLUMN_SCORE));
            cursor.close();
            return String.format("%s (%d)", bestWord, bestWordScore);
        }

        private String getPlaceText(SharedPreferences sharedPrefs) {
            String place = sharedPrefs.getString(
                    getString(R.string.pref_user_place_key),
                    getString(R.string.pref_user_place_default));
            String numberOfUsers = sharedPrefs.getString(
                    getString(R.string.pref_number_of_users_key),
                    getString(R.string.pref_number_of_users_default)
            );
            return String.format("%s / %s participants", place, numberOfUsers);
        }

    }

    public static class AchievementsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_section_achievements, container, false);

            GridView gridView = (GridView) view.findViewById(R.id.achievements);
            gridView.setAdapter(new AchievementsAdapter(
                    getContext(),
                    dbHelper.getUnlockedAchievements(db),
                    dbHelper.getAchievementsCount(db)));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

    }

    public static class WordsCompletedFragment extends Fragment {

        private SimpleCursorAdapter wordsAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_section_words, container, false);

            wordsAdapter = new SimpleCursorAdapter(
                    getContext(),
                    R.layout.list_item_word,
                    dbHelper.getCollectedWords(db),
                    new String[] {
                            DictionaryEntry.COLUMN_WORD,
                            DictionaryEntry.COLUMN_SCORE,
                            DictionaryEntry.COLUMN_TIMES_COLLECTED },
                    new int[] {
                            R.id.word,
                            R.id.score,
                            R.id.count },
                    0
            );

            ListView listView = (ListView) view.findViewById(R.id.words);
            listView.setAdapter(wordsAdapter);

            return view;
        }
    }
}
