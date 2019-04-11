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
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        mRecyclerView = view.findViewById(R.id.search_results);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

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

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {

                Log.d(TAG, "onCreateView: in on query text change");
                Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                assert currentUser != null;
                String UserEmail = currentUser.getEmail();

                assert UserEmail != null;
                Index index = client.getIndex(UserEmail);

                Query query = new Query(s)
                        .setAttributesToRetrieve("title", "description", "date", "category", "flag")
                        .setHitsPerPage(50);
                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject content, AlgoliaException error) {
                        try {
                            JSONArray hits = content.getJSONArray("hits");
                            ArrayList<SearchItem> arrItems = new ArrayList<SearchItem>();
                            for (int i = 0; i < hits.length(); i++){
                                JSONObject jsonObject = hits.getJSONObject(i);
                                String cat = jsonObject.getString("category");
                                String date = jsonObject.getString("date");
                                String desc = jsonObject.getString("description");
                                String flag = jsonObject.getString("flag");
                                String title = jsonObject.getString("title");
                                String ID = jsonObject.getString("objectID");

                                SearchItem searchItem = new SearchItem(cat,date,desc,flag,title,ID);
                                arrItems.add(searchItem);

                                Log.d("Algolia", content.toString());
                            }
                            mAdapter = new SearchAdapter(arrItems);
                            mRecyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                return true;
            }
        });

        Log.d(TAG, "onCreateView: before return view");

        return view;
    }


}
