package com.grabble.android.mantas;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_leaderboard, new LeaderboardFragment())
                    .commit();
        }
    }

    public static class LeaderboardFragment extends Fragment {
        private ArrayAdapter<String> leaderboardAdapter;
        private String[] leaderboardArray = {
            " 7.        Nickname #1                                                           439",
            " 8.        Nickname #2                                                           420",
            " 9.        Nickname #3                                                           398",
            "10.        Nickname #4                                                          354",
            "11.        Grabbster9000                                                       333",
            "12.        Nickname #5                                                          327",
            "13.        Nickname #6                                                          320",
            "14.        Nickname #7                                                          298",
            "15.        Nickname #8                                                          209",
            "16.        Nickname #9                                                          201"
        };
        private ArrayList<String> leaderboard = new ArrayList<>(Arrays.asList(leaderboardArray));

        public LeaderboardFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

            leaderboardAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_leaderboard,
                    R.id.list_item_leaderboard_textview,
                    leaderboard
            );

            ListView listView = (ListView) view.findViewById(R.id.leaderboard);
            listView.setAdapter(leaderboardAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(getActivity().getApplicationContext(), leaderboardArray[position], Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }
}
