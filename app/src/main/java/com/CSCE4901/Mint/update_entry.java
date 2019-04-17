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

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class update_entry extends AppCompatActivity{

    private EditText title;
        private EditText des;

    private EditText customCategory;
    private boolean customEnabled = false;
        private String cat;
        private String category;
        private String categoryTemp;

        private ImageButton flag;
        private String flagged = "0";//default set to false

        private String DATE;

        private Date timestamp;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database


        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.update);//layout
            //set format to get the date
            final SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy", Locale.US);

            Objects.requireNonNull(getSupportActionBar()).hide();

            //initialize buttons
            Button cancel = findViewById(R.id.cancel_button);
            Button update = findViewById(R.id.update_button);

            //initialize editTexts
            title = findViewById(R.id.entry_title);
            customCategory = findViewById(R.id.custom_category);

            des = findViewById(R.id.entry_description);

            //initialize image button
            flag = findViewById(R.id.flag);

            //initialize calendar view
            CalendarView CAL = update_entry.this.findViewById(R.id.entry_calendar);
            Calendar now = Calendar.getInstance();
            CAL.setMaxDate(now.getTimeInMillis());

            //get intent from searchAdapter
            Intent intent = getIntent();
            final HashMap<String,String> data = (HashMap<String, String>) intent.getSerializableExtra("key");


            //initialize spinner
            Spinner CAT = findViewById(R.id.entry_category);
            final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                    R.array.categories, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CAT.setAdapter(arrayAdapter);

            CAT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    cat = parent.getItemAtPosition(position).toString();

                    if (cat.equals("Custom")){

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
                                categoryTemp = "";
                                customEnabled = false;
                            }
                            else { //show custom
                                if(categoryTemp.equals("1")){
                                    customCategory.setVisibility(View.GONE);
                                    parent.setSelection(arrayAdapter.getPosition("Lunch"));
                                    category=cat;
                                    categoryTemp = "";
                                    customEnabled = false;
                                }
                                if(categoryTemp.equals("2")) {
                                    customCategory.setVisibility(View.GONE);
                                    parent.setSelection(arrayAdapter.getPosition("Dinner"));
                                    category=cat;
                                    categoryTemp = "";
                                    customEnabled = false;
                                }
                                if(categoryTemp.equals("3")) {
                                    customCategory.setVisibility(View.GONE);
                                    parent.setSelection(arrayAdapter.getPosition("Snack"));
                                    category=cat;
                                    categoryTemp = "";
                                    customEnabled = false;
                                }
                                if(categoryTemp.equals("4")) {
                                    customCategory.setVisibility(View.VISIBLE);
                                    parent.setSelection(arrayAdapter.getPosition("Custom"));
                                    category = customCategory.getText().toString();
                                    categoryTemp = "";
                                    customEnabled = true;

                                }
                            }
                        }
                        else
                        {
                            customCategory.setVisibility(View.GONE);
                            category = cat;
                            categoryTemp = "";
                            customEnabled = false;
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

            customCategory.setText(tempCat);
            category = customCategory.getText().toString();
            categoryTemp = "4";
            customCategory.setVisibility(View.VISIBLE);

            if(tempCat.equals("Breakfast") || "Lunch".equals(tempCat)|| "Dinner".equals(tempCat) || "Snack".equals(tempCat))
            {

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
            try {
                timestamp = DATEformat.parse(DATE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                CAL.setDate(new SimpleDateFormat("M/dd/yyyy", Locale.US).parse(DATE).getTime(), true, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            //flag from OG entry
            final String tempFlag = data.get("flag");
            assert tempFlag != null;
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
                    if(Objects.equals(flagged, "0")) {
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

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String editTitle = title.getText().toString();
                    final String editDes = des.getText().toString();

                    if (editTitle.equals("") || editTitle.equals("Need Title")) {
                        title.setTextColor(Color.parseColor("#8B0000"));
                        title.setText(getString(R.string.Need_Title));

                    }
                    else {

                        if(customEnabled)
                        {
                            category=customCategory.getText().toString();
                        }
                        //get all the information in a HashMap for firestore
                        final HashMap<String, Object> updateData = new HashMap<>();
                        updateData.put("title", editTitle);
                        updateData.put("category", category);
                        updateData.put("description", editDes);
                        updateData.put("flag", flagged);
                        updateData.put("date", DATE);
                        updateData.put("timestamp", timestamp);

                        final String userEmail = data.get("email"); //get email from data hashMap in intent
                        final String ID = data.get("ID"); //get ID name of doc from data hashMap in intent

                        //update doc in database
                        //delete entry
                        //db.collection(userEmail).document(ID).delete();
                        //db.collection(userEmail).document(ID).set(updateData);
                        final DocumentReference updateDoc = db.collection(Objects.requireNonNull(userEmail)).document(Objects.requireNonNull(ID));
                        updateDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                assert document != null;
                                if (document.exists()) {
                                    updateDoc.update(updateData);
                                    //block below is for Algolia
                                    Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");

                                    final Index index = client.getIndex(userEmail);
                                    List<JSONObject> array = new ArrayList<>();

                                    try {
                                        array.add(
                                                new JSONObject().put("title", editTitle).put("objectID", ID)
                                        );
                                        array.add(
                                                new JSONObject().put("category", category).put("objectID", ID)
                                        );
                                        array.add(
                                                new JSONObject().put("description", editDes).put("objectID", ID)
                                        );
                                        array.add(
                                                new JSONObject().put("flag", flagged).put("objectID", ID)
                                        );
                                        array.add(
                                                new JSONObject().put("date", DATE).put("objectID", ID)
                                        );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    index.partialUpdateObjectsAsync(new JSONArray(array),null);

                                }
                                else {
                                    db.collection(userEmail).add(updateData);

                                    //block below is for Algolia
                                    Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");

                                    final Index index = client.getIndex(userEmail);

                                    JSONObject object = null;
                                    try {
                                        object = new JSONObject()
                                                .put("title", title)
                                                .put("category", cat)
                                                .put("description", des)
                                                .put("flag", flagged)
                                                .put("date", DATE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    assert object != null;
                                    index.addObjectAsync(object, ID, null);
                                }
                            }
                        });

                        //edit entry toast
                        Toast.makeText(update_entry.this, "Updating Entry", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = update_entry.this.getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
                        editor.putInt("CHECK",1); //set as true
                        editor.apply();
                        SharedPreferences ed = update_entry.this.getSharedPreferences("PreferencesName", MODE_PRIVATE);
                        int check = ed.getInt("CHECK",0);
                        //Toast.makeText(update_entry.this, String.format(" "+check), Toast.LENGTH_SHORT).show();
                        finish();




                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go back to home fragment
                    finish();
                }
            });

            //when date picker is change set the DATe to that chosen date
            CAL.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    int m = month+1;
                    String D = String.valueOf(dayOfMonth);
                    String M = String.valueOf(m);
                    String Y = String.valueOf(year);
                    DATE=M + "/" + D + "/" + Y;
                    try {
                        timestamp = DATEformat.parse(DATE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });



        }



}
