package com.CSCE4901.Mint.Entry;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EntryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //declare
    FirebaseAuth firebaseAuth;

    View view;
    EditText TITLE;

    TextView catText;
    Spinner CAT;
    EditText customCategory;
    boolean customEnabled = false;


    EditText DES;
    ImageButton FLAG;
    Button SAVE;
    CalendarView CAL;

    String flagged = "0";//default set to false
    String title;
    String cat;
    String des;

    String DATE;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_entry, container, false); //Connect the XML file with thi fragment

        //set format to get the date
        final SimpleDateFormat DATEformat = new SimpleDateFormat("M/d/yyyy");

        //initialize edittexts
        TITLE= view.findViewById(R.id.entry_title);
        DES=view.findViewById(R.id.entry_description);
        customCategory = view.findViewById(R.id.custom_category);

        //initialize textview
        catText = view.findViewById(R.id.entry_category_text);


        //initialize spinner
        CAT= view.findViewById(R.id.entry_category);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CAT.setAdapter(arrayAdapter);
        CAT.setOnItemSelectedListener(this);


        //initialize Buttons
        FLAG=view.findViewById(R.id.flag);
        SAVE=view.findViewById(R.id.entry_add_button);

        //initialize calendar view
        CAL=view.findViewById(R.id.entry_calendar);
        Calendar now = Calendar.getInstance();
        CAL.setMaxDate(now.getTimeInMillis());


        //initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get the DATE for default date
        DATE = DATEformat.format(new Date(CAL.getDate()));

        SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get text field values
                title = TITLE.getText().toString();
                des = DES.getText().toString();

                if(title.equals("") || title.equals("Need Title"))
                {
                    TITLE.setTextColor(Color.parseColor("#8B0000"));
                    TITLE.setText("Need Title");

                }
                else {
                    //only get custom category if it is selected from the category spinner
                    if(customEnabled){
                        cat = customCategory.getText().toString().trim();
                    }
                    //get all the information in a HashMap
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("title", title);
                    data.put("category", cat);
                    data.put("description", des);
                    data.put("flag", flagged);
                    data.put("date", DATE);

                    //get email of signed in user
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    final String UserEmail = currentUser.getEmail();

                    assert UserEmail != null;
                    db.collection(UserEmail).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Create Entry", "DocumentSnapshot added with ID: " + documentReference.getId());

                            Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");

                            final Index index = client.getIndex(UserEmail);

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
                            index.addObjectAsync(object, documentReference.getId(), null);
                            Toast.makeText(getContext(), "Entry Added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), "Error Creating Entry", Toast.LENGTH_SHORT).show();
                            Log.d("CREATE ENTRY", "OnFailure", e);
                        }
                    });
                    //clear out edit texts
                    TITLE.setText("");
                   // CAT.setText("");
                    DES.setText("");
                    //set spinner to position 0
                    CAT.setSelection(0);
                    TITLE.setTextColor(Color.BLACK);
                }


            }
        });

        FLAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if flag button is not pushed
                if(flagged == "0") {
                    //change color to YELLOW
                    FLAG.setColorFilter(Color.parseColor("#CCCC00"));
                    //FLAG.setBackgroundColor(Color.parseColor("#CCCC00"));
                    flagged = "1";
                }
                //else if flag button has already been pushed AKA flagged =="1"
                else
                {
                    //change button color back to normal non pushed
                    FLAG.setColorFilter(Color.parseColor("#696969"));
                    //FLAG.setBackgroundColor(Color.parseColor("#696969"));
                    flagged = "0";
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
                DATE=M + "/" + D + "/" + Y;
            }
        });

       return view;
    }


    //for category spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), cat, Toast.LENGTH_SHORT).show();
        if (cat.equals("Custom")){
            customCategory.setVisibility(View.VISIBLE);
            customEnabled = true;
        } else {
            customCategory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
