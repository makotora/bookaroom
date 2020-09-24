package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bookaroom.R;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;

public class ListingActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_LISTING_ID_NAME = "EXTRA_LISTING_ID";
    public static final long LISTING_ID_DEFAULT_VALUE = -1;
    private Long listingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        listingId = getIntent().getLongExtra(INTENT_EXTRA_LISTING_ID_NAME,
                                             LISTING_ID_DEFAULT_VALUE);
        if (listingId == LISTING_ID_DEFAULT_VALUE) {
            Utils.makeInternalErrorToast(this);
            NavigationUtils.startHomeActivity(this);
        }

        System.out.println("RECEIVED LISTING ID " + listingId);

        NavigationUtils.initializeBottomNavigationBar(this);
    }
}