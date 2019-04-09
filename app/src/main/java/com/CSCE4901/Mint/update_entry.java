package com.CSCE4901.Mint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class update_entry extends AppCompatActivity{

        Button Cancel;
        Button Update;

        EditText title;
        EditText des;

        Spinner CAT;
        EditText customCategory;
        EditText customCategory2;
        boolean customEnabled = false;
        String cat;
        String category;
        String categoryTemp;

        ImageButton flag;
        String flagged = "0";//default set to false

        String DATE;

        CalendarView CAL;

        FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database


        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.update);//layout
            //set format to get the date
            final SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy");

            Objects.requireNonNull(getSupportActionBar()).hide();

            //initialize buttons
            Cancel = (Button) findViewById(R.id.cancel_button);
            Update = (Button) findViewById(R.id.update_button);

            //initialize editTexts
            title = (EditText) findViewById(R.id.entry_title);
            customCategory = (EditText) findViewById(R.id.custom_category);
            customCategory2 = (EditText) findViewById(R.id.custom_category2);
            des = (EditText) findViewById(R.id.entry_description);

            //initialize image button
            flag = (ImageButton) findViewById(R.id.flag);

            //initialize calendar view
            CAL=update_entry.this.findViewById(R.id.entry_calendar);
            Calendar now = Calendar.getInstance();
            CAL.setMaxDate(now.getTimeInMillis());

            //get intent from searchAdapter
            Intent intent = getIntent();
            final HashMap<String,String> data = (HashMap<String, String>) intent.getSerializableExtra("key");

            //initialize spinner
            CAT= (Spinner) findViewById(R.id.entry_category);
            final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                    R.array.categories, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CAT.setAdapter(arrayAdapter);

            CAT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //customCategory2.setVisibility(View.GONE);//the last cat be gone from view
                    cat = parent.getItemAtPosition(position).toString();
                    //customCategory2.setVisibility(View.GONE);
                    //Toast.makeText(parent.getContext(), cat, Toast.LENGTH_SHORT).show();
                    if (cat.equals("Custom")){
                        //customCategory2.setVisibility(View.GONE);
                        customCategory.setVisibility(View.VISIBLE);
                        customEnabled = true;
                        category = customCategory.getText().toString();
                        categoryTemp = "";
                    }
                    else
                    {
                        if(cat.equals("Breakfast")) {
                            if(categoryTemp.equals("")) { //empty category do not sow custom
                                customCategory.setVisibility(View.GONE);
                                category = cat;// breakfast as category
                            }
                            else { //show custom
                                if(categoryTemp.equals("1")){
                                    customCategory.setVisibility(View.GONE);
                                    parent.setSelection(arrayAdapter.getPosition("Lunch"));
                                    //category = customCategory.getText().toString();
                                    category=cat;
                                }
                                if(categoryTemp.equals("2")) {
                                    customCategory.setVisibility(View.GONE);
                                    parent.setSelection(arrayAdapter.getPosition("Dinner"));
                                    category=cat;
                                }
                                if(categoryTemp.equals("3")) {
                                    customCategory.setVisibility(View.GONE);
                                    parent.setSelection(arrayAdapter.getPosition("Snack"));
                                    category=cat;
                                }
                                if(categoryTemp.equals("4")) {
                                    customCategory.setVisibility(View.VISIBLE);
                                    parent.setSelection(arrayAdapter.getPosition("Custom"));
                                    category = customCategory.getText().toString();
                                    categoryTemp = "";
                                }
                            }
                        }
                        else
                        {
                            customCategory.setVisibility(View.GONE);
                            category = cat;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //get data from hash map passed on intent
            String tempTitle = data.get("title");
            title.setText(tempTitle);

            String tempDes = data.get("description");
            des.setText(tempDes);

            String tempCat = data.get("category");

            if(!tempCat.equals("Breakfast") || !"Lunch".equals(tempCat)|| !"Dinner".equals(tempCat) || !"Snack".equals(tempCat))
            {
                customCategory.setText(tempCat);
                category = customCategory.getText().toString();
                categoryTemp = "4";
                customCategory.setVisibility(View.VISIBLE);
                //customCategory2.setText(tempCat);
                //customCategory2.setVisibility(View.VISIBLE);
            }

            if(tempCat.equals("Breakfast") || "Lunch".equals(tempCat)|| "Dinner".equals(tempCat) || "Snack".equals(tempCat))
            {
                //Toast.makeText(update_entry.this, tempCat, Toast.LENGTH_LONG).show();
                //category="";
                categoryTemp = "";
                customCategory.setText("");
                customCategory.setVisibility(View.GONE);
                if(tempCat.equals("Lunch")){
                    categoryTemp="1";
                }
                if(tempCat.equals("Dinner")) {
                    categoryTemp="2";
                }
                if(tempCat.equals("Snack")) {
                    categoryTemp="3";
                }
            }

            //set date picker of calendar to OG date
            DATE=data.get("date");
            String dateParts[] = DATE.split("/");

            int month = Integer.parseInt(dateParts[0]);
            int day = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);

            long milliTime = calendar.getTimeInMillis();

            CAL.setDate(milliTime, true,true);




            //flag from OG entry
            final String tempFlag = data.get("flag");
            if(tempFlag.equals("1"))
            {
                //change color to YELLOW
                flag.setColorFilter(Color.parseColor("#CCCC00"));
                //FLAG.setBackgroundColor(Color.parseColor("#CCCC00"));
                flagged = "1";
            }
            else
            {
                //change button color back to normal non pushed
                flag.setColorFilter(Color.parseColor("#696969"));
                //FLAG.setBackgroundColor(Color.parseColor("#696969"));
                flagged = "0";
            }


            //flag button selected
            flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //if flag button is not pushed
                    if(flagged == "0") {
                        //change color to YELLOW
                        flag.setColorFilter(Color.parseColor("#CCCC00"));
                        //FLAG.setBackgroundColor(Color.parseColor("#CCCC00"));
                        flagged = "1";
                    }
                    //else if flag button has already been pushed AKA flagged =="1"
                    else
                    {
                        //change button color back to normal non pushed
                        flag.setColorFilter(Color.parseColor("#696969"));
                        //FLAG.setBackgroundColor(Color.parseColor("#696969"));
                        flagged = "0";
                    }
                }
            });

            Update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String editTitle = title.getText().toString();
                    String editDes = des.getText().toString();

                    if (editTitle.equals("") || editTitle.equals("Need Title")) {
                        title.setTextColor(Color.parseColor("#8B0000"));
                        title.setText("Need Title");

                    }
                    else {

                        //get all the information in a HashMap
                        final HashMap<String, Object> updateData = new HashMap<>();
                        updateData.put("title", editTitle);
                        updateData.put("category", category);
                        updateData.put("description", editDes);
                        updateData.put("flag", flagged);
                        updateData.put("date", DATE);

                        final String userEmail = data.get("email"); //get email from data hashMap in intent
                        String ID = data.get("ID"); //get ID name of doc from data hashMap in intent

                        //update doc in database
                        //delete entry
                        //db.collection(userEmail).document(ID).delete();
                        //db.collection(userEmail).document(ID).set(updateData);
                        final DocumentReference updateDoc = db.collection(userEmail).document(ID);
                        updateDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    updateDoc.update(updateData);
                                }
                                else {
                                    db.collection(userEmail).add(updateData);
                                }
                            }
                        });

                        //edit entry toast
                        Toast.makeText(update_entry.this, String.format("Updating Entry"), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
                        editor.putInt("CHECK", 1);
                        editor.apply();
                        //go back to home fragment
                        //finish();
                        /*Intent goIntent = new Intent(getApplicationContext(), OverviewActivity.class);
                        goIntent.putExtra("check", 1); //send data hashMap
                        startActivity(goIntent);
                        */
                        //onResumeFragments();
                        finish();
                        Toast.makeText(update_entry.this, String.format("testing"), Toast.LENGTH_SHORT).show();

                    }
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go back to home fragment
                    finish();
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
                    DATE=M + "/" + D + "/" + Y;
                }
            });



        }



}
