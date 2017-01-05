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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grabble.android.mantas.data.GrabbleDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class UserInfoActivity extends AppCompatActivity {


    UserInfoPagerAdapter userInfoPagerAdapter;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userInfoPagerAdapter = new UserInfoPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(userInfoPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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
        private ArrayAdapter<String> wordsAdapter;
        private String[] wordsArray = {
                "Aaronic",
                "Ababdeh",
                "abacate",
                "abacist",
                "abactor",
                "Abadite",
                "abaiser",
                "abalone"
        };
        private ArrayList<String> words = new ArrayList<>(Arrays.asList(wordsArray));

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_section_words, container, false);

            wordsAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_word,
                    R.id.list_item_word_textview,
                    words
            );

            ListView listView = (ListView) view.findViewById(R.id.words);
            listView.setAdapter(wordsAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }
}
