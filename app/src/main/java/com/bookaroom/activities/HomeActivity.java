package com.bookaroom.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.bookaroom.utils.LayoutUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.listeners.DatePickOnClickListener;
import com.bookaroom.utils.navigation.NavigationUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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

    private int initialSearchLayoutHeight;
    private static final int HIDDEN_SEARCH_LAYOUT_HEIGHT = 0;
    private boolean searchHidden;

    private ListingService listingService;

    private static final int INITIAL_VISIBLE_RESULTS = 10;
    private static final int ADDITIONAL_ELEMENTS_PER_LOAD = 5;
    private static final int LOAD_DELAY_MS = 2000;

    private ReentrantLock loadResultsLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationUtils.initializeBottomNavigationBar(this);

        listingService = ApiUtils.getListingService(this);

        loadResultsLock = new ReentrantLock();

        initializeSearchForm();
        initializeResultsView();
        initializeResultsScrollListener();

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
        } else {
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

        searchListings(searchRequest);
        hideSearchForm();
    }

    public void hideSearchForm() {
        initialSearchLayoutHeight = searchLayout.getHeight();
        LayoutUtils.slideView(searchLayout,
                              initialSearchLayoutHeight,
                              HIDDEN_SEARCH_LAYOUT_HEIGHT,
                              new AnimatorListenerAdapter() {
                                  @Override
                                  public void onAnimationEnd(Animator animation) {
                                      super.onAnimationEnd(animation);
                                      searchLayout.setVisibility(View.GONE);
                                  }
                              });
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
            Toast.makeText(this,
                           R.string.search_no_results,
                           Toast.LENGTH_SHORT).show();
        }

        handleListingShortViewsResults(searchResults);
    }

    private void showSearchForm() {
        searchLayout.setVisibility(View.VISIBLE);
        LayoutUtils.slideView(searchLayout,
                              HIDDEN_SEARCH_LAYOUT_HEIGHT,
                              initialSearchLayoutHeight,
                              null);

        searchHidden = false;
    }

    private void initializeResultsView() {
        allListingShortViews = new ArrayList<>();

        // The Adapter has keeps his own list of 'visible' listing short views
        listingShortViewsAdapter = new ListingShortViewsAdapter(this,
                                                                R.layout.listing_short_view,
                                                                R.layout.loading_layout,
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

        loadResultsLock.lock();

        try {
            List<ListingShortViewResponse> initialResults = getInitialResults();
            listingShortViewsAdapter.replaceListingShortViewsWith(initialResults);
        } finally {
            loadResultsLock.unlock();
        }
    }

    private List<ListingShortViewResponse> getInitialResults() {
        List<ListingShortViewResponse> initialResults = new ArrayList<>(INITIAL_VISIBLE_RESULTS);
        for (int i = 0; i < allListingShortViews.size() && i < INITIAL_VISIBLE_RESULTS; i++) {
            initialResults.add(allListingShortViews.get(i));
        }
        return initialResults;
    }

    private void setDummyData() {
        edtState.setText("Attica");
        edtCity.setText("Glyfada");
        edtCountry.setText("Greece");
        edtCheckIn.setText("24/09/2020");
        edtCheckOut.setText("30/09/2020");
        edtNumberOfGuests.setText("1");
    }

    private void initializeResultsScrollListener() {
        listingShortViewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(
                    @NonNull RecyclerView recyclerView,
                    int newState) {
                super.onScrollStateChanged(recyclerView,
                                           newState);
            }

            @Override
            public void onScrolled(
                    @NonNull RecyclerView recyclerView,
                    int dx,
                    int dy) {
                super.onScrolled(recyclerView,
                                 dx,
                                 dy);
                handleResultsScroll(recyclerView,
                                    dx,
                                    dy);
            }

        });
    }

    private void handleResultsScroll(
            RecyclerView recyclerView,
            int dx,
            int dy) {
        loadResultsLock.lock();

        try {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (!isLastElementCompletelyVisible(recyclerView)) {
                return;
            }

            // Load more elements
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    listingShortViewsAdapter.addItem(listingShortViewsAdapter.getLoadingItem());
                    List<ListingShortViewResponse> elementsToShow = getNextElementsToShow();
                    listingShortViewsAdapter.removeLastItem();

                    listingShortViewsAdapter.addAllListingShortViews(elementsToShow);

                }
            });
        }
        finally {
            loadResultsLock.unlock();
        }
    }

    private boolean isLastElementCompletelyVisible(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        return linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == listingShortViewsAdapter.getItemCount() - 1;
    }

    private List<ListingShortViewResponse> getNextElementsToShow() {
        final int currentVisibleCount = listingShortViewsAdapter.getItemCount();
        final int targetVisibleCount = currentVisibleCount + ADDITIONAL_ELEMENTS_PER_LOAD;
        final int allElementsCount = allListingShortViews.size();

        List<ListingShortViewResponse> elementsToShow = new ArrayList<>();
        for (int i = currentVisibleCount; i < allElementsCount && i < targetVisibleCount; i++) {
            elementsToShow.add(allListingShortViews.get(i));
        }

        return elementsToShow;
    }

}