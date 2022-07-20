package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.ichat.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //Validation for keeping users logged in at run time
        if (mAuth.getCurrentUser()!= null)
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

        //Initializing objects
        progressDialog = new ProgressDialog(LoginActivity.this);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLogin();
            }
        });

        binding.registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        binding.googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();

            }
        });

        binding.facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookSignIn();
            }
        });



    }

    //For signing in users with email and password
    private void emailLogin() {
        //checking for empty inputs
        if (!binding.etUserEmail.getText().toString().isEmpty() && !binding.userPassword.getText().toString().isEmpty())
        {
            progressDialog.setTitle("Signing in");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(binding.etUserEmail.getText().toString(), binding.userPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            progressDialog.dismiss();
                            if (task.isSuccessful())
                            {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else
        {
            Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
        }
    }

    //For signing in users with google
    private void googleSignIn() {

    }

    //For signing in users with Facebook
    private void facebookSignIn() {
    }
}