package com.grabble.android.mantas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
        private LeaderboardAdapter leaderboardAdapter;

        public LeaderboardFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

            leaderboardAdapter = new LeaderboardAdapter(getContext(), R.layout.list_item_leaderboard);

            ListView listView = (ListView) view.findViewById(R.id.leaderboard);
            listView.setAdapter(leaderboardAdapter);

            FetchLeaderboardTask fetchLeaderboardTask = new FetchLeaderboardTask(getContext(), leaderboardAdapter);
            fetchLeaderboardTask.execute();

            return view;
        }
    }
}
