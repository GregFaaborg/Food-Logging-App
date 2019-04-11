package com.CSCE4901.Mint.Report;


import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;



public class ReportFragment extends Fragment {

    View view;

    Button weekly;
    Button monthly;
    Button favorites;

    FirebaseFirestore db;

    //for requesting permissions to access files
    private static final int REQUEST_CODE = 22;




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


        weekly = view.findViewById(R.id.custom_range_report);
        monthly = view.findViewById(R.id.monthly_report);
        favorites = view.findViewById(R.id.favorites_report);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        //ask permission for file access
        if (!checkFilePermission()) {
            // Permission is not granted, so asking for permission

            askFilePermission();
        }

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkFilePermission()){
                    askFilePermission();
                }
                else {
                    customRangePicker();
                }

            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFilePermission()){
                    askFilePermission();
                }
                else {
                    monthPicker();
                }
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkFilePermission()){
                    askFilePermission();
                }
                else {
                    getFavorites();
                }

            }
        });

        return view;
    }

    private void askFilePermission(){

        Toast.makeText(getContext(), "File access required for reports", Toast.LENGTH_SHORT).show();

        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    private void customRangePicker(){

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getFragmentManager();

        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd)
                    {
                        //get start and end date
                        String startDate =  (++monthStart) + "/" + dayStart + "/" + yearStart;
                        String endDate = (++monthEnd) + "/" + (++dayEnd) + "/" + yearEnd;

                        SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy", Locale.US);
                        Date beginDate = new Date();
                        Date lastDate = new Date();
                        try {
                            beginDate = DATEformat.parse(startDate);
                            lastDate = DATEformat.parse(endDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        dateRangeQuery(beginDate, lastDate, startDate, endDate);
                    }
                });

        smoothDateRangePickerFragment.setMaxDate(Calendar.getInstance());
        smoothDateRangePickerFragment.show(fragmentManager, "smoothDateRangePicker");
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
                Date beginDate = date.getTime();

                //get last day of month
                date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
                date.add(Calendar.DATE, 1);
                String lastDayOfMonth = sdf.format(date.getTime());
                Date endDate = date.getTime();

                //Toast.makeText(getContext(), firstDayOfMonth + " - " + lastDayOfMonth, Toast.LENGTH_SHORT).show();

                dateRangeQuery(beginDate, endDate, firstDayOfMonth, lastDayOfMonth);


            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH));

        builder.setTitle("Select Month and Year for Report")
                .setMaxMonth(month)
                .setMaxYear(year)
                .build()
                .show();
    }

    private void dateRangeQuery(final Date beginDate, final Date endDate,final String begin, final String end){

        String email = getUserEmail();

        assert email != null;
        db.collection(email)
                .whereGreaterThanOrEqualTo("timestamp", beginDate)
                .whereLessThanOrEqualTo("timestamp", endDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Log.d("Range", begin + " - " + end);
                        TemplatePDF templatePDF;
                        //constructor for PDF
                        templatePDF = new TemplatePDF(getContext());

                        createPDF(templatePDF,"for dates ",begin + " to " + end);

                        StringBuilder entryData = new StringBuilder();
                        String favorite;

                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {

                                Log.d("Range", begin + " - " + end);
                                Log.d("Month", document.getId() + " => " + document.getData());
                                entryData.append("Title: ").append(document.getString("title")).append("\n");
                                entryData.append("Date: ").append(document.getString("date")).append("\n");
                                entryData.append("Category: ").append(document.getString("category")).append("\n");
                                favorite = document.getString("flag");
                                favorite = Objects.equals(favorite, "0") ? "No" : "Yes";
                                entryData.append("Favorite: ").append(favorite).append("\n");
                                entryData.append("Description: ").append(document.getString("description")).append("\n");
                                entryData.append("\n");

                                String finalEntryData = entryData.toString();
                                templatePDF.addParagraph(finalEntryData);
                                entryData.setLength(0);
                                Log.d("finalEntryData", finalEntryData);

                            }
                            templatePDF.closeDocument();
                            templatePDF.viewPDF();
                        }
                        else {

                            Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                            Log.d("Date", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private String getUserEmail(){
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();

        if(user != null) {
            return user.getEmail();
        }

        return null;
    }

    private void getFavorites() {

        //createPDF("Favorites ", "");

        String email = getUserEmail();
        assert email != null;
        db.collection(email)
                .whereEqualTo("flag", "1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        TemplatePDF templatePDF;
                        //constructor for PDF
                        templatePDF = new TemplatePDF(getContext());

                        createPDF(templatePDF, "Favorites", "");

                        StringBuilder entryData = new StringBuilder();
                        String favorite;

                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {

                                Log.d("Favorites", document.getId() + " => " + document.getData());
                                entryData.append("Title: ").append(document.getString("title")).append("\n");
                                entryData.append("Date: ").append(document.getString("date")).append("\n");
                                entryData.append("Category: ").append(document.getString("category")).append("\n");
                                favorite = document.getString("flag");
                                favorite = Objects.equals(favorite, "0") ? "No" : "Yes";
                                entryData.append("Favorite: ").append(favorite).append("\n");
                                entryData.append("Description: ").append(document.getString("description")).append("\n");
                                entryData.append("\n");

                                String finalEntryData = entryData.toString();
                                Log.d("finalEntryData", finalEntryData);
                                entryData.setLength(0);
                                templatePDF.addParagraph(finalEntryData);


                            }
                            templatePDF.closeDocument();
                            templatePDF.viewPDF();
                        }
                        else {
                            Log.d("Favorites", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void createPDF(TemplatePDF templatePDF,String subTitle, String dateRange)
    {

        templatePDF.openDocument();
        templatePDF.addMetaData("Report", "Entries Report", "Mint App");
        templatePDF.addTitles("Mint Report", subTitle, dateRange);

    }


    private boolean checkFilePermission(){

        //returns true if file access permissions are allowed, false otherwise
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

}
