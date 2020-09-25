package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bookaroom.R;
import com.bookaroom.adapters.ListingImagesPagerAdapter;
import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.ListingFullViewResponse;
import com.bookaroom.models.ListingReviewRequest;
import com.bookaroom.models.ReservationRequest;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.PicassoTrustAll;
import com.bookaroom.remote.services.ListingReviewsService;
import com.bookaroom.remote.services.ListingService;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.ResponseUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingActivity extends AppCompatActivity implements OnMapReadyCallback  {

    public static final String INTENT_EXTRA_LISTING_ID_NAME = "EXTRA_LISTING_ID";
    public static final String INTENT_EXTRA_CHECK_IN_NAME = "EXTRA_CHECK_IN";
    public static final String INTENT_EXTRA_CHECK_OUT_NAME = "EXTRA_CHECKOUT";
    public static final String INTENT_EXTRA_NUM_GUESTS_NAME = "EXTRA_NUM_GUESTS";

    public static final long LISTING_ID_DEFAULT_VALUE = -1;

    private ListingService listingService;
    private ListingReviewsService listingReviewsService;

    // Intent data
    private long listingId;
    private Date searchCheckIn;
    private Date searchCheckOut;
    private Integer searchNumberOfGuests;

    // Display data
    private ViewFlipper picturesViewFlipper;
    private static final int FLIP_IMAGE_INTERVAL_MS = 3000;

    private ViewPager picturesViewPager;
    private List<Bitmap> pictureBitmaps;
    private ListingImagesPagerAdapter picturesPagerAdapter;
    private List<Target> pictureLoadTargets = new ArrayList<>();

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

    private Button reservationButton;

    private ImageView hostImageView;
    private TextView tvViewHostProfileLink;

    // Review form
    private RatingBar reviewRatingBar;
    private EditText reviewEditText;
    private Button reviewSubmitButton;

    public ListingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        listingService = ApiUtils.getListingService(this);
        listingReviewsService = ApiUtils.getListingReviewsService(this);

        initializeIntentData();

        NavigationUtils.initializeBottomNavigationBar(this);

        initializeViewFields();
        initializePicturePagerAdapter();
        initializePicturesViewFlipper();
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

        int intentSearchNumberOfGuests = intent.getIntExtra(INTENT_EXTRA_NUM_GUESTS_NAME,
                                                            0);
        searchNumberOfGuests = intentSearchNumberOfGuests == 0 ? null : intentSearchNumberOfGuests;
    }

    private void initializeViewFields() {
        picturesViewPager = findViewById(R.id.listing_pictures_vp);
        picturesViewFlipper = findViewById(R.id.listing_pictures_vf);
        tvNumberOfBeds = findViewById(R.id.listing_number_of_beds);
        tvNumberOfBathrooms = findViewById(R.id.listing_number_of_bathrooms);
        tvListingType = findViewById(R.id.listing_type);
        tvNumberOfBedrooms = findViewById(R.id.listing_number_of_bedrooms);
        tvArea = findViewById(R.id.listing_area);
        tvDescription = findViewById(R.id.listing_description);
        tvRules = findViewById(R.id.listing_rules);
        tvAddress = findViewById(R.id.listing_address);

        chkHasLivingRoom = findViewById(R.id.listing_has_living_room);
        // Disable editting on the checkbox
        chkHasLivingRoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    chkHasLivingRoom.setChecked(false);
                }
            }
        });

        reservationButton = findViewById(R.id.listing_reservation_button);
        hostImageView = findViewById(R.id.listing_host_image);
        tvViewHostProfileLink = findViewById(R.id.listing_host_view_link);
    }

    private void initializePicturePagerAdapter() {
        pictureBitmaps = new ArrayList<>();
        picturesPagerAdapter = new ListingImagesPagerAdapter(this, pictureBitmaps);
        picturesViewPager.setAdapter(picturesPagerAdapter);
    }

    private void initializePicturesViewFlipper() {
        picturesViewFlipper.setFlipInterval(FLIP_IMAGE_INTERVAL_MS);
        picturesViewFlipper.startFlipping();
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

    private void handleViewListingResponse(ListingFullViewResponse lfvr) {
        initializeListingPicturesView(lfvr.getMainPicturePath(), lfvr.getAdditionalPicturePaths());
        tvNumberOfBeds.setText(Utils.getIntegerStringOrDefault(lfvr.getNumberOfBeds(), ""));
        tvNumberOfBathrooms.setText(Utils.getIntegerStringOrDefault(lfvr.getNumberOfBathrooms(), ""));
        tvListingType.setText(lfvr.getTypeStr());
        tvNumberOfBedrooms.setText(Utils.getIntegerStringOrDefault(lfvr.getNumberOfBedrooms(), ""));
        tvArea.setText(Utils.getIntegerStringOrDefault(lfvr.getArea(), ""));
        tvDescription.setText(lfvr.getDescription());
        tvRules.setText(lfvr.getRules());
        tvAddress.setText(lfvr.getAddress());

        initializeListingMap(lfvr.getAddress(), lfvr.getLatitude(), lfvr.getLongitude());

        chkHasLivingRoom.setChecked(lfvr.getIsAvailable() != null && lfvr.getIsAvailable().booleanValue());
        // Disable editting on the checkbox
        chkHasLivingRoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    chkHasLivingRoom.setChecked(false);
                }
            }
        });

        PicassoTrustAll.getInstance(this).load(RequestUtils.getUrlForServerFilePath(lfvr.getHostPicturePath()))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(hostImageView);

        initializeReservationButton(lfvr.getIsAvailable());
        initializeHostViewLink(lfvr.getHostId());
        initializeReviewForm(lfvr.getHasLastReservationPassed());
    }

    private void initializeListingPicturesView(
            String mainPicturePath,
            List<String> additionalPicturePaths) {
        List<String> allPicturePaths = new ArrayList<>();

        if (mainPicturePath != null) {
            allPicturePaths.add(mainPicturePath);
        }

        if (additionalPicturePaths != null) {
            allPicturePaths.addAll(additionalPicturePaths);
        }
        pictureLoadTargets.clear();

        for (String picturePath : allPicturePaths) {

            // One target per additional image load
            Target pictureLoadTarget = new Target() {
                @Override
                public void onBitmapLoaded(
                        Bitmap bitmap,
                        Picasso.LoadedFrom from) {
                    addPictureBitmapSynchronized(bitmap);
                }

                @Override
                public void onBitmapFailed(
                        Exception e,
                        Drawable errorDrawable) {
                    e.printStackTrace();
                    Utils.makeInternalErrorToast(ListingActivity.this);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            // Keep inside the global list to avoid GC
            pictureLoadTargets.add(pictureLoadTarget);

            PicassoTrustAll.getInstance(this).load(RequestUtils.getUrlForServerFilePath(picturePath))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(pictureLoadTarget);
        }
    }

    private synchronized void addPictureBitmapSynchronized(Bitmap bitmap) {
        pictureBitmaps.add(bitmap);
        picturesPagerAdapter.notifyDataSetChanged();

        ImageView newImageView = new ImageView(this);
        newImageView.setImageBitmap(bitmap);

        picturesViewFlipper.addView(newImageView);
    }

    public LatLng getLocationFromAddress(
            Context context,
            String strAddress) {

        Geocoder coder = new Geocoder(context,
                                      Locale.getDefault());
        List<Address> possibleAddresses;
        LatLng latLng = null;

        try {
            possibleAddresses = coder.getFromLocationName(strAddress,
                                                          1);
            if (possibleAddresses == null) {
                return null;
            }
            Address location = possibleAddresses.get(0);

            latLng = new LatLng(location.getLatitude(),
                                location.getLongitude());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return latLng;
    }

    private void initializeListingMap(String address, Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            listingLatLong = new LatLng(latitude, longitude);
        }
        else if (!Utils.isNullOrEmpty(address)) { // Try to get LatLng using the address
            listingLatLong = getLocationFromAddress(this, address);
        }

        SupportMapFragment listingMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.listing_map);
        listingMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        listingMap = googleMap;

        if (listingLatLong != null) {
            setMapLocation(googleMap,
                           "",
                           listingLatLong);
            return;
        }
    }

    private void setMapLocation(
            GoogleMap googleMap,
            String markerTitle,
            LatLng locationLatLong) {
        if (googleMap == null) {
            return;
        }

        googleMap.clear();
        MarkerOptions currentLocationMO = new MarkerOptions().position(locationLatLong).title(markerTitle);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(locationLatLong));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLong,
                                                                  Constants.DEFAULT_MAP_ZOOM));
        googleMap.addMarker(currentLocationMO);
    }

    private void initializeReservationButton(Boolean listingIsAvailable) {
        if (listingIsAvailable == null ||
                !listingIsAvailable.booleanValue() || searchCheckIn == null || searchCheckOut == null || searchNumberOfGuests == null) {
            disableReservationButton();
            return;
        }

        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveListing(listingIsAvailable);
            }
        });
    }

    private void reserveListing(Boolean listingIsAvailable) {
        if (listingIsAvailable == null ||
                !listingIsAvailable.booleanValue() || searchCheckIn == null || searchCheckOut == null || searchNumberOfGuests == null) {
            disableReservationButton(); // Shouldn't be here..
            return;
        }

        Call reservationCall = listingService.reserve(new ReservationRequest(listingId,
                                                                             searchCheckIn,
                                                                             searchCheckOut, searchNumberOfGuests));
        reservationCall.enqueue(new Callback<ActionResponse>() {
            @Override
            public void onResponse(
                    Call<ActionResponse> call,
                    Response<ActionResponse> response) {
                ResponseUtils.handleActionResponse(ListingActivity.this, response,
                                                   (ar) -> disableReservationButton(), (ar) -> {});
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                Utils.makeInternalErrorToast(ListingActivity.this);
            }
        });
    }

    private void disableReservationButton() {
        reservationButton.setEnabled(false);
    }

    private void initializeHostViewLink(Long hostId) {
        tvViewHostProfileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHostProfile(hostId);
            }
        });
    }

    private void viewHostProfile(Long hostId) {
        if (hostId == null) {
            Toast.makeText(this, R.string.listing_view_host_no_id, Toast.LENGTH_SHORT);
            return;
        }

        NavigationUtils.startHostProfileActivity(this, hostId);
    }

    private void initializeReviewForm(boolean hasLastReservationPassed) {
        reviewRatingBar = findViewById(R.id.listing_review_rating_bar);
        reviewEditText = findViewById(R.id.listing_review_edt_text);
        reviewSubmitButton = findViewById(R.id.listing_review_submit);

        if (!hasLastReservationPassed) {
            disableListingReview();
        }

        reviewSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        final float rating = reviewRatingBar.getRating();
        final String review = Utils.getEditTextString(reviewEditText);

        Call submitReviewCall =
                listingReviewsService.addReview(new ListingReviewRequest(listingId, rating,
                                                                         review));
        submitReviewCall.enqueue(new Callback<ActionResponse>() {
            @Override
            public void onResponse(
                    Call<ActionResponse> call,
                    Response<ActionResponse> response) {
                ResponseUtils.handleActionResponse(ListingActivity.this,
                                                   response,
                                                   (ar) ->
                                                           disableListingReview(),
                                                   (ar) -> {
                                                   });
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                Utils.makeInternalErrorToast(ListingActivity.this);
            }
        });
    }

    private void disableListingReview() {
        reviewRatingBar.setEnabled(false);
        reviewEditText.setEnabled(false);
        reviewSubmitButton.setEnabled(false);
    }
}