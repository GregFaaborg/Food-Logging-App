package com.CSCE4901.Mint.Report;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.CSCE4901.Mint.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Temp extends AppCompatActivity {


    private static final int REQUEST_CODE = 22;
    Button button;

    private String[] header = {"Title", "Category", "Date"};
    private String shortText = "Hello";
    private String longText = "BLAuybcuwciwi wcuycwd ycdwquv cdw    uv cwyv dqouvcwqouvywqvy quvy";
    private TemplatePDF templatePDF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            //Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        Calendar cal = Calendar.getInstance();


        DateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);

        //clear time of day
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date start = cal.getTime();

        //start of next week
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        Date end = cal.getTime();

        String formattedStart = dateFormat.format(start);
        String formattedEnd = dateFormat.format(end);
        String range = formattedStart + " to " + formattedEnd;


        button = findViewById(R.id.view_pdf_button);

        templatePDF = new TemplatePDF(getApplicationContext());

        templatePDF.openDocument();
        templatePDF.addMetaData("Entries", "Report", "Mint App");
        templatePDF.addTitles("Mint Report", "for week ", range);
        templatePDF.addParagraph(shortText);
        templatePDF.addParagraph(longText);
        templatePDF.createTable(header,getClients());
        templatePDF.closeDocument();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templatePDF.viewPDF();
            }
        });

    }

    private ArrayList<String[]> getClients() {

        ArrayList<String[]> rows = new ArrayList<>();

        rows.add(new String[]{"THIS","THAT","TODAY"});
        rows.add(new String[]{"THIs","THAt","TODAy"});
        rows.add(new String[]{"THis","THat","TODay"});
        rows.add(new String[]{"This","That","TOday"});

        return rows;
    }
}

