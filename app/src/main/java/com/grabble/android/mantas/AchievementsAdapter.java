package com.grabble.android.mantas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mantas on 09/11/2016.
 */

public class AchievementsAdapter extends BaseAdapter {
    private Context context;
    public Achievement[] achievements = {
            new Achievement("Test1", R.drawable.test1),
            new Achievement("Test2", R.drawable.test1),
            new Achievement("Test1", R.drawable.test1),
            new Achievement("Test1", R.drawable.test1),
            new Achievement("Test1", R.drawable.test1)
    };

    public AchievementsAdapter(Context c) {
        context = c;
    }

    @Override
    public int getCount() {
        return achievements.length;
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
        View linearLayout;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            linearLayout = inflater.inflate(R.layout.achievement_layout, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.achievement_title);
            ImageView imageView = (ImageView) linearLayout.findViewById(R.id.achievement_image);
            textView.setText(achievements[position].getTitle());
            imageView.setImageResource(achievements[position].getImageId());
        } else {
            linearLayout = (View) convertView;
        }
        return linearLayout;
    }
}
