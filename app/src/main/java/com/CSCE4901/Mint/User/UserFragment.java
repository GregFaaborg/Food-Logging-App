package com.CSCE4901.Mint.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.CSCE4901.Mint.MainActivity;
import com.CSCE4901.Mint.R;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Paragraph;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class UserFragment extends Fragment implements View.OnClickListener {

    private TextView first;
    private TextView last;
    private TextView emailDB;
    private TextView docEmail;

    private String email;
    private String FIRST="first name";
    private String LAST="last name";
    private String docEMAIL="docs email";

    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); //point db to the root directory of the database


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //initialize textviews
        first= view.findViewById(R.id.first);
        last= view.findViewById(R.id.last);
        emailDB= view.findViewById(R.id.email);
        docEmail= view.findViewById(R.id.doc);
        Button del = view.findViewById(R.id.delete_button);

        //initialize Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get email of signed in user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        email = Objects.requireNonNull(currentUser).getEmail();


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
        Button logOutButton = view.findViewById(R.id.logout_button);
        logOutButton.setOnClickListener(this);

        //update
        Button update = view.findViewById(R.id.update_button);
        update.setOnClickListener(this);

        //delete account
        del.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.delete_button:
                //delete doc in user named of logged in user
                DocumentReference userInfo = db.collection("users").document(email);
                userInfo.delete();

                db.collection(email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                DocumentReference doc = db.collection(email).document(document.getId());
                                doc.delete();

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

                //delete authentication
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                assert currentUser != null;
                String emailIndex = currentUser.getEmail();

                currentUser.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted. ");
                                }
                            }
                        });

                //delete index from algolia
                Client client = new Client("SPV08Z7AV0", "adee0fbb15896a566a5ac1a39e322bb4");
                assert emailIndex != null;
                Index index = client.getIndex(emailIndex);
                index.clearIndexAsync(null);
                client.deleteIndexAsync(emailIndex, null);
                Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();


            case R.id.logout_button:

                //send user to login screen
                Intent intent = new Intent(getActivity(), MainActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                getActivity().finish();
                Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.update_button:
                //update button pushed update the DB and have the new info in the user info view/page

                //delete OG email named doc
                db.collection("users").document(email).delete();

                //get strings in from the text views

                FIRST=first.getText().toString();
                LAST=last.getText().toString();
                docEMAIL=docEmail.getText().toString();


                //get all the information in a HashMap
                Map<String, Object> data = new HashMap<>();
                data.put("firstName", FIRST);
                data.put("lastName", LAST);
                //data.put("email", email);
                data.put("doctorEmail",docEMAIL);

                db.collection("users").document(email).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Info Updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Error Updating Info", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        }


    }
}
