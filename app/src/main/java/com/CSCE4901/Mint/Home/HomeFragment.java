package com.CSCE4901.Mint.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    //declare variables
    FirebaseAuth firebaseAuth;

    View view;
    String pickedDate;
    CalendarView CAL;

    FirebaseFirestore db=FirebaseFirestore.getInstance(); //connect to firebase db


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false); //connect to XML fragment_home

        //set format to get the date
        final SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy");

        //initialize calendar view
        CAL=view.findViewById(R.id.entry_calendar);

        //initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get the DATE for default date
        pickedDate = DATEformat.format(new Date(CAL.getDate()));

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

                Toast.makeText(CAL.getContext(), " "+pickedDate, Toast.LENGTH_LONG).show();
            }
        });

        return view;

    }
}
