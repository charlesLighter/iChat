package com.android.ichat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ichat.Activities.ChatDetailsActivity;
import com.android.ichat.Models.MessageModel;
import com.android.ichat.Models.Users;
import com.android.ichat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<Users> usersList; //list of registered users
    Context context;

    public UserAdapter(ArrayList<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    public UserAdapter(ArrayList<Users> usersList) {
        this.usersList = usersList;
    }

    //inflating the sample user on the recycler view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_user, parent, false);
        return new ViewHolder(view);
    }

    //Getting all registered users from the database
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Users users = usersList.get(position);

        //showing profile picture
        Picasso.get().load(users.getUserProfilePic()).placeholder(R.drawable.default_profile_avatar).into(holder.userProfileImage);
        //showing user name
        holder.userName.setText(users.getUserName());

        //Setting the last message
        FirebaseDatabase.getInstance().getReference().child("Chats")
                        .child(FirebaseAuth.getInstance()
                        .getUid() + users.getUserId())
                        .orderByChild("timestamp")
                        .limitToLast(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren())
                                {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                                    {
                                        holder.lastMessage.setText(snapshot1.child("message").getValue().toString());

                                        //Setting the time


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        //opening the chat details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                //Passing the contacts data
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("userProfilePic", users.getUserProfilePic());
                intent.putExtra("userName", users.getUserName());
                intent.putExtra("userStatus", users.getUserStatus());
                intent.putExtra("userEmail", users.getUserEmail());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return usersList.size();
    }

    //class for fetching xml views
    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userProfileImage;
        TextView userName, lastMessage, r_s_time;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profileImage);
            userName = itemView.findViewById(R.id.txt_userName);
            lastMessage = itemView.findViewById(R.id.txt_lastMessage);
            r_s_time = itemView.findViewById(R.id.txt_time);


        }
    }
}
