package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.whatsapp.ChatListFragment;
import com.example.whatsapp.KomunitasFragment;
import com.example.whatsapp.PanggilanFragment;
import com.example.whatsapp.PembaruanFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_chat) {
                selectedFragment = new ChatListFragment();
            } else if (item.getItemId() == R.id.navigation_pembaruan) {
                selectedFragment = new PembaruanFragment();
            } else if (item.getItemId() == R.id.navigation_komunitas) {
                selectedFragment = new KomunitasFragment();
            } else if (item.getItemId() == R.id.navigation_panggilan) {
                selectedFragment = new PanggilanFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
    }
}