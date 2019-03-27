package com.CSCE4901.Mint.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.CSCE4901.Mint.MainActivity;
import com.CSCE4901.Mint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment implements View.OnClickListener {

    View view;
    Button logOutButton;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database

    //get email of signed in user
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    String Email = currentUser.getEmail();

    @IgnoreExtraProperties
    public class Post {

        public String FirstName;
        public String Lastname;
        public String Email;
        public String DoctorsEmail;

        DatabaseMetaData mDatabase;


        public int starCount = 0;
        Map<String, Boolean> stars = new HashMap<>();


        public Post(String firstName, String lastName, String doctorsEmail, String email) {
            this.FirstName = firstName;
            this.Lastname = lastName;
            this.DoctorsEmail = doctorsEmail;
            this.Email = email;
        }
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("firstName", FirstName);
            result.put("lastName", Lastname);
            result.put("email", Email);
            result.put("doctorsEmail", DoctorsEmail);
            result.put("starCount", starCount);
            result.put("stars", stars);

            return result;
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);

        logOutButton = view.findViewById(R.id.logout_button);
        logOutButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.logout_button:

                //send user to login screen
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();

                FirebaseAuth.getInstance().signOut();
                break;
            //case  update info button
        }


    }
}
