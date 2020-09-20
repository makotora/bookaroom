package com.bookaroom.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bookaroom.R;
import com.bookaroom.exceptions.InvalidInputException;
import com.bookaroom.models.ListingSearchRequest;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.ListingService;
import com.bookaroom.utils.DateUtils;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.listeners.DatePickOnClickListener;
import com.bookaroom.utils.navigation.NavigationUtils;

import java.text.ParseException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {
    // Search Form
    LinearLayout searchLayout;
    EditText edtAddress;
    EditText edtCheckIn;
    EditText edtCheckOut;
    EditText edtNumberOfGuests;
    Button searchButton;
    boolean searchHidden;

    ListingService listingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationUtils.initializeBottomNavigationBar(this);

        listingService = ApiUtils.getListingService(this);

        initializeSearchForm();
        initializeResultsView();
    }

    private void initializeSearchForm() {
        searchHidden = false;
        searchLayout = findViewById(R.id.search_layout);
        edtAddress = findViewById(R.id.search_edt_address);
        edtCheckIn = findViewById(R.id.search_edt_check_in);
        edtCheckOut = findViewById(R.id.search_edt_check_out);
        edtNumberOfGuests = findViewById(R.id.search_edt_number_of_guests);

        setupDatePickers(edtCheckIn, edtCheckOut);

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchClick();
            }
        });
    }

    private void setupDatePickers(EditText edtCheckIn, EditText edtCheckOut) {
        edtCheckIn.setOnClickListener(new DatePickOnClickListener(HomeActivity.this,
                                                               new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                edtCheckIn.setText(DateUtils.getDateString(day, month + 1, year));
            }
        }));

        edtCheckOut.setOnClickListener(new DatePickOnClickListener(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                edtCheckOut.setText(DateUtils.getDateString(day, month + 1, year));
            }
        }));
    }

    private void onSearchClick() {
        ListingSearchRequest searchRequest;

        try {
            searchRequest = getListingSearchRequestIfValid();
        } catch (InvalidInputException e) {
            displayInvalidInputMessage(e.getErrorStringResource());
            return;
        }

        searchListings(searchRequest);
        hideSearchForm();
    }

    private ListingSearchRequest getListingSearchRequestIfValid() throws InvalidInputException {
        String address = Utils.getEditTextString(edtAddress);
        if (Utils.isNullOrEmpty(address)) {
            throw new InvalidInputException(R.string.search_error_address);
        }

        Date checkInDate;
        {
            String checkInDateStr = Utils.getEditTextString(edtCheckIn);
            if (Utils.isNullOrEmpty(checkInDateStr)) {
                throw new InvalidInputException(R.string.search_error_check_in);
            }

            try {
                checkInDate = DateUtils.parseDate(checkInDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new InvalidInputException(R.string.search_error_check_in);
            }
        }

        Date checkOutDate;
        {
            String checkOutDateStr = Utils.getEditTextString(edtCheckOut);
            if (Utils.isNullOrEmpty(checkOutDateStr)) {
                throw new InvalidInputException(R.string.search_error_check_out);
            }

            try {
                checkOutDate = DateUtils.parseDate(checkOutDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new InvalidInputException(R.string.search_error_check_out);
            }
        }

        if (!checkOutDate.after(checkInDate)) {
            throw new InvalidInputException(R.string.search_error_invalid_dates);
        }

        Integer numberOfGuests = Utils.parseInteger(Utils.getEditTextString(edtNumberOfGuests));
        if (numberOfGuests == null) {
            throw new InvalidInputException(R.string.search_error_number_of_guests);
        }

        return new ListingSearchRequest(address, checkInDate, checkOutDate, numberOfGuests);
    }

    private void displayInvalidInputMessage(int strResource) {
        Utils.displayInvalidInputMessage(this, strResource);
    }

    public void hideSearchForm() {
        searchLayout.setVisibility(View.VISIBLE);
        searchLayout.setAlpha(0.0f);

        searchLayout.animate()
                .translationY(0 - searchLayout.getHeight())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                                    view.setVisibility(View.GONE);
                    }
                });
        searchHidden = true;
    }

    private void searchListings(ListingSearchRequest searchRequest) {
        // TODO
        return;
    }

    private void initializeResultsView() {
        Call call = listingService.getUserRecommendedListings();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(
                    Call call,
                    Response response) {

            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {

            }
        });
    }
}