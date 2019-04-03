package com.CSCE4901.Mint.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.CSCE4901.Mint.R;
import com.CSCE4901.Mint.Search.SearchAdapter;
import com.CSCE4901.Mint.Search.SearchItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    //declare variables
    FirebaseAuth firebaseAuth;

    View view;
    String pickedDate;
    String displayPickedDate;
    CalendarView CAL;

    FirebaseFirestore db=FirebaseFirestore.getInstance(); //connect to firebase db

    LinearLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    SearchAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false); //connect to XML fragment_home

        //set format to get the date
        final SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy");
        final SimpleDateFormat DATEformat2 = new SimpleDateFormat("MMMM, d, yyyy");

        //initialize calendar view
        CAL=view.findViewById(R.id.home_calendar);

        //initialize Fire base Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get the DATE for default date
        pickedDate = DATEformat.format(new Date(CAL.getDate()));
        displayPickedDate = DATEformat2.format(new Date(CAL.getDate()));
        final TextView displayDATE = view.findViewById(R.id.DATE);
        displayDATE.setText(displayPickedDate);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.homeRecycler);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String UserEmail = currentUser.getEmail();

        db.collection(UserEmail)
                .whereEqualTo("date", pickedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<SearchItem> arrItems = new ArrayList<SearchItem>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrItems.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title")));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            mAdapter = new SearchAdapter(arrItems);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        //when date picker is change set the DATe to that chosen date
        CAL.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                int d =dayOfMonth;
                int m = month+1;
                int y = year;
                String D = String.valueOf(d);
                String M = String.valueOf(m);
                String Y = String.valueOf(y);
                pickedDate=M + "/" + D + "/" + Y;
                displayPickedDate = DATEformat2.format(new Date(year-1900,month, dayOfMonth));//displayPickedDate = M+","+D+","+Y;
                displayDATE.setText(displayPickedDate);
                //Toast.makeText(CAL.getContext(), " "+pickedDate, Toast.LENGTH_LONG).show();

                //search through entries for the pickedDate chosen and display them in the recycler view

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();

                db.collection(UserEmail)
                        .whereEqualTo("date", pickedDate)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<SearchItem> arrItems = new ArrayList<SearchItem>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        arrItems.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title")));
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    mAdapter = new SearchAdapter(arrItems);
                                    mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

            }
        });



        return view;

    }
}
