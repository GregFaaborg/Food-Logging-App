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

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

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

    }


    private void monthPicker() {
        final Calendar today = Calendar.getInstance();

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                Log.d("Month Picker", "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                Toast.makeText(getContext(), "Date set with month " + selectedMonth  + " year " + selectedYear, Toast.LENGTH_SHORT).show();
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setTitle("Select Month and Year for Report")
                .build()
                .show();
    }


}
