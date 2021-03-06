package com.CSCE4901.Mint;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();

        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);

        Button loginButton = findViewById(R.id.login_button);
        Button signUpButton = findViewById(R.id.signup_button);

        TextView forgotPassword = findViewById(R.id.forgot_password);

        checkPlayServicesVersion();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });

    }

    private boolean validateEmail() {

        String email = Objects.requireNonNull(emailLayout.getEditText()).getText().toString().trim();

        if(email.isEmpty()) {
            emailLayout.setError("Enter your email");
            return false;
        } else {
            emailLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString().trim();

        if(password.isEmpty()) {
            passwordLayout.setError("Enter your password");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }

    }


    private void validate() {

        if(!validateEmail() | !validatePassword()){
            return;
        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing in");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            //login user
            String email = Objects.requireNonNull(emailLayout.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString().trim();

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


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) if they are launch overview activity

        if (isNetworkAvailable(getApplicationContext())){
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                launchOverview();
            }
        }
        else {

            showSnackIfOffline();
        }
    }

    private void launchOverview(){
        Intent intent = new Intent(this,OverviewActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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

    private void showSnackIfOffline(){
        final boolean online = isOnline();
        runOnUiThread(new TimerTask() { //must run on main thread to update UI (show Snackbar), can be used only in Activity (FragmentActivity, AppCompatActivity...)
            @Override
            public void run() {
                if(!online)
                    Snackbar.make(findViewById(android.R.id.content), "Internet Connection Required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //when dismissed is clicked hide snackbar
                        }
                    })
                            .show();
                    //do nothing, continue like normal

            }
        });
    }

    private boolean isOnline(){
        try {
            return Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8").waitFor() == 0; //  "8.8.8.8" is the server to ping
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
