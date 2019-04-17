package com.CSCE4901.Mint.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    //declare variables
    private FirebaseAuth firebaseAuth;

    private String pickedDate;
    private String displayPickedDate;

    private final FirebaseFirestore db=FirebaseFirestore.getInstance(); //connect to firebase db

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //saved=savedInstanceState;



        //set format to get the date
        final SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy", Locale.US);
        final SimpleDateFormat DATEformat2 = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

        //initialize calendar view
        CalendarView CAL = view.findViewById(R.id.home_calendar);
        Calendar now = Calendar.getInstance();
        CAL.setMaxDate(now.getTimeInMillis());

        //refresh button initialize
        ImageButton RE = view.findViewById(R.id.RE);

        //initialize Fire base Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get the DATE for default date
        pickedDate = DATEformat.format(new Date(CAL.getDate()));
        displayPickedDate = DATEformat2.format(new Date(CAL.getDate()));
        final TextView displayDATE = view.findViewById(R.id.DATE);
        displayDATE.setText(displayPickedDate);

        mRecyclerView = view.findViewById(R.id.homeRecycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String UserEmail = Objects.requireNonNull(currentUser).getEmail();

        db.collection(Objects.requireNonNull(UserEmail)).whereEqualTo("date", pickedDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<SearchItem> arrItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        arrItems.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title"), document.getId()));
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    mAdapter = new SearchAdapter(arrItems);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });


        //when date picker is change set the DATe to that chosen date
        CAL.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull final CalendarView view, int year, int month, int dayOfMonth) {

                int m = month + 1;
                String D = String.valueOf(dayOfMonth);
                String M = String.valueOf(m);
                String Y = String.valueOf(year);
                pickedDate = M + "/" + D + "/" + Y;
                displayPickedDate = DATEformat2.format(new Date(year - 1900, month, dayOfMonth));//displayPickedDate = M+","+D+","+Y;
                displayDATE.setText(displayPickedDate);
                //Toast.makeText(CAL.getContext(), " "+pickedDate, Toast.LENGTH_LONG).show();

                //search through entries for the pickedDate chosen and display them in the recycler view

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = Objects.requireNonNull(currentUser).getEmail();

                db.collection(Objects.requireNonNull(UserEmail)).whereEqualTo("date", pickedDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<SearchItem> arrItems = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                arrItems.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title"), document.getId()));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            mAdapter = null;
                            mAdapter = new SearchAdapter(arrItems);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


            }
        });

        RE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = Objects.requireNonNull(currentUser).getEmail();
                final ArrayList<SearchItem> arrItems = new ArrayList<>();

                db.collection(Objects.requireNonNull(UserEmail)).whereEqualTo("date", pickedDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //ArrayList<SearchItem> arrItems = new ArrayList<SearchItem>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                arrItems.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title"), document.getId()));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            mAdapter = null;
                            mAdapter = new SearchAdapter(arrItems);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

                //mItems.addAll(mItems);
                mAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Toast.makeText(getContext(), String.format("ON RESUME "), Toast.LENGTH_SHORT).show();
        SharedPreferences editor = Objects.requireNonNull(this.getActivity()).getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        int check = editor.getInt("CHECK",0); //set as false aka not finished
        //Toast.makeText(getContext(), String.format(" "+check), Toast.LENGTH_SHORT).show();
        if(check==1) {
            mAdapter=null;
            FirebaseUser current = firebaseAuth.getCurrentUser();
            String emailUser = Objects.requireNonNull(current).getEmail();
            //final ArrayList<SearchItem> itemsList = new ArrayList<SearchItem>();

            db.collection(Objects.requireNonNull(emailUser)).whereEqualTo("date", pickedDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<SearchItem> itemsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            itemsList.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title"), document.getId()));
                            //Toast.makeText(getContext(), String.format("FLAG: "+document.getString("flag")), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        mAdapter=null;
                        mAdapter = new SearchAdapter(itemsList);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        }
        //newA.notifyDataSetChanged();
        SharedPreferences.Editor edit = this.getActivity().getSharedPreferences("PreferencesName", Context.MODE_PRIVATE).edit();
        edit.putInt("CHECK",0); //set as false aka not finished
        edit.apply();

        /*
        //Toast.makeText(getContext(), String.format("ON RESUME "), Toast.LENGTH_SHORT).show();
        SharedPreferences editor = this.getActivity().getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        int check = editor.getInt("CHECK",0); //set as false aka not finished
        //Toast.makeText(getContext(), String.format(" "+check), Toast.LENGTH_SHORT).show();
        if(check==1){
            //Toast.makeText(getContext(), String.format("IT IS 1: "+check), Toast.LENGTH_SHORT).show();
            //update
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser current = firebaseAuth.getCurrentUser();
            String emailUser = current.getEmail();
            final ArrayList<SearchItem> itemsList = new ArrayList<SearchItem>();

            db.collection(emailUser).whereEqualTo("date", pickedDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            itemsList.add(new SearchItem(document.getString("category"), document.getString("date"), document.getString("description"), document.getString("flag"), document.getString("title"), document.getId()));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        newA = null;
                        newA = new SearchAdapter(itemsList);
                        mRecyclerView.setAdapter(newA);
                        newA.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        }
        //newA.notifyDataSetChanged();
        SharedPreferences.Editor edit = this.getActivity().getSharedPreferences("PreferencesName", Context.MODE_PRIVATE).edit();
        edit.putInt("CHECK",0); //set as false aka not finished
        edit.apply();
         */
    }


}

