package com.grabble.android.mantas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.grabble.android.mantas.data.GrabbleDbHelper;
import com.grabble.android.mantas.data.GrabbleContract.BagEntry;

public class BagActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_bag, new BagFragment())
                    .commit();
        }
    }

    public static class BagFragment extends Fragment {

        SimpleCursorAdapter lettersAdapter;
        SQLiteDatabase db;

        public BagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_bag, container, false);

            GrabbleDbHelper dbHelper = new GrabbleDbHelper(getContext());
            db = dbHelper.getReadableDatabase();

            lettersAdapter = new SimpleCursorAdapter(getContext(),
                    R.layout.letter_layout,
                    dbHelper.getAllLetterCountsInBag(db),
                    new String[] { BagEntry.COLUMN_NAME_LETTER, "count" },
                    new int[] { R.id.letter_value, R.id.letter_count },
                    0
            );

            GridView gridView = (GridView) view.findViewById(R.id.letters);
            gridView.setAdapter(lettersAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int i, long id) {
                    Cursor cursor = (Cursor) lettersAdapter.getItem(i);
                    String letter = cursor.getString(cursor.getColumnIndex(BagEntry.COLUMN_NAME_LETTER));
                    Toast.makeText(getContext(), letter, Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
            db.close();
        }
    }
}
