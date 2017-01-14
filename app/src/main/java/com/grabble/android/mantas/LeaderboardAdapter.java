package com.grabble.android.mantas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Mantas on 14/01/2017.
 */

public class LeaderboardAdapter extends ArrayAdapter<User> {


    public LeaderboardAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.list_item_leaderboard, null);
        }

        User user = getItem(position);

        if (user != null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String currentUserNickname = sharedPrefs.getString(
                    getContext().getString(R.string.pref_user_nickname_key),
                    getContext().getString(R.string.pref_user_nickname_default));

            TextView place = (TextView) v.findViewById(R.id.place);
            TextView nickname = (TextView) v.findViewById(R.id.nickname);
            TextView score = (TextView) v.findViewById(R.id.score);

            if (place != null) {
                place.setText(user.getPlace().toString());
            }
            if (nickname != null) {
                nickname.setText(user.getNickname());
            }
            if (score != null) {
                score.setText(user.getScore().toString());
            }

            if (currentUserNickname.equals(user.getNickname())) {
                place.setTextColor(Color.parseColor(getContext().getString(R.string.color_light_blue)));
                nickname.setTextColor(Color.parseColor(getContext().getString(R.string.color_light_blue)));
                score.setTextColor(Color.parseColor(getContext().getString(R.string.color_light_blue)));
            } else {
                place.setTextColor(Color.WHITE);
                nickname.setTextColor(Color.WHITE);
                score.setTextColor(Color.WHITE);
            }
        }

        return v;
    }
}
