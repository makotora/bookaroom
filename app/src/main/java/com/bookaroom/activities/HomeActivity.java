package com.bookaroom.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bookaroom.R;
import com.bookaroom.utils.NavigationUtils;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationUtils.initializeBottomNavigationBar(this);
    }
}