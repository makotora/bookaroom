package com.bookaroom.remote.services;

import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.ListingFullViewResponse;
import com.bookaroom.models.ListingResponse;
import com.bookaroom.models.ListingShortViewResponse;
import com.bookaroom.models.ReservationRequest;

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

public interface ListingService {
    public static final String LISTING_MAIN_PICTURE_PARAM_NAME = "mainListingPictureFile";
    public static final String LISTING_ADDITIONAL_PICTURES_PARAM_NAME =
            "listingPictureFiles";

    public static final String ENDPOINT_PATH = "/listings";

    @Multipart
    @POST(ENDPOINT_PATH + "/create")
    Call<ActionResponse> create(@Part("address") RequestBody address,
                                @Part("longitude") RequestBody longitude,
                                @Part("latitude") RequestBody latitude,
                                @Part("maxGuests") RequestBody maxGuests,
                                @Part("minPrice") RequestBody minPrice,
                                @Part("costPerExtraGuest") RequestBody costPerExtraGuest,
                                @Part("typeStr") RequestBody typeStr,
                                @Part("rules") RequestBody rules,
                                @Part("description") RequestBody description,
                                @Part("numberOfBeds") RequestBody numberOfBeds,
                                @Part("numberOfBathrooms") RequestBody numberOfBathrooms,
                                @Part("numberOfBedrooms") RequestBody numberOfBedrooms,
                                @Part("area") RequestBody area,
                                @Part("hasLivingRoom") RequestBody hasLivingRoom,
                                @Part("listingAvailabilities") RequestBody listingAvailabilities,
                                @Part MultipartBody.Part mainListingPictureFile,
                                @Part List<MultipartBody.Part> listingPictureFiles);

    @Multipart
    @POST(ENDPOINT_PATH + "/update")
    Call<ActionResponse> update(@Part("address") RequestBody address,
                                @Part("longitude") RequestBody longitude,
                                @Part("latitude") RequestBody latitude,
                                @Part("maxGuests") RequestBody maxGuests,
                                @Part("minPrice") RequestBody minPrice,
                                @Part("costPerExtraGuest") RequestBody costPerExtraGuest,
                                @Part("typeStr") RequestBody typeStr,
                                @Part("rules") RequestBody rules,
                                @Part("description") RequestBody description,
                                @Part("numberOfBeds") RequestBody numberOfBeds,
                                @Part("numberOfBathrooms") RequestBody numberOfBathrooms,
                                @Part("numberOfBedrooms") RequestBody numberOfBedrooms,
                                @Part("area") RequestBody area,
                                @Part("hasLivingRoom") RequestBody hasLivingRoom,
                                @Part("listingAvailabilities") RequestBody listingAvailabilities,
                                @Part MultipartBody.Part mainListingPictureFile,
                                @Part List<MultipartBody.Part> listingPictureFiles);

    @GET(ENDPOINT_PATH + "/getByCurrentUser")
    Call<ListingResponse> getByCurrentUser();

    @DELETE(ENDPOINT_PATH + "/deleteByCurrentUser")
    Call<ActionResponse> deleteByCurrentUser();

    @GET(ENDPOINT_PATH + "/getUserRecommendedListings")
    Call<List<ListingShortViewResponse>> getUserRecommendedListings();

    @GET(ENDPOINT_PATH + "/search")
    Call<List<ListingShortViewResponse>> search(@Query("state") String state,
                                                @Query("city") String city,
                                                @Query("country") String country,
                                                @Query("checkIn") Date checkIn,
                                                @Query("checkOut") Date checkOut,
                                                @Query("numberOfGuests") Integer numberOfGuests);

    @GET(ENDPOINT_PATH + "/view")
    Call<ListingFullViewResponse> view(@Query("listingId") Long listingId,
                                       @Query("checkIn") Date checkIn,
                                       @Query("checkOut") Date checkOut,
                                       @Query("numberOfGuests") Integer numberOfGuests);

    @POST(ENDPOINT_PATH + "/reserve")
    Call<ActionResponse> reserve(@Body ReservationRequest reservationRequest);
}
