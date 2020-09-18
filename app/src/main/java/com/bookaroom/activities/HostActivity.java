package com.bookaroom.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bookaroom.R;
import com.bookaroom.adapters.AvailabilityRangesAdapter;
import com.bookaroom.adapters.SelectedImagesAdapter;
import com.bookaroom.enums.ListingType;
import com.bookaroom.adapters.data.AvailabilityRange;
import com.bookaroom.exceptions.InvalidInputException;
import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.ListingDetails;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.ListingService;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.NavigationUtils;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.Utils;
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
import com.google.android.libraries.places.api.Places;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = "HostActivity";

    private static final int REQUEST_MAIN_IMAGE_PERMISSIONS_CODE = 1;
    private static final int REQUEST_MAIN_IMAGE_PICK_CODE = 2;
    private static final int REQUEST_ADDITIONAL_IMAGE_PERMISSIONS_CODE = 3;
    private static final int REQUEST_ADDITIONAL_IMAGE_PICK_CODE = 4;
    private static final int REQUEST_LOCATION_PERMISSIONS_CODE = 5;

    // Request Data
    private EditText addressEdtText;
    private LatLng addressLatLng;
    private ArrayList<AvailabilityRange> availabilityRanges;
    private EditText maxGuestsEdtText;
    private EditText minPriceEdtText;
    private EditText extraCostEdtText;
    private Spinner listingTypeSpinner;

    private SelectedImageInfo mainPictureInfo;
    private boolean mainPictureSelected;

    private List<SelectedImageInfo> additionalImages;
    private EditText rulesEdtText;
    private EditText descriptionEdtText;
    private EditText bedsEdtText;
    private EditText bathroomsEdtText;
    private EditText bedroomsEdtText;
    private CheckBox hasLivingRoomCheckBox;
    private EditText areaEdtText;
    //-------------

    ListingService listingService;

    private Button addressSearchBtn;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private GoogleMap listingLocationMap;

    private ListView availabilityRangesListView;
    private AvailabilityRangesAdapter availabilityRangesAdapter;

    private ImageView mainPictureView;

    private RecyclerView additionalImagesView;
    private SelectedImagesAdapter additionalImagesAdapter;

    private Button submitButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        NavigationUtils.initializeBottomNavigationBar(this);

        initializeServices();

        initializeSimpleFields();
        initializeGooglePlaces();
        initializeAddressSearch();
        initializeListingMap();
        initializeAvailabilityRangesView();
        initializeListingTypeSpinner();

        initializeMainImageView();
        initializeAdditionalImagesView();

        overrideScrollOperations();

        boolean hostHasListing = false;
        if (hostHasListing) {
            setExistingData();
            initializeDeleteButton();
        }

        initializeSubmitButton(hostHasListing);

        if (Constants.INITIALIZE_FORMS_WITH_TEST_DATA) {
            setDummyData();
        }
    }

    private void initializeServices() {
        listingService = ApiUtils.getListingService();
    }

    private void initializeSimpleFields() {
        maxGuestsEdtText = (EditText) findViewById(R.id.host_edt_max_guests);
        minPriceEdtText = (EditText) findViewById(R.id.host_edt_min_price);
        extraCostEdtText = (EditText) findViewById(R.id.host_edt_extra_cost);
        rulesEdtText = (EditText) findViewById(R.id.host_edt_rules);
        descriptionEdtText = (EditText) findViewById(R.id.host_edt_description);
        bedsEdtText = (EditText) findViewById(R.id.host_edt_num_beds);
        bathroomsEdtText = (EditText) findViewById(R.id.host_edt_num_bathrooms);
        bedroomsEdtText = (EditText) findViewById(R.id.host_edt_num_bedrooms);
        hasLivingRoomCheckBox = (CheckBox) findViewById(R.id.host_has_living_room);
        areaEdtText = (EditText) findViewById(R.id.host_edt_listing_area);
    }

    private void initializeGooglePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        }
    }

    private void initializeAddressSearch() {
        addressEdtText = findViewById(R.id.host_edtAddress);

        addressSearchBtn = findViewById(R.id.host_btn_search_address);

        addressSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng location = getAddressTextLocation();
                if (location != null) {
                    setMapLocation(listingLocationMap, "", location);
                }
            }
        });
    }

    public LatLng getAddressTextLocation() {
        addressLatLng = getLocationFromAddress(HostActivity.this, addressEdtText.getText().toString());
        return addressLatLng;
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context, Locale.getDefault());
        List<Address> possibleAddresses;
        LatLng latLng = null;

        try {
            possibleAddresses = coder.getFromLocationName(strAddress, 1);
            if (possibleAddresses == null) {
                return null;
            }
            Address location = possibleAddresses.get(0);

            latLng = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return latLng;
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
        availabilityRanges = getInitialAvailabilityRanges();

        availabilityRangesListView = (ListView) findViewById(R.id.listViewAvailabiliyRanges);
        availabilityRangesAdapter = new AvailabilityRangesAdapter(this, R.layout.host_availability_dates, availabilityRanges);
        availabilityRangesListView.setAdapter(availabilityRangesAdapter);

        findViewById(R.id.host_btnAddDates).setOnClickListener((view) -> addAvailabilityDatesRow());
    }

    private ArrayList<AvailabilityRange> getInitialAvailabilityRanges() {
        AvailabilityRange initialAvailabilityRange = new AvailabilityRange(new Date(), new Date());
        ArrayList<AvailabilityRange> availabilityRanges = new ArrayList<AvailabilityRange>(1);
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
        mainPictureInfo = selectedImageInfo;
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
        listingLocationMap = googleMap;

        LatLng currentLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        setMapLocation(googleMap, "Your location", currentLocationLatLng);
    }

    private void setMapLocation(GoogleMap googleMap, String markerTitle, LatLng currentLocationLatLng) {
        googleMap.clear();
        MarkerOptions currentLocationMO = new MarkerOptions().position(currentLocationLatLng).title(markerTitle);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocationLatLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, Constants.DEFAULT_MAP_ZOOM));
        googleMap.addMarker(currentLocationMO);
    }

    // TODO
    private void setExistingData() {}

    private void initializeDeleteButton() {
        LinearLayout deleteButtonLayout = findViewById(R.id.host_delete_button_layout);
        deleteButton = new Button(this);

    }

    private void initializeSubmitButton(boolean hasListing) {
        int buttonTextResource;
        View.OnClickListener submitFormOnClickListener;

        if (!hasListing) {
            buttonTextResource = R.string.host_create_listing;
            submitFormOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCreateClick();
                }
            };
        }
        else {
            buttonTextResource = R.string.host_update_listing;
            submitFormOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUpdateClick();
                }
            };
        }

        submitButton = (Button) findViewById(R.id.host_submit_button);
        submitButton.setText(buttonTextResource);
        submitButton.setOnClickListener(submitFormOnClickListener);
    }

    private void onCreateClick() {
        ListingDetails listingDetails;

        try {
            listingDetails = getListingDetailsIfValid();
        } catch (InvalidInputException e) {
            displayInvalidInputMessage(e.getErrorStringResource());
            return;
        }

        createListing(listingDetails);
    }

    private void createListing(ListingDetails listingDetails) {
        Call call =
                listingService.create(RequestUtils.getRequestBodyForString(listingDetails.getAddress()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getLongitude()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getLatitude()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getMaxGuests()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getMinPrice()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getCostPerExtraGuest()),
                                      RequestUtils.getRequestBodyForString(listingDetails.getType().name()),
                                      RequestUtils.getRequestBodyForString(listingDetails.getRules()),
                                      RequestUtils.getRequestBodyForString(listingDetails.getDescription()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getNumberOfBeds()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getNumberOfBathrooms()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getNumberOfBedrooms()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.getArea()),
                                      RequestUtils.getRequestBodyForObject(listingDetails.isHasLivingRoom()),
                                      RequestUtils.getRequestBodyForString(RequestUtils.toRequestString(listingDetails.getAvailabilityRanges())),
                                      RequestUtils.getMultipartBodyPartForFile
                                              (listingDetails.getMainPicture(),
                                               ListingService.LISTING_MAIN_PICTURE_PARAM_NAME
                                               ),
                                      RequestUtils.getMultipartsForFiles(listingDetails.getAdditionalPictures(), ListingService.LISTING_ADDITIONAL_PICTURES_PARAM_NAME)
                                      );

        call.enqueue(new Callback<ActionResponse>() {
            @Override
            public void onResponse(Call<ActionResponse> call, Response<ActionResponse> response) {
                if (response.isSuccessful()) {
                    ActionResponse actionResponse = response.body();

                    if (actionResponse != null) {
                        Toast.makeText(HostActivity.this, actionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (!actionResponse.isSuccess()) {
                        return;
                    }
                }
                else {
                    Utils.makeInternalErrorToast(HostActivity.this);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(HostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO
    private void onUpdateClick() {
    }

    private ListingDetails getListingDetailsIfValid() throws InvalidInputException {
        String address = Utils.getEditTextString(addressEdtText);
        if (Utils.isNullOrEmpty(address)) {
            throw new InvalidInputException(R.string.host_empty_address);
        }

        Integer maxGuests = Utils.parseInteger(maxGuestsEdtText);
        if (maxGuests == null) {
            throw new InvalidInputException(R.string.host_invalid_max_guests);
        }

        Double minPrice = Utils.parseDouble(minPriceEdtText);
        if (minPrice == null) {
            throw new InvalidInputException(R.string.host_invalid_min_price);
        }

        Double extraCost = Utils.parseDouble(extraCostEdtText);
        if (extraCost == null) {
            throw new InvalidInputException(R.string.host_invalid_extra_cost);
        }

        ListingType listingType;
        try {
            listingType = ListingType.valueOf(listingTypeSpinner.getSelectedItem().toString());
        }
        catch (Exception e) {
            throw new InvalidInputException(R.string.host_invalid_listing_type);
        }

        // Rules not checked on purpose. Maybe there are no rules :)
        String rules = Utils.getEditTextString(rulesEdtText);

        String description = Utils.getEditTextString(descriptionEdtText);
        if (Utils.isNullOrEmpty(description)) {
            throw new InvalidInputException(R.string.host_empty_description);
        }

        Integer numberOfBeds = Utils.parseInteger(bedsEdtText);
        if (numberOfBeds == null) {
            throw new InvalidInputException(R.string.host_invalid_number_of_beds);
        }

        Integer numberOfBathrooms = Utils.parseInteger(bathroomsEdtText);
        if (numberOfBathrooms == null) {
            throw new InvalidInputException(R.string.host_invalid_number_of_bathrooms);
        }

        Integer numberOfBedrooms = Utils.parseInteger(bedroomsEdtText);
        if (numberOfBedrooms == null) {
            throw new InvalidInputException(R.string.host_invalid_number_of_bedrooms);
        }

        Integer listingArea = Utils.parseInteger(areaEdtText);
        if (listingArea == null) {
            throw new InvalidInputException(R.string.host_invalid_listing_area);
        }

        boolean hasLivingRoom = hasLivingRoomCheckBox.isChecked();

        if (availabilityRanges.isEmpty()) {
            throw new InvalidInputException(R.string.host_missing_availability_dates);
        }

        for (AvailabilityRange availabilityRange : availabilityRanges) {
            if (availabilityRange.getFrom() == null || availabilityRange.getTo() == null) {
                throw new InvalidInputException(R.string.host_missing_availability_dates);
            }
        }

        if (!mainPictureSelected) {
            throw new InvalidInputException(R.string.host_missing_main_picture);
        }
        File mainImageFile = new File(mainPictureInfo.getPath());

        ArrayList<File> additionalImageFiles = new ArrayList<>(additionalImages.size());
        for (int i = 0; i < additionalImages.size(); i++) {
            SelectedImageInfo selectedImageInfo = additionalImages.get(i);
            additionalImageFiles.add(new File(selectedImageInfo.getPath()));
        }

        LatLng addressLatLng = getAddressTextLocation();

        return new ListingDetails(
                address,
                addressLatLng == null ? null : addressLatLng.longitude,
                addressLatLng == null ? null : addressLatLng.latitude,
                maxGuests,
                minPrice,
                extraCost,
                listingType,
                rules,
                description,
                numberOfBeds,
                numberOfBathrooms,
                numberOfBathrooms,
                listingArea,
                hasLivingRoom,
                availabilityRanges,
                mainImageFile,
                additionalImageFiles
                );
    }

    private void displayInvalidInputMessage(int strResource) {
        Utils.displayInvalidInputMessage(this, strResource);
    }

    private void setDummyData() {
        addressEdtText.setText("Greece");
        maxGuestsEdtText.setText("5");
        minPriceEdtText.setText("100");
        extraCostEdtText.setText("10");
        descriptionEdtText.setText("Dummy Description");
        bedsEdtText.setText("3");
        bathroomsEdtText.setText("2");
        bedroomsEdtText.setText("2");
        areaEdtText.setText("75");
    }


}