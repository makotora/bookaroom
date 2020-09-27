package com.bookaroom.remote.services;

import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.ListingFullViewResponse;
import com.bookaroom.models.ListingResponse;
import com.bookaroom.models.ListingReviewRequest;
import com.bookaroom.models.ListingReviewResponse;
import com.bookaroom.models.ListingShortViewResponse;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ListingReviewsService {
    public static final String ENDPOINT_PATH = "/reviews";

    @POST(ENDPOINT_PATH + "/addReview")
    Call<ActionResponse> addReview(
            @Body
            ListingReviewRequest listingReviewRequest);

    @GET(ENDPOINT_PATH + "/getByHostUserId")
    Call<List<ListingReviewResponse>> getByHostUserId(
            @Query("hostUserId") Long hostUserId);
}
