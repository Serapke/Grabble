package com.grabble.android.mantas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

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

        private ArrayAdapter<Character> lettersAdapter;
        private Character[] lettersArray = {
                'Z',
                'M',
                'P',
                'A',
                'A',
                'P',
                'Y',
                'E',
                'W',
                'Z'
        };

        public BagFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_bag, container, false);

            lettersAdapter = new ArrayAdapter<Character>(
                    getActivity(),
                    R.layout.list_item_letter,
                    R.id.list_item_letter_textview,
                    lettersArray
            );

            GridView gridView = (GridView) view.findViewById(R.id.letters);
            gridView.setAdapter(lettersAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }

}
