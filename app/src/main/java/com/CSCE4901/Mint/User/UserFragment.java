package com.CSCE4901.Mint.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.CSCE4901.Mint.MainActivity;
import com.CSCE4901.Mint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserFragment extends Fragment implements View.OnClickListener {

    View view;
    Button logOutButton;
    Button update;
    TextView first;
    TextView last;
    EditText emailDB;
    TextView docEmail;
   /* EditText etNewEmail;*/

    String email;
    String FIRST="first name";
    String LAST="last name";
    String docEMAIL="docs email";

    /*FirebaseAuth mAuth;
    FirebaseUser fbUser; */

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database
    /* Context mContext; */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);

        //initialize textviews
        first=view.findViewById(R.id.first);
        last=view.findViewById(R.id.last);
        emailDB=view.findViewById(R.id.email);
        docEmail=view.findViewById(R.id.doc);

        //Update for email
        /* etNewEmail=view.findViewById(R.id.user_email);
        update=view.findViewById(R.id.update_button);


        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser(); */

        //initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get email of signed in user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        email = currentUser.getEmail();


        //create a doc reference in the user collection to the email doc
        DocumentReference userInfo = db.collection("users").document(email);
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            //If the user is able to get data from the database
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Check if the document exists in the database
                if (documentSnapshot.exists()) {
                    //get document fields
                    FIRST=documentSnapshot.getString("firstName");
                    LAST=documentSnapshot.getString("lastName");
                    docEMAIL=documentSnapshot.getString("doctorEmail");
                    //output the data in the text views of the page
                    first.setText(FIRST);
                    last.setText(LAST);
                    docEmail.setText(docEMAIL);
                    emailDB.setText(email);
                }
            }
        });

        //logout
        logOutButton = view.findViewById(R.id.logout_button);
        logOutButton.setOnClickListener(this);

        //update
        update=view.findViewById(R.id.update_button);
        update.setOnClickListener(this);

        return view;
    }
    
    /*private void setUpdate() {

        String oldEmail = emailDB.getText().toString();
        String newEmail = etNewEmail.getText().toString();

        if (TextUtils.isEmpty(oldEmail)) {

            Toast.makeText(mContext, "Please Enter Your Old Email", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(newEmail)) {

            Toast.makeText(mContext, "Please Enter Your New Email", Toast.LENGTH_SHORT).show();

        } else {

            String email = fbUser.getEmail();

            if (!oldEmail.equals(email)) {

                Toast.makeText(mContext, "Wrong Current Email, Please Check Again", Toast.LENGTH_SHORT).show();

            } else {

                fbUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            fbUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(mContext, "Verification Email Sent To Your Email.. Please Verify and Login", Toast.LENGTH_LONG).show();

                                    // Logout From Firebase
                                    FirebaseGeneral firebaseGeneral = new FirebaseGeneral();
                                    firebaseGeneral.logoutUser(mContext);

                                }
                            });

                        } else {

                            try {
                                throw Objects.requireNonNull(task.getException());
                            }

                            // Invalid Email
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Toast.makeText(mContext, "Invalid Email...", Toast.LENGTH_LONG).show();

                            }
                            // Email Already Exists
                            catch (FirebaseAuthUserCollisionException existEmail) {
                                Toast.makeText(mContext, "Email Used By Someone Else, Please Give Another Email...", Toast.LENGTH_LONG).show();

                            }
                            // Any Other Exception
                            catch (Exception e) {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }

                    }
                });

            }

        }

    } */

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
            case R.id.update_button:
                //update button pushed update the DB and have the new info in the user info view/page

                //delete OG email named doc
                db.collection("users").document(email).delete();

                //get strings in from the text views
                //email=emailDB.getText().toString();
                FIRST=first.getText().toString();
                LAST=last.getText().toString();
                docEMAIL=docEmail.getText().toString();
                //TOOOOO DOOOOO
                //check if docEmail is valid in firebase auth


                //get all the information in a HashMap
                Map<String, Object> data = new HashMap<>();
                data.put("firstName", FIRST);
                data.put("lastName", LAST);
                //data.put("email", email);
                data.put("doctorEmail",docEMAIL);

                //db.collection("users").document(email).set(data);
                //TOOOOO DOOOOO
                //do the firebase auth email update


        }


    }
}
