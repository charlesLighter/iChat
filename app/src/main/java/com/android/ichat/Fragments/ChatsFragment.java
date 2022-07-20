package com.android.ichat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ichat.Adapters.UserAdapter;
import com.android.ichat.Models.Users;
import com.android.ichat.R;
import com.android.ichat.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private ArrayList<Users> usersList = new ArrayList<>();
    private FirebaseDatabase database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        //initializing Objects
        database = FirebaseDatabase.getInstance();
        UserAdapter adapter = new UserAdapter(usersList, getContext());

        //setting adapter to the recyclerview
        binding.chatsRecycler.setAdapter(adapter);

        //Setting a layout manager for the recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatsRecycler.setLayoutManager(layoutManager);

        //Getting all users
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    users.setUserId(dataSnapshot.getKey());

                    //Removing the current signed in user from the list of users on the recycler view
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        usersList.add(users); //showing users
                    }


                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        return binding.getRoot();
    }
}