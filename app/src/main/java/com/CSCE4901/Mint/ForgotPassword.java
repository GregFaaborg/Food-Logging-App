package com.CSCE4901.Mint;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout tilForgetPasswordEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button send_reset = findViewById(R.id.forgot_password_button);

        tilForgetPasswordEmail = findViewById(R.id.forgot_password_layout);

        send_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetEmail();
            }
        });
    }

    private boolean validateEmail() {

        String email = tilForgetPasswordEmail.getEditText().getText().toString().trim();

        if(email.isEmpty()) {
            tilForgetPasswordEmail.setError("Email Required");
            return false;
        } else {
            tilForgetPasswordEmail.setError(null);
            return true;
        }
    }

    private void sendResetEmail() {

        if(!validateEmail()) {
            return;

        } else {

            String email = tilForgetPasswordEmail.getEditText().getText().toString().trim();

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this,
                                        "Email Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                try {
                                    throw task.getException();

                                } catch (FirebaseAuthEmailException e) {

                                    Toast.makeText(ForgotPassword.this,
                                            "Error Sending Email", Toast.LENGTH_SHORT).show();
                                    Log.d("Password Reset", e.getMessage());

                                } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {

                                    Toast.makeText(ForgotPassword.this,
                                            "Malformed email, try again.", Toast.LENGTH_SHORT).show();
                                    Log.d("Bad Email", "onComplete: malformed_email");

                                } catch (FirebaseAuthInvalidUserException invalidEmail){

                                    Toast.makeText(ForgotPassword.this,
                                            "Email not found", Toast.LENGTH_SHORT).show();
                                    Log.d("Invalid Email", invalidEmail.getMessage());
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }


}
