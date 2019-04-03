package com.CSCE4901.Mint.Report;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;



public class ReportFragment extends Fragment {

    View view;

    Button weekly;
    Button monthly;
    Button favorites;

    FirebaseFirestore db;


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


        weekly = view.findViewById(R.id.weekly_report);
        monthly = view.findViewById(R.id.monthly_report);
        favorites = view.findViewById(R.id.favorites_report);

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentManager fragmentManager = getActivity().getFragmentManager();

                
                SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                        new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                       int yearStart, int monthStart,
                                                       int dayStart, int yearEnd,
                                                       int monthEnd, int dayEnd)
                            {
                                //get start and end date
                                String startDate = dayStart + "/" + (++monthStart) + "/" + yearStart;
                                String endDate = dayEnd + "/" + (++monthEnd) + "/" + yearEnd;

                                dateRangeQuery(startDate, endDate);
                            }
                        });

                smoothDateRangePickerFragment.setMaxDate(Calendar.getInstance());
                smoothDateRangePickerFragment.show(fragmentManager, "smoothDateRangePicker");



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
