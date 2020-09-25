package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookaroom.R;
import com.bookaroom.models.ListingFullViewResponse;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.ListingService;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_LISTING_ID_NAME = "EXTRA_LISTING_ID";
    public static final String INTENT_EXTRA_CHECK_IN_NAME = "EXTRA_CHECK_IN";
    public static final String INTENT_EXTRA_CHECK_OUT_NAME = "EXTRA_CHECKOUT";
    public static final String INTENT_EXTRA_NUM_GUESTS_NAME = "EXTRA_NUM_GUESTS";

    public static final long LISTING_ID_DEFAULT_VALUE = -1;

    private ListingService listingService;

    // Intent data
    private Long listingId;
    private Date searchCheckIn;
    private Date searchCheckOut;
    private Integer searchNumberOfGuests;

    // Display data
    private ViewPager picturesViewPager;
    private TextView tvNumberOfBeds;
    private TextView tvNumberOfBathrooms;
    private TextView tvListingType;
    private TextView tvNumberOfBedrooms;
    private TextView tvArea;
    private TextView tvDescription;
    private TextView tvRules;
    private TextView tvAddress;

    private GoogleMap listingMap;
    private LatLng listingLatLong;

    private CheckBox chkHasLivingRoom;

    private ImageView hostImageView;
    private TextView tvViewHostProfileLink;

    public ListingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        listingService = ApiUtils.getListingService(this);

        initializeIntentData();

        NavigationUtils.initializeBottomNavigationBar(this);

        initializeViewFields();
        initializeHostViewLink();
        initializeListingData();
    }

    private void initializeIntentData() {
        Intent intent = getIntent();
        listingId = intent.getLongExtra(INTENT_EXTRA_LISTING_ID_NAME,
                                        LISTING_ID_DEFAULT_VALUE);
        if (listingId == LISTING_ID_DEFAULT_VALUE) {
            Utils.makeInternalErrorToast(this);
            NavigationUtils.startHomeActivity(this);
        }

        long searchCheckInInt = intent.getLongExtra(INTENT_EXTRA_CHECK_IN_NAME,
                                                    0);
        long searchCheckOutInt = intent.getLongExtra(INTENT_EXTRA_CHECK_OUT_NAME,
                                                     0);

        searchCheckIn = searchCheckInInt == 0 ? null : new Date(searchCheckInInt);
        searchCheckOut = searchCheckOutInt == 0 ? null : new Date(searchCheckOutInt);
        searchNumberOfGuests = intent.getIntExtra(INTENT_EXTRA_NUM_GUESTS_NAME,
                                                  1);
    }

    private void initializeViewFields() {
        picturesViewPager = findViewById(R.id.listing_pictures_vp);
        tvNumberOfBeds = findViewById(R.id.listing_number_of_beds);
        tvNumberOfBathrooms = findViewById(R.id.listing_number_of_bathrooms);
        tvListingType = findViewById(R.id.listing_type);
        tvNumberOfBedrooms = findViewById(R.id.listing_number_of_bedrooms);
        tvArea = findViewById(R.id.listing_area);
        tvDescription = findViewById(R.id.listing_description);
        tvRules = findViewById(R.id.listing_rules);
        tvAddress = findViewById(R.id.listing_address);

        chkHasLivingRoom = findViewById(R.id.listing_has_living_room);

        hostImageView = findViewById(R.id.listing_host_image);
        tvViewHostProfileLink = findViewById(R.id.listing_host_view_link);
    }

    private void initializeHostViewLink() {
        tvViewHostProfileLink.setMovementMethod(LinkMovementMethod.getInstance());
        tvViewHostProfileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHostProfile();
            }
        });
    }

    private void viewHostProfile() {
        // TODO
    }

    private void initializeListingData() {
        Call viewListingCall = listingService.view(listingId,
                                                   searchCheckIn,
                                                   searchCheckOut,
                                                   searchNumberOfGuests);
        viewListingCall.enqueue(new Callback<ListingFullViewResponse>() {
            @Override
            public void onResponse(
                    Call<ListingFullViewResponse> call,
                    Response<ListingFullViewResponse> response) {
                handleViewListingResponse(response.body());
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                Utils.makeLoadErrorToast(ListingActivity.this);
            }
        });
    }

    private void handleViewListingResponse(ListingFullViewResponse body) {
        // TODO
    }
}