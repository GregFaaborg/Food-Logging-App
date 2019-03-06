package com.CSCE4901.Mint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout tilFirstName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    private Button signUpButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tilFirstName = findViewById(R.id.first_name_layout);
        tilLastName = findViewById(R.id.last_name_layout);
        tilEmail = findViewById(R.id.user_email_layout);
        tilPassword = findViewById(R.id.user_password_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpButton = findViewById(R.id.signup);
        cancelButton = findViewById(R.id.cancel);


        //progress for creating account
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        //if cancel button is pushed go back to main activity (Login Screen)
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goBackIntent);
                finish();
            }
        });


        //validate fields if all are valid create account and go to overview activity
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAll();
            }
        });
    }

    private boolean validateFirstName() {

        String firstName = tilFirstName.getEditText().getText().toString().trim();

        if(firstName.isEmpty()) {
            tilFirstName.setError("Enter your first name");
            return false;
        } else {
            tilFirstName.setError(null);
            return true;
        }
    }


    private boolean validateLastName() {

        String lastName = tilLastName.getEditText().getText().toString().trim();

        if(lastName.isEmpty()) {
            tilLastName.setError("Enter your last name");
            return false;
        } else {
            tilLastName.setError(null);
            return true;
        }
    }


    private boolean validateEmail() {

        String email = tilEmail.getEditText().getText().toString().trim();

        if(email.isEmpty()){
            tilEmail.setError("Enter your email");
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = tilPassword.getEditText().getText().toString().trim();

        if(password.isEmpty()) {
            tilPassword.setError("Enter your password");
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }

    private void validateAll()
    {
        if(!validateFirstName() | !validateLastName() | !validateEmail() | !validatePassword()){
            return;
        } else {

            final String firstName = tilFirstName.getEditText().getText().toString().trim();
            final String lastName = tilLastName.getEditText().getText().toString().trim();
            String email = tilEmail.getEditText().getText().toString().trim();
            String password = tilPassword.getEditText().getText().toString().trim();




            String fullName = firstName + " " + lastName;
            createAccount(email, password, fullName);
        }

    }

    private void AddUserToDB(String firstName, String lastName, String email){

        //create user entity
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("doctorEmail", "");


        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AddUserToDB Success", "DocumentSnapshot added with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AddUserToDB Failure", "Error adding document", e);
                    }
                });
    }

    private void createAccount(final String email, String password, final String fullName) {


        final String[] splitFullName = fullName.split("\\s+");

        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //add first and last name to user profile
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();

                            if (user != null) {
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this,
                                                            "Account Created", Toast.LENGTH_SHORT).show();
                                                    Log.d("User name add", "User profile updated.");


                                                    //add first name, last name, and user email to database
                                                    AddUserToDB(splitFullName[0],splitFullName[1],email);
                                                    launchOverview();
                                                }
                                            }
                                        });
                            }
                        } else {

                            progressDialog.dismiss();

                            try {
                                throw task.getException();
                                
                            } catch (FirebaseAuthUserCollisionException existEmail) {

                                Log.d("Email Exists", "onComplete: Email collision");

                                Toast.makeText(SignUpActivity.this,
                                        "Email already exists, try again.", Toast.LENGTH_SHORT).show();


                            } catch (FirebaseAuthWeakPasswordException weakPassword) {

                                Log.d("Weak Password", "onComplete: malformed_email");

                                Toast.makeText(SignUpActivity.this, weakPassword.getReason(), Toast.LENGTH_SHORT).show();

                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {

                                Log.d("Bad Email", "onComplete: malformed_email");

                                Toast.makeText(SignUpActivity.this,
                                        "Malformed email, try again.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {

                                Toast.makeText(SignUpActivity.this,
                                        "Creation Error, try again,", Toast.LENGTH_SHORT).show();

                                Log.d("Create Account Error", "onComplete: " + e.getMessage());
                            }

                        }
                    }
                });
    }

    private void launchOverview() {
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}
