package com.CSCE4901.Mint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {


    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private Button loginButton;
    private Button signUpButton;

    private TextView forgotPassword;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO Remove this comment just testing git stuff more testing git
    // Test Drive
        //test commit
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);

        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_button);

        forgotPassword = findViewById(R.id.forgot_password);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(v);
                //launchOverview();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private boolean validateEmail() {

        String email = emailLayout.getEditText().getText().toString();

        if(email.isEmpty()){
            emailLayout.setError("Enter your email");
            return false;
        } else {
            emailLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = passwordLayout.getEditText().getText().toString();

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
        } else
        {

            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,
                                        "Logging In", Toast.LENGTH_SHORT).show();

                                launchOverview();
                            }
                            else {
                                Toast.makeText(MainActivity.this,
                                        "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    public void launchOverview(){
        Intent intent = new Intent(this,OverviewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }


}
