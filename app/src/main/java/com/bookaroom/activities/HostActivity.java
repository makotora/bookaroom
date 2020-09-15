package com.bookaroom.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.bookaroom.R;
import com.bookaroom.adapters.AvailabilityRangesAdapter;
import com.bookaroom.adapters.SelectedImagesAdapter;
import com.bookaroom.enums.ListingType;
import com.bookaroom.adapters.data.AvailabilityRange;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.NavigationUtils;
import com.bookaroom.utils.dto.SelectedImageInfo;
import com.bookaroom.utils.listeners.ImageSelectionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HostActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_MAIN_IMAGE_PERMISSIONS_CODE = 1;
    private static final int REQUEST_MAIN_IMAGE_PICK_CODE = 2;
    private static final int REQUEST_ADDITIONAL_IMAGE_PERMISSIONS_CODE = 3;
    private static final int REQUEST_ADDITIONAL_IMAGE_PICK_CODE = 4;
    private static final int REQUEST_LOCATION_PERMISSIONS_CODE = 5;
    private static final int REQUEST_CURRENT_LOCATION_CODE = 6;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    private ListView availabilityRangesListView;
    private AvailabilityRangesAdapter availabilityRangesAdapter;
    private Spinner listingTypeSpinner;

    private ImageView mainPictureView;
    private String mainPicturePath;
    private boolean mainPictureSelected;

    private RecyclerView additionalImagesView;
    private List<SelectedImageInfo> additionalImages;
    private SelectedImagesAdapter additionalImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        NavigationUtils.initializeBottomNavigationBar(this);

        initializeListingMap();
        initializeAvailabilityRangesView();
        initializeListingTypeSpinner();

        initializeMainImageView();
        initializeAdditionalImagesView();

        overrideScrollOperations();
    }

    private void initializeListingMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getPermissionsOrCurrentLocation();
    }

    private void getPermissionsOrCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, Constants.READ_LOCATION_PERMISSIONS_ARRAY, REQUEST_LOCATION_PERMISSIONS_CODE);
            return;
        }

        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Task<Location> lastLocationTask = fusedLocationProviderClient.getLastLocation();
        lastLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    initializeListMapFragment();
                }
            }
        });
    }

    private void initializeListMapFragment() {
        SupportMapFragment listingMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.host_listing_map);
        listingMapFragment.getMapAsync(this);
    }

    private void initializeAvailabilityRangesView() {
        availabilityRangesListView = (ListView) findViewById(R.id.listViewAvailabiliyRanges);
        availabilityRangesAdapter = new AvailabilityRangesAdapter(this, R.layout.host_availability_dates, getInitialAvailabilityRanges());
        availabilityRangesListView.setAdapter(availabilityRangesAdapter);

        findViewById(R.id.host_btnAddDates).setOnClickListener((view) -> addAvailabilityDatesRow());
    }

    private List<AvailabilityRange> getInitialAvailabilityRanges() {
        AvailabilityRange initialAvailabilityRange = new AvailabilityRange(new Date(), new Date());
        List<AvailabilityRange> availabilityRanges = new ArrayList<AvailabilityRange>(1);
        availabilityRanges.add(initialAvailabilityRange);

        return availabilityRanges;
    }

    private void addAvailabilityDatesRow() {
        availabilityRangesAdapter.add(new AvailabilityRange());
        availabilityRangesAdapter.notifyDataSetChanged();
        availabilityRangesListView.setSelection(availabilityRangesAdapter.getCount() - 1);
    }

    private void initializeListingTypeSpinner() {
        listingTypeSpinner = (Spinner) findViewById(R.id.spinnerListingType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListingType.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listingTypeSpinner.setAdapter(adapter);
    }

    private void initializeMainImageView() {
        mainPictureView = (ImageView) findViewById(R.id.host_main_image);
        mainPictureView.setImageResource(R.drawable.select_image);
        mainPictureView.setOnClickListener((view) -> ImageSelectionHelper.requestSelectionOrPermissions(this, getMainImageSelectionString(), REQUEST_MAIN_IMAGE_PERMISSIONS_CODE, REQUEST_MAIN_IMAGE_PICK_CODE));

        mainPictureSelected = false;
    }

    private String getMainImageSelectionString() {
        return getResources().getString(R.string.host_select_main_image);
    }

    private String getAdditionalImageSelectionString() {
        return getResources().getString(R.string.host_select_additional_image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_MAIN_IMAGE_PERMISSIONS_CODE:
                ImageSelectionHelper.requestSelection(this, getMainImageSelectionString(), REQUEST_MAIN_IMAGE_PICK_CODE);
                break;
            case REQUEST_MAIN_IMAGE_PICK_CODE:
                setMainPicture(data);
                break;
            case REQUEST_ADDITIONAL_IMAGE_PERMISSIONS_CODE:
                ImageSelectionHelper.requestSelection(this, getAdditionalImageSelectionString(), REQUEST_ADDITIONAL_IMAGE_PICK_CODE);
                break;
            case REQUEST_ADDITIONAL_IMAGE_PICK_CODE:
                addAdditionalPicture(data);
                break;
            case REQUEST_LOCATION_PERMISSIONS_CODE:
                getCurrentLocation();
                break;

        }
    }

    private void setMainPicture(Intent data) {
        SelectedImageInfo selectedImageInfo = ImageSelectionHelper.getSelectedImageInfo(this, data);
        mainPictureView.setImageBitmap(selectedImageInfo.getBitmap());
        mainPicturePath = selectedImageInfo.getPath();
        mainPictureSelected = true;
    }

    private void initializeAdditionalImagesView() {
        additionalImagesView = (RecyclerView) findViewById(R.id.host_additional_pictures);
        additionalImagesView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        additionalImages = new ArrayList<>();
        additionalImagesAdapter = new SelectedImagesAdapter(R.layout.host_additional_picture, additionalImages);
        additionalImagesView.setAdapter(additionalImagesAdapter);

        findViewById(R.id.host_btnAddPictures).setOnClickListener((view) ->
                ImageSelectionHelper.requestSelectionOrPermissions(this, getMainImageSelectionString(),
                        REQUEST_ADDITIONAL_IMAGE_PERMISSIONS_CODE, REQUEST_ADDITIONAL_IMAGE_PICK_CODE));
    }

    private void addAdditionalPicture(Intent data) {
        SelectedImageInfo selectedImageInfo = ImageSelectionHelper.getSelectedImageInfo(this, data);
        additionalImages.add(selectedImageInfo);
        additionalImagesAdapter.notifyDataSetChanged();
    }


    private void overrideScrollOperations() {
        ScrollView parentScroll = (ScrollView) findViewById(R.id.host_scrollView);
        ListView availabilityRangesScroll = availabilityRangesListView;

        parentScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                availabilityRangesScroll.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        availabilityRangesScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        parentScroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    case MotionEvent.ACTION_UP:
                        parentScroll.requestDisallowInterceptTouchEvent(false);
                        return true;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions currentLocationMO = new MarkerOptions().position(currentLocationLatLng).title("Your location");

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocationLatLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, Constants.DEFAULT_MAP_ZOOM));
        googleMap.addMarker(currentLocationMO);
    }
}