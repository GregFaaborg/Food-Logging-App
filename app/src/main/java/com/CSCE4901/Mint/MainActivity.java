package com.CSCE4901.Mint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.pm.PackageInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {


    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    //todo
    private TextView forgotPassword;


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);

        Button loginButton = findViewById(R.id.login_button);
        Button signUpButton = findViewById(R.id.signup_button);

        forgotPassword = findViewById(R.id.forgot_password);

        checkPlayServicesVersion();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(v);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private boolean validateEmail() {

        String email = emailLayout.getEditText().getText().toString().trim();

        if(email.isEmpty()) {
            emailLayout.setError("Enter your email");
            return false;
        } else {
            emailLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = passwordLayout.getEditText().getText().toString().trim();

        if(password.isEmpty()) {
            passwordLayout.setError("Enter your password");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }

    }


    public void validate(View view) {

        if(!validateEmail() | !validatePassword()){
            return;
        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing in");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            //login user
            String email = emailLayout.getEditText().getText().toString().trim();
            String password = passwordLayout.getEditText().getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                launchOverview();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this,
                                        "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    private void launchOverview(){
        Intent intent = new Intent(this,OverviewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) if they are launch overview activity
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            launchOverview();
        }
    }

    private void checkPlayServicesVersion(){

        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int status = googleApiAvailability .isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            //Status that you are interested is SERVICE_VERSION_UPDATE_REQUIRED
            final Dialog dialog = googleApiAvailability.getErrorDialog(this,status, 1);
            dialog.show();
        }

    }

}
