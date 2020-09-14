package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.bookaroom.R;
import com.bookaroom.adapters.AvailabilityRangesAdapter;
import com.bookaroom.adapters.SelectedImagesAdapter;
import com.bookaroom.enums.ListingType;
import com.bookaroom.adapters.data.AvailabilityRange;
import com.bookaroom.utils.NavigationUtils;
import com.bookaroom.utils.dto.SelectedImageInfo;
import com.bookaroom.utils.listeners.ImageSelectionHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HostActivity extends AppCompatActivity {
    private static final int REQUEST_MAIN_IMAGE_PERMISSIONS_CODE = 1;
    private static final int REQUEST_MAIN_IMAGE_PICK_CODE = 2;
    private static final int REQUEST_ADDITIONAL_IMAGE_PERMISSIONS_CODE = 3;
    private static final int REQUEST_ADDITIONAL_IMAGE_PICK_CODE = 4;

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

        initializeAvailabilityRangesView();
        initializeListingTypeSpinner();

        initializeMainImageView();
        initializeAdditionalImagesView();
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
}