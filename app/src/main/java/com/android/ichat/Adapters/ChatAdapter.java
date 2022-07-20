package com.android.ichat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ichat.Models.MessageModel;
import com.android.ichat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messageModel;
    Context context;
    String receiverId;
    Long timeStamp;

    //for identifying the sender and the receiver in the chat
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModel, Context context) {
        this.messageModel = messageModel;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModel, Context context, String receiverId) {
        this.messageModel = messageModel;
        this.context = context;
        this.receiverId = receiverId;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModel, Context context, Long timeStamp) {
        this.messageModel = messageModel;
        this.context = context;
        this.timeStamp = timeStamp;
    }

    //For inflating the xml message layout on the recyclerview
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sent_message, parent, false);
            return  new SenderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_received_message, parent, false);
            return  new ReceiverViewHolder(view);
        }
    }

    //For positioning the message bubble
    //Returns a view type indicating whether the message is from the sender(Current logged in user) or the receiver
    @Override
    public int getItemViewType(int position) {
       if (messageModel.get(position).getUser_id().equals(FirebaseAuth.getInstance().getUid()))
       {
           return SENDER_VIEW_TYPE;
       }
       else {
           return RECEIVER_VIEW_TYPE;
       }
    }

    //For showing the sent and received messages on the bubbles
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messages = messageModel.get(position);
        if (holder.getClass() == SenderViewHolder.class)
        {
            ((SenderViewHolder) holder).sentMessage.setText(messages.getMessage()); //Sent messages

            //Showing the time at which the message was sent
            Date date = new Date(messages.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH : mm");
            String time = simpleDateFormat.format(date);
            ((SenderViewHolder) holder).sentTime.setText(time);

        }else {
            ((ReceiverViewHolder) holder).receivedMessage.setText(messages.getMessage()); //Received message
            //Showing the time at which the message was received
            Date date = new Date(messages.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH : mm");
            String time = simpleDateFormat.format(date);
            ((ReceiverViewHolder) holder).receivedTime.setText(time);
        }

        //Deleting a message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                                database.getReference()
                                        .child("Chats")
                                        .child(senderRoom)
                                        .child(messages.getMessageId())
                                        .setValue(null);
                            }
                        })
                        .setNegativeButton("no", null)
                        .show();
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return messageModel.size();
    }
    //View holder class for received messages
    public class ReceiverViewHolder extends RecyclerView.ViewHolder
    {
        TextView receivedMessage, receivedTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receivedMessage = itemView.findViewById(R.id.txt_receivedMessage);
            receivedTime = itemView.findViewById(R.id.txt_receivedTime);
        }
    }

    //View holder class for sent messages
    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView sentMessage, sentTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessage = itemView.findViewById(R.id.txt_sentMessage);
            sentTime = itemView.findViewById(R.id.txt_sentTime);
        }
    }
}
