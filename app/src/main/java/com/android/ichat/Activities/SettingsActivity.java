package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.ichat.Models.Users;
import com.android.ichat.R;
import com.android.ichat.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Settings");

        //Initializing objects
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //setting user's profile picture, name and status
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                //showing the user's profile photo
                Picasso.get()
                        .load(users.getUserProfilePic())
                        .placeholder(R.drawable.default_profile_avatar)
                        .into(binding.userProfileImage);
                //showing the user name
                binding.txtUserName.setText(users.getUserName());
                //showing the user status
                binding.txtUserStatus.setText(users.getUserStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ProfileSettings.class));
            }
        });
    }
}