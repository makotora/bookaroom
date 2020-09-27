package com.bookaroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bookaroom.R;
import com.bookaroom.adapters.ReviewsAdapter;
import com.bookaroom.models.ListingReviewResponse;
import com.bookaroom.models.UserProfileResponse;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.PicassoTrustAll;
import com.bookaroom.remote.services.ListingReviewsService;
import com.bookaroom.remote.services.UserService;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostProfileActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_HOST_ID_NAME = "EXTRA_HOST_ID";
    private static final Long HOST_ID_DEFAULT_VALUE = -1L;

    private long hostId;

    private UserService userService;
    private ListingReviewsService reviewsService;

    private ImageView hostImageView;
    private TextView tvName;
    private TextView tvSurname;
    private TextView tvEmail;
    private TextView tvPhone;

    private RecyclerView reviewsListView;
    private ReviewsAdapter reviewsAdapter;
    private List<ListingReviewResponse> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_profile);

        initializeIntentData();

        NavigationUtils.initializeBottomNavigationBar(this);

        userService = ApiUtils.getUserService(this);
        reviewsService = ApiUtils.getListingReviewsService(this);

        initializeViewFields();
        initializeReviewsLayout();

        getAndSetProfileData();
        getAndSetReviews();

        overrideScrollOperations();
    }

    private void initializeIntentData() {
        hostId = getIntent().getLongExtra(INTENT_EXTRA_HOST_ID_NAME,
                                          HOST_ID_DEFAULT_VALUE);
        if (hostId == HOST_ID_DEFAULT_VALUE) {
            Utils.makeInternalErrorToast(this);
            NavigationUtils.startHomeActivity(this);
        }
    }

    private void initializeViewFields() {
        hostImageView = findViewById(R.id.host_profile_image);
        tvName = findViewById(R.id.host_profile_name);
        tvSurname = findViewById(R.id.host_profile_surname);
        tvEmail = findViewById(R.id.host_profile_email);
        tvPhone = findViewById(R.id.host_profile_phone);
    }

    private void initializeReviewsLayout() {
        reviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(this, R.layout.review, reviews);

        reviewsListView = findViewById(R.id.host_profile_reviews);
        reviewsListView.setLayoutManager(new LinearLayoutManager(this,
                                                                LinearLayoutManager.VERTICAL,
                                                                false));
        reviewsListView.setAdapter(reviewsAdapter);
    }

    private void getAndSetProfileData() {
        Call call = userService.getUserProfile(hostId);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(
                    Call<UserProfileResponse> call,
                    Response<UserProfileResponse> response) {
                handleUserProfileResponse(response.body());
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                t.printStackTrace();
                Utils.makeInternalErrorToast(HostProfileActivity.this);
            }
        });
    }

    private void handleUserProfileResponse(UserProfileResponse userProfileResponse) {
        if (!Utils.isNullOrEmpty(userProfileResponse.getPicturePath())) {
            PicassoTrustAll.getInstance(this).load(RequestUtils.getUrlForServerFilePath(userProfileResponse.getPicturePath()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(hostImageView);
        }

        tvName.setText(userProfileResponse.getName());
        tvSurname.setText(userProfileResponse.getSurname());
        tvEmail.setText(userProfileResponse.getEmail());
        tvPhone.setText(userProfileResponse.getPhone());
    }

    private void getAndSetReviews() {
        Call call = reviewsService.getByHostUserId(hostId);
        call.enqueue(new Callback<List<ListingReviewResponse>>() {
            @Override
            public void onResponse(
                    Call<List<ListingReviewResponse>> call,
                    Response<List<ListingReviewResponse>> response) {
                handleReviewsResponse(response.body());
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                t.printStackTrace();
                Utils.makeInternalErrorToast(HostProfileActivity.this);
            }
        });
    }

    private void handleReviewsResponse(List<ListingReviewResponse> reviewsResponse) {
        reviewsAdapter.replaceAllWith(reviewsResponse);
    }

    private void overrideScrollOperations() {
        ScrollView parentScroll = findViewById(R.id.host_profile_scrollView);
        RecyclerView reviewsScroll = reviewsListView;

        parentScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(
                    View v,
                    MotionEvent event) {
                reviewsScroll.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        reviewsScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(
                    View v,
                    MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }
}