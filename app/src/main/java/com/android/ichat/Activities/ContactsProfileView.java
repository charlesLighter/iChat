package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.ichat.Models.Users;
import com.android.ichat.R;
import com.android.ichat.databinding.ActivityContactsProfileViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContactsProfileView extends AppCompatActivity {
    private ActivityContactsProfileViewBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        showContactDetails();


    }

    //For showing contact's profile
    private void showContactDetails() {
        String contactName = getIntent().getStringExtra("contact_name");
        String contactEmail = getIntent().getStringExtra("contact_email");
        String contactStatus = getIntent().getStringExtra("contact_status");
        String contactProfilePic = getIntent().getStringExtra("contact_profilePic");

        Picasso.get()
                .load(contactProfilePic)
                .placeholder(R.drawable.default_profile_avatar)
                .into(binding.userProfileImage);
        binding.txtUserName.setText(contactName);
        binding.txtUserEmail.setText(contactEmail);
        binding.txtUserStatus.setText(contactStatus);
    }

}