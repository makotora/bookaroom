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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookaroom.R;
import com.bookaroom.adapters.ListingShortViewsAdapter;
import com.bookaroom.exceptions.InvalidInputException;
import com.bookaroom.models.ListingSearchRequest;
import com.bookaroom.models.ListingShortViewResponse;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.ListingService;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.DateUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.listeners.DatePickOnClickListener;
import com.bookaroom.utils.navigation.NavigationUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {
    // Search Form
    private LinearLayout searchLayout;
    private EditText edtState;
    private EditText edtCity;
    private EditText edtCountry;
    private EditText edtCheckIn;
    private EditText edtCheckOut;
    private EditText edtNumberOfGuests;
    private Button searchButton;

    private RecyclerView listingShortViewsRecyclerView;

    private ListingShortViewsAdapter listingShortViewsAdapter;
    private List<ListingShortViewResponse> allListingShortViews;

    boolean searchHidden;

    private ListingService listingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationUtils.initializeBottomNavigationBar(this);

        listingService = ApiUtils.getListingService(this);

        initializeSearchForm();
        initializeResultsView();

        if (Constants.INITIALIZE_FORMS_WITH_TEST_DATA) {
            setDummyData();
        }
    }

    private void initializeSearchForm() {
        searchHidden = false;

        searchLayout = findViewById(R.id.search_layout);

        edtState = findViewById(R.id.search_edt_state);
        edtCity = findViewById(R.id.search_edt_city);
        edtCountry = findViewById(R.id.search_edt_country);
        edtCheckIn = findViewById(R.id.search_edt_check_in);
        edtCheckOut = findViewById(R.id.search_edt_check_out);
        edtNumberOfGuests = findViewById(R.id.search_edt_number_of_guests);

        setupDatePickers(edtCheckIn,
                         edtCheckOut);

        searchButton = findViewById(R.id.search_button);

        setSearchButtonOnClickListener();
    }

    private void setupDatePickers(
            EditText edtCheckIn,
            EditText edtCheckOut) {
        edtCheckIn.setOnClickListener(new DatePickOnClickListener(HomeActivity.this,
                                                                  new DatePickerDialog.OnDateSetListener() {
                                                                      @Override
                                                                      public void onDateSet(
                                                                              DatePicker datePicker,
                                                                              int year,
                                                                              int month,
                                                                              int day) {
                                                                          edtCheckIn.setText(DateUtils.getDateString(day,
                                                                                                                     month + 1,
                                                                                                                     year));
                                                                      }
                                                                  }));

        edtCheckOut.setOnClickListener(new DatePickOnClickListener(HomeActivity.this,
                                                                   new DatePickerDialog.OnDateSetListener() {
                                                                       @Override
                                                                       public void onDateSet(
                                                                               DatePicker datePicker,
                                                                               int year,
                                                                               int month,
                                                                               int day) {
                                                                           edtCheckOut.setText(DateUtils.getDateString(day,
                                                                                                                       month + 1,
                                                                                                                       year));
                                                                       }
                                                                   }));
    }

    private void setSearchButtonOnClickListener() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchClick();
            }
        });
    }

    private void onSearchClick() {
        if (!searchHidden) {
            validateFormAndDoSearch();
        }
        else {
            showSearchForm();
        }
    }

    private void validateFormAndDoSearch() {
        ListingSearchRequest searchRequest;

        try {
            searchRequest = getListingSearchRequestIfValid();
        } catch (InvalidInputException e) {
            displayInvalidInputMessage(e.getErrorStringResource());
            return;
        }

        clearListingResults();
        searchListings(searchRequest);
        hideSearchForm();
    }

    public void hideSearchForm() {
        int translationY = 0 - searchLayout.getHeight() + searchButton.getHeight();

        searchLayout.setVisibility(View.VISIBLE);
        searchLayout.setAlpha(0.0f);

        searchLayout.animate()
                .translationY(translationY)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        searchLayout.setVisibility(View.VISIBLE);
                    }
                });

        listingShortViewsRecyclerView.animate()
                .translationY(translationY)
                .alpha(1.0f)
                .setListener(null);

        searchHidden = true;
    }

    private ListingSearchRequest getListingSearchRequestIfValid() throws InvalidInputException {
        String state = Utils.getEditTextString(edtState);
        if (Utils.isNullOrEmpty(state)) {
            throw new InvalidInputException(R.string.search_error_state);
        }

        String city = Utils.getEditTextString(edtCity);
        if (Utils.isNullOrEmpty(city)) {
            throw new InvalidInputException(R.string.search_error_city);
        }

        String country = Utils.getEditTextString(edtCountry);
        if (Utils.isNullOrEmpty(country)) {
            throw new InvalidInputException(R.string.search_error_city);
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

        return new ListingSearchRequest(state,
                                        city,
                                        country,
                                        checkInDate,
                                        checkOutDate,
                                        numberOfGuests);
    }

    private void displayInvalidInputMessage(int strResource) {
        Utils.displayInvalidInputMessage(this,
                                         strResource);
    }

    private void clearListingResults() {
        listingShortViewsAdapter.clearListingShortViews();
    }

    private void searchListings(ListingSearchRequest searchRequest) {
        Call searchCall = listingService.search(searchRequest.getState(),
                                                searchRequest.getCity(),
                                                searchRequest.getCountry(),
                                                searchRequest.getCheckIn(),
                                                searchRequest.getCheckOut(),
                                                searchRequest.getNumberOfGuests());
        searchCall.enqueue(new Callback<List<ListingShortViewResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ListingShortViewResponse>> call,
                    Response<List<ListingShortViewResponse>> response) {
                handleListingSearchResults(response.body());
            }

            @Override
            public void onFailure(
                    Call<List<ListingShortViewResponse>> call,
                    Throwable t) {
                Utils.makeLoadErrorToast(HomeActivity.this);
            }
        });
    }

    private void handleListingSearchResults(List<ListingShortViewResponse> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            Toast.makeText(this, R.string.search_no_results, Toast.LENGTH_SHORT).show();
            return;
        }

        handleListingShortViewsResults(searchResults);
    }

    private void showSearchForm() {
        int translationY = 0;

        searchLayout.animate()
                .translationY(translationY)
                .alpha(1.0f)
                .setListener(null);

        listingShortViewsRecyclerView.animate()
                .translationY(translationY)
                .alpha(1.0f)
                .setListener(null);

        searchHidden = false;
    }

    private void initializeResultsView() {
        allListingShortViews = new ArrayList<>();

        // The Adapter has keeps his own list of 'visible' listing short views
        listingShortViewsAdapter = new ListingShortViewsAdapter(this,
                                                                R.layout.listing_short_view,
                                                                new ArrayList<>());

        listingShortViewsRecyclerView = findViewById(R.id.listing_short_views_layout);
        listingShortViewsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                                                                               LinearLayoutManager.VERTICAL,
                                                                               false));
        listingShortViewsRecyclerView.setAdapter(listingShortViewsAdapter);

        displayUserRecommendedListings();
    }

    private void displayUserRecommendedListings() {
        Call call = listingService.getUserRecommendedListings();
        call.enqueue(new Callback<List<ListingShortViewResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ListingShortViewResponse>> call,
                    Response<List<ListingShortViewResponse>> response) {
                List<ListingShortViewResponse> listingShortViewsResult = response.body();
                handleListingShortViewsResults(listingShortViewsResult);
            }

            @Override
            public void onFailure(
                    Call<List<ListingShortViewResponse>> call,
                    Throwable t) {
                Utils.makeLoadErrorToast(HomeActivity.this);
            }
        });
    }

    private void handleListingShortViewsResults(List<ListingShortViewResponse> listingShortViewsResult) {
        allListingShortViews = listingShortViewsResult == null ? new ArrayList<>() :
                listingShortViewsResult;
        // TODO add only 10 views and handle the rest during scroll

        listingShortViewsAdapter.replaceListingShortViewsWith(allListingShortViews);
    }

    private void setDummyData() {
        edtState.setText("Attica");
        edtCity.setText("Glyfada");
        edtCountry.setText("Greece");
        edtCheckIn.setText("26/06/1996");
        edtCheckOut.setText("06/08/1996");
        edtNumberOfGuests.setText("15");
    }
}