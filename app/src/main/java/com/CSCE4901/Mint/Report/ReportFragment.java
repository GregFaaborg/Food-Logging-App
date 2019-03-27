package com.CSCE4901.Mint.Report;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;


import com.CSCE4901.Mint.MainActivity;
import com.CSCE4901.Mint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DayOfWeek;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.whiteelephant.monthpicker.MonthPickerDialog;



public class ReportFragment extends Fragment{

    View view;

    Button weekly;
    Button monthly;
    Button favorites;

    FirebaseFirestore db;

    public static final int REQUEST_CODE = 1;  // Used to identify the result

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);


        //get instance of firestore database
        db = FirebaseFirestore.getInstance();

        final FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();


        weekly = view.findViewById(R.id.weekly_report);
        monthly = view.findViewById(R.id.monthly_report);
        favorites = view.findViewById(R.id.favorites_report);

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatDialogFragment appCompatDialogFragment = new DatePickerFragment();

                appCompatDialogFragment.setTargetFragment(ReportFragment.this, REQUEST_CODE);
                appCompatDialogFragment.show(fragmentManager,"Select Week");

            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthPicker();
            }
        });


        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFavorites();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            String selectedDate = data.getStringExtra("selectedDate");

            getWeekRange(selectedDate);
        }
    }

    private void getWeekRange(String selectedDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy", Locale.US);

        Date date;
        Calendar cal = Calendar.getInstance();

        try {

            //parse selected date and set date to that
            date = sdf.parse(selectedDate);
            cal.setTime(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //set date to sunday (minimum day of week)
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK));

        //getting first day of week
        Date firstDay = cal.getTime();
        String firstDayOfWeek = sdf.format(firstDay);

        //Add 6 days to first day of week to get last day
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String lastDayOfWeek= sdf.format(cal.getTime());


        //Printing Week range
        Toast.makeText(getContext(), firstDayOfWeek + " - " + lastDayOfWeek, Toast.LENGTH_SHORT).show();

        dateRangeQuery(firstDayOfWeek, lastDayOfWeek);
    }


    private void monthPicker() {
        final Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);


        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {

                Log.d("Month Picker", "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);


                SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy", Locale.US);

                date.set(selectedYear,selectedMonth,1);

                //get first day of month
                date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
                String firstDayOfMonth = sdf.format(date.getTime());

                //get last day of month
                date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
                String lastDayOfMonth = sdf.format(date.getTime());

                Toast.makeText(getContext(), firstDayOfMonth + " - " + lastDayOfMonth, Toast.LENGTH_SHORT).show();

                dateRangeQuery(firstDayOfMonth, lastDayOfMonth);

            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH));

        builder.setTitle("Select Month and Year for Report")
                .setMaxMonth(month)
                .setMaxYear(year)
                .build()
                .show();
    }

    private void dateRangeQuery(String begin, String end){

        String email = getUserEmail();
        db.collection(email)
                .whereGreaterThanOrEqualTo("date", begin)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {

                                Log.d("Month", document.getId() + " => " + document.getData());
                            }
                        }
                        else {
                            Log.d("Month", "Error getting documents: ", task.getException());
                        }
                    }
                });





    }

    private String getUserEmail(){
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();

        if(user != null) {
            String email = user.getEmail();
            return email;
        }

        return null;
    }

    private void getFavorites() {

        String email = getUserEmail();
        db.collection(email)
                .whereEqualTo("flag", "1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {

                                Log.d("Favorites", document.getId() + " => " + document.getData());
                            }
                        }
                        else {
                            Log.d("Favorites", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
