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

import org.w3c.dom.Text;

/**
 * Created by Mantas on 14/01/2017.
 */

public class LeaderboardAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;

    private SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    private String currentUserNickname = sharedPrefs.getString(
            getContext().getString(R.string.pref_user_nickname_key),
            getContext().getString(R.string.pref_user_nickname_default));

    public LeaderboardAdapter(Context context, int resource) {
        super(context, resource);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_leaderboard, parent, false);
            holder = new UserViewHolder();
            holder.place = (TextView) convertView.findViewById(R.id.place);
            holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
            holder.score = (TextView) convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        } else {
            holder = (UserViewHolder) convertView.getTag();
        }

        User user = getItem(position);

        if (user != null) {
            holder.place.setText(String.format("%d", user.getPlace()));
            holder.nickname.setText(user.getNickname());
            holder.score.setText(String.format("%d", user.getScore()));

            // Highlight the current user
            if (isCurrentUser(user.getNickname())) {
                highlightUser(holder);
            } else {
                holder.place.setTextColor(Color.WHITE);
                holder.nickname.setTextColor(Color.WHITE);
                holder.score.setTextColor(Color.WHITE);
            }
        }

        return convertView;
    }

    /**
     * Using ViewHolder for performance reasons.
     * More info: https://dl.google.com/googleio/2010/android-world-of-listview-android.pdf
     */
    private static class UserViewHolder {
        TextView place;
        TextView nickname;
        TextView score;
    }

    private boolean isCurrentUser(String nickname) {
        return currentUserNickname.equals(nickname);
    }

    private void highlightUser(UserViewHolder holder) {
        holder.place.setTextColor(Color.parseColor(getContext().getString(R.string.color_light_blue)));
        holder.nickname.setTextColor(Color.parseColor(getContext().getString(R.string.color_light_blue)));
        holder.score.setTextColor(Color.parseColor(getContext().getString(R.string.color_light_blue)));
    }
}
