package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.ichat.Adapters.ChatAdapter;
import com.android.ichat.Adapters.UserAdapter;
import com.android.ichat.Models.MessageModel;
import com.android.ichat.Models.Users;
import com.android.ichat.R;
import com.android.ichat.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {
    private ActivityChatDetailsBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //Back arrow
        binding.btnArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        //Contact profile
        binding.contactProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passing contacts data to the next activity


            }
        });

        //Initializing Objects
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //Fetching username and profile picture
        final String senderId = mAuth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String userStatus = getIntent().getStringExtra("userStatus");
        String userEmail = getIntent().getStringExtra("userEmail");
        String userProfilePic = getIntent().getStringExtra("userProfilePic");


        //Displaying the contacts name
        binding.txtUserName.setText(userName);
        //displaying contacts status
        binding.txtUserStatus.setText(userStatus);
        //Showing contacts profile picture
        Picasso.get()
                .load(userProfilePic)
                .placeholder(R.drawable.default_profile_avatar)
                .into(binding.userProfileImage);



        //Objects related to chat
        final ArrayList<MessageModel> messageModel = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModel, this, receiverId);

        //Setting adapter to the recycler view
        binding.chatsRecycler.setAdapter(chatAdapter);
        //Setting a layout manager for the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatDetailsActivity.this);
        binding.chatsRecycler.setLayoutManager(linearLayoutManager);

        /* Creating chat rooms for receiver and sender
         * Used to identify which user is the sender and which is the receiver
         */
        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;



        //Initializing emoji pop up
        EmojiPopup emojiPopup = EmojiPopup.Builder
                        .fromRootView(findViewById(R.id.root_View))
                                .build(binding.etUserMessage);
        binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();//showing the emojis

                //TODO: Changing the icon when emojis are showing
                while (emojiPopup.isShowing())
                {

                    binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Showing the keyboard again
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(view, 0);

                        }

                    });
                    break;
                }




            }
        });



        //Sending the Message
        binding.btnSendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Extracting the input message
                String userMessage = binding.etUserMessage.getText().toString();

                //Condition to disable the user from sending a blank message
                if(!userMessage.isEmpty())
                {
                    final  MessageModel model = new MessageModel(senderId, userMessage);
                    //Getting the time in which the message was sent
                    model.setTimestamp(new Date().getTime());
                    binding.etUserMessage.getText().clear();

                    //Storing the message in the database
                    database.getReference()
                            .child("Chats")
                            .child(senderRoom)
                            .push()
                            .setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    //store a copy of the sent message inside the receiver room
                                    database.getReference()
                                            .child("Chats")
                                            .child(receiverRoom)
                                            .push()
                                            .setValue(model)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    //Error message
                                    Toast.makeText(ChatDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        //Showing messages on the recycler view
        database.getReference()
                .child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        messageModel.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());
                            messageModel.add(model);

                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Showing contacts profile
        binding.contactProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Passing contact's data to the next activity
                String contactName = userName;
                String contactStatus = userStatus;
                String contactEmail = userEmail;
                String contactProfilePic = userProfilePic;

                Intent intent = new Intent(ChatDetailsActivity.this, ContactsProfileView.class);
                intent.putExtra("contact_name", contactName);
                intent.putExtra("contact_email", contactEmail);
                intent.putExtra("contact_status", contactStatus);
                intent.putExtra("contact_profilePic", contactProfilePic);
                startActivity(intent);

            }
        });
    }
}