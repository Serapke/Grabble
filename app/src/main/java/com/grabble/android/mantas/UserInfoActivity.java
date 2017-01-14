package com.grabble.android.mantas;

import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Arrays;

public class UserInfoActivity extends AppCompatActivity {


    UserInfoPagerAdapter userInfoPagerAdapter;

    ViewPager viewPager;

    private static SQLiteDatabase db;
    private static GrabbleDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userInfoPagerAdapter = new UserInfoPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(userInfoPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        dbHelper = new GrabbleDbHelper(this);
        db = dbHelper.getReadableDatabase();
    }

    public class UserInfoPagerAdapter extends FragmentPagerAdapter {

        public UserInfoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new UserStatsFragment();
                case 1:
                    return new AchievementsFragment();
                case 2:
                    return new WordsCompletedFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "User Stats";
                case 1:
                    return "Achievements";
                case 2:
                    return "Words";
                default:
                    return "Empty";
            }
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
            String place = sharedPrefs.getString(
                    getString(R.string.pref_user_place_key),
                    getString(R.string.pref_user_place_default)) ;

            GrabbleDbHelper dbHelper = new GrabbleDbHelper(getContext());

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            long count = dbHelper.getLettersCount(db);

            View rootView = inflater.inflate(R.layout.fragment_section_user_stats, container, false);
            ((TextView) rootView.findViewById(R.id.nickname)).setText(nickname);
            ((TextView) rootView.findViewById(R.id.place)).setText(place + " / 1005 participants");
            ((TextView) rootView.findViewById(R.id.wordCount)).setText("10");
            ((TextView) rootView.findViewById(R.id.bestWord)).setText("Zyzomys (109)");
            ((TextView) rootView.findViewById(R.id.letterInABagCount)).setText(count + " letters");
            return rootView;
        }

    }

    public static class AchievementsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_section_achievements, container, false);

            GridView gridView = (GridView) view.findViewById(R.id.achievements);
            gridView.setAdapter(new AchievementsAdapter(getActivity().getApplicationContext()));

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
