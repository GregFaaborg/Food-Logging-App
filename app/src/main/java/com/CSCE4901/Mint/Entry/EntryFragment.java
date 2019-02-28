package com.CSCE4901.Mint.Entry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.CSCE4901.Mint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

        //initialize textviews
        TITLE= view.findViewById(R.id.entry_title);
        title = TITLE.getText().toString();
        CAT= view.findViewById(R.id.entry_category);
        cat = CAT.getText().toString();
        DES=view.findViewById(R.id.entry_description);
        des = DES.getText().toString();
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
                //get all the information in a HashMap
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("category", cat);
                data.put("description", des);
                data.put("flag",Flagged );
                //get email of signed in user
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String UserEmail = currentUser.getEmail();
                //save text views and flag button to database
                db.collection(UserEmail).document("first entry").set(data);
            }
        });

        FLAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make flag button to yellow ALSO MAKE IT TO STAR B4 END OF SPRINT 1
                if(Flagged=="0") {
                    Flagged = "1";
                }
                if (Flagged=="1")
                {
                    Flagged="0";
                }
            }
        });

       return view;
    }
}
