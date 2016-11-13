package com.grabble.android.mantas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Mantas on 13/11/2016.
 */

public class LettersAdapter extends BaseAdapter {
    private Context context;
    public Letter[] letters = {
            new Letter('A', 2),
            new Letter('Z', 1),
            new Letter('M', 3),
            new Letter('P', 9),
            new Letter('Y', 12),
            new Letter('E', 5),
            new Letter('W', 7)
    };

    public LettersAdapter(Context c) { context = c; }

    @Override
    public int getCount() { return letters.length; }

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
            linearLayout = inflater.inflate(R.layout.letter_layout, null);
            TextView letterView = (TextView) linearLayout.findViewById(R.id.letter_value);
            TextView letterCountView = (TextView) linearLayout.findViewById(R.id.letter_count);
            letterView.setText(letters[position].getValue().toString());
            letterCountView.setText(letters[position].getCount().toString());
        } else {
            linearLayout = (LinearLayout) convertView;
        }
        return linearLayout;
    }
}
