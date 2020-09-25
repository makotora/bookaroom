package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bookaroom.R;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;

public class HostProfileActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_HOST_ID_NAME = "EXTRA_HOST_ID";
    private static final Long HOST_ID_DEFAULT_VALUE = -1L;

    private long hostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_profile);

        hostId = getIntent().getLongExtra(INTENT_EXTRA_HOST_ID_NAME,
                                          HOST_ID_DEFAULT_VALUE);
        if (hostId == HOST_ID_DEFAULT_VALUE) {
            Utils.makeInternalErrorToast(this);
            NavigationUtils.startHomeActivity(this);
        }

        System.out.println("Got host id " + hostId);

    }
}