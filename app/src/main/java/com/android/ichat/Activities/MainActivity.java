package com.android.ichat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.ichat.Fragments.CallsFragment;
import com.android.ichat.Fragments.ChatsFragment;
import com.android.ichat.Fragments.GroupsFragment;
import com.android.ichat.Fragments.StoriesFragment;
import com.android.ichat.R;
import com.android.ichat.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Setting default fragment at runtime
        replaceFragment(new ChatsFragment());
        getSupportActionBar().setTitle("Chats");

        //Initializing objects
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //displaying fragments
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.chats:
                        replaceFragment(new ChatsFragment());
                        getSupportActionBar().setTitle("Chats");
                        break;
                    case R.id.groups:
                        replaceFragment(new GroupsFragment());
                        getSupportActionBar().setTitle("Groups");
                        break;
                    case R.id.stories:
                        replaceFragment(new StoriesFragment());
                        getSupportActionBar().setTitle("Stories");
                        break;
                    case R.id.calls:
                        replaceFragment(new CallsFragment());
                        getSupportActionBar().setTitle("Calls");
                        break;
                }
                return false;
            }
        });

    }


    //For inflating the main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //For getting main menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.groupChat)
        {
            replaceFragment(new GroupsFragment());
        }
        else if (id == R.id.settings)
        {
           Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
           startActivity(intent);
        }
        else if (id == R.id.logout)
        {
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    //For navigating users from one fragment to the next
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    //For logging out users
    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Confirm you want to quit application");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
        builder.show();

    }
}