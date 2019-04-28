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
import android.widget.TextView;
import android.widget.Toast;

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

    private FirebaseAuth firebaseAuth;
    //private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private TextView noResultsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        final SearchView searchView = view.findViewById(R.id.search);
        noResultsText = view.findViewById(R.id.noResults);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Searching...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mRecyclerView = view.findViewById(R.id.search_results);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
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

                        //no error occurred
                        if (error == null){
                            try {
                                JSONArray hits = content.getJSONArray("hits");
                                ArrayList<SearchItem> arrItems = new ArrayList<>();
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
                                NoResults();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //error did occur. Show toast and log error
                        else {
                            Toast.makeText(getContext(), "Error occurred. Try again later", Toast.LENGTH_LONG).show();
                            Log.d("Algolia Error", error.toString());
                        }
                    }
                });
                return true;
            }
        });

        Log.d(TAG, "onCreateView: before return view");

        return view;
    }

    private void NoResults(){
        if(mAdapter.getItemCount() == 0){
            noResultsText.setVisibility(View.VISIBLE);
        }
        else {
            noResultsText.setVisibility(View.INVISIBLE);
        }
    }


}
