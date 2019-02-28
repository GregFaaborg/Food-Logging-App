package com.CSCE4901.Mint.Entry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.CSCE4901.Mint.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EntryFragment extends Fragment {

    //declare
    FirebaseAuth firebaseAuth;

    View view;
    TextView TITLE;
    TextView CAT;
    TextView DES;
    Button FLAG;
    Button SAVE;
    CalendarView CAL;

    String Flagged="0";//default set to false
    String title;
    String cat;
    String des;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_entry, container, false); //Connect the XML file with thi fragment

        //set format to get the date
        final SimpleDateFormat DATEformat = new SimpleDateFormat("MM/dd/yyyy");

        //initialize textviews
        TITLE= view.findViewById(R.id.entry_title);
        CAT= view.findViewById(R.id.entry_category);
        DES=view.findViewById(R.id.entry_description);


        //initialize Buttons
        FLAG=view.findViewById(R.id.flag);
        SAVE=view.findViewById(R.id.entry_add_button);

        //initialize calendar view
        CAL=view.findViewById(R.id.entry_calendar);

        //initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the DATE
                String DATE = DATEformat.format(new Date(CAL.getDate()));

                //get text field values
                title = TITLE.getText().toString();
                cat = CAT.getText().toString();
                des = DES.getText().toString();

                //get all the information in a HashMap
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("category", cat);
                data.put("description", des);
                data.put("flag",Flagged );
                data.put("date", DATE);

                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();


                //save text views and flag button to database in user email collection
                db.collection(UserEmail).add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Create Entry", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(getContext(), "Entry Added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getContext(), "Error Creating Entry", Toast.LENGTH_SHORT).show();
                                Log.d("CREATE ENTRY", "OnFailure" ,e);
                            }
                        });
            }
        });

        FLAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MAKE FLAG BUTTON INTO STAR B4 END OF SPRINT 1
                //if flag button is not pushed
                if(Flagged=="0") {
                    Flagged = "1";
                }
                //else if flag button has already been pushed AKA flagged =="1"
                else
                {
                    ////change button color to normal non pushed
                    Flagged="0";
                }
            }
        });



       return view;
    }
}
