package com.CSCE4901.Mint.Search;



import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.CSCE4901.Mint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class SearchFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database
    //private DatabaseReference mDatabase;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SearchAdapter mAdapter;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        final SearchView searchView = view.findViewById(R.id.search);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Searching...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.search_results);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        //TODO implement recylerview to show search results

        /*
        TODO maybe use spinner for selecting filter options or use radio buttons
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
                //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
                progressDialog.show();
                Log.d(TAG, "onCreateView: in on query text submit");
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();
                db.collection(UserEmail)
                        .whereEqualTo("title", s)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    ArrayList<SearchItem> arrItems = new ArrayList<SearchItem>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        arrItems.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title"),document.getId()));
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    mAdapter = new SearchAdapter(arrItems);
                                    mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
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
