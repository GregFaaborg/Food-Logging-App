package com.CSCE4901.Mint.Search;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.CSCE4901.Mint.R;

import static android.content.ContentValues.TAG;


public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        final SearchView searchView = view.findViewById(R.id.search);





        Log.d(TAG, "onCreateView: HERE");

        /*
        final Spinner spinner = view.findViewById(R.id.search_spinner);


        setting up spinner for search options (flagged, date, title)
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource
                (getContext(),R.array.search_options, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        */

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                Log.d(TAG, "onCreateView: HERE in searchview on click list");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
                Log.d(TAG, "onCreateView: in on query text submit");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCreateView: in on query text change");
                return true;
            }
        });

        Log.d(TAG, "onCreateView: before return view");
        return view;


    }



}
