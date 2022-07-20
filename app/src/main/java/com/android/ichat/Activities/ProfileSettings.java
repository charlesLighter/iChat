package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.ichat.Models.Users;
import com.android.ichat.R;
import com.android.ichat.databinding.ActivityProfileSettingsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileSettings extends AppCompatActivity {
    private ActivityProfileSettingsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Profile Settings");

        //Initializing objects
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(ProfileSettings.this);

        //Setting user's profile permanently on the views
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                //profile picture
                Picasso.get()
                        .load(users.getUserProfilePic())
                        .placeholder(R.drawable.default_profile_avatar)
                        .into(binding.userProfileImage);
                //user name
                binding.txtUserName.setText(users.getUserName());
                //user status
                binding.txtUserStatus.setText(users.getUserStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        binding.btnSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserStatus();
            }
        });

        binding.btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName();
            }
        });


    }

    //For changing user's name
    private void changeUserName() {
        String newUserName = binding.etChangeUsername.getText().toString().trim();
        if(!newUserName.isEmpty())
        {
            HashMap<String, Object> nameObject = new HashMap<>();
            nameObject.put("userName", newUserName);

            //Saving the new username in the database
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(nameObject);
            Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show();
            binding.etChangeUsername.getText().clear();
        }
        else
        {
            Toast.makeText(this, "Please enter new username", Toast.LENGTH_SHORT).show();
        }
    }

    //For updating user's status
    private void updateUserStatus() {
        String newUserStatus = binding.etChangeUserStatus.getText().toString().trim();
        if(!newUserStatus.isEmpty())
        {
            HashMap<String, Object> statusObject = new HashMap<>();
            statusObject.put("userStatus", newUserStatus);

            //Saving the userStatus in the database
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(statusObject);
            Toast.makeText(this, "status updated successfully", Toast.LENGTH_SHORT).show();
            binding.etChangeUserStatus.getText().clear();

        }
        else
        {
            Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
        }

    }

    //For selecting photos from gallery
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    //For displaying selected Image and saving it in the database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            //Displaying selected image
            Uri selectedImage = data.getData();
            binding.userProfileImage.setImageURI(selectedImage);

            //Storing the image in the database
            final StorageReference reference = storage.getReference().child("profile_photos").child(FirebaseAuth.getInstance().getUid());
            reference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                         database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("userProfilePic").setValue(uri.toString());
                        }
                    });
                    progressDialog.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //getting the error message
                    Toast.makeText(ProfileSettings.this, e.toString(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                {
                    //Showing progress dialog
                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }

    }
}