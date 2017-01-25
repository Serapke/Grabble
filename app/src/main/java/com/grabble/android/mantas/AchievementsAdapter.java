package com.grabble.android.mantas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mantas on 09/11/2016.
 */

public class AchievementsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Achievement> achievements;

    public AchievementsAdapter(Context context, List<Achievement> achievements, Long size) {
        this.inflater = LayoutInflater.from(context);
        this.achievements = achievements;
        // Show 'Lock' for locked achievements
        while (achievements.size() < size) {
            achievements.add(new Achievement("Locked", R.drawable.achievement_locked));
        }
    }

    @Override
    public int getCount() {
        return achievements.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.achievement_layout, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.achievement_title);
            holder.icon = (ImageView) convertView.findViewById(R.id.achievement_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(achievements.get(position).getTitle());
        holder.icon.setImageResource(achievements.get(position).getImageId());

        return convertView;
    }

    /**
     * Using ViewHolder for performance reasons.
     * More info: https://dl.google.com/googleio/2010/android-world-of-listview-android.pdf
     */
    private static class ViewHolder {
        TextView title;
        ImageView icon;
    }
}
