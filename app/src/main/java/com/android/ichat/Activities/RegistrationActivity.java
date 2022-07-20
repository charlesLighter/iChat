package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.ichat.Models.Users;
import com.android.ichat.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ActivityRegistrationBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //Initializing firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Initializing progress dialog
        progressDialog = new ProgressDialog(this);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        binding.txtLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

    }

    private void registerUser() {
        //Checking for empty text fields
        if (!binding.etUserName.getText().toString().isEmpty() && !binding.etUserEmail.getText().toString().isEmpty()
           && !binding.etUserPassword.getText().toString().isEmpty() && !binding.etUserPassword2.getText().toString().isEmpty())
        {
            //Checking if passwords match
            if (binding.etUserPassword.getText().toString().equals(binding.etUserPassword2.getText().toString()))
            {
                //Register new user
                progressDialog.setTitle("Creating Account");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(binding.etUserEmail.getText().toString(), binding.etUserPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressDialog.dismiss();
                        if (task.isSuccessful())
                        {
                            //Recording user details in the database
                            Users user = new Users(binding.etUserName.getText().toString(), binding.etUserEmail.getText().toString(), binding.etUserPassword.getText().toString());
                            String userIdentifier = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(userIdentifier).setValue(user);

                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
            {
                Toast.makeText(this, "Password don't match", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(this, "Please fill in all spaces", Toast.LENGTH_SHORT).show();
        }

    }
}