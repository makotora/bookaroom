package com.bookaroom.remote.services;

import com.bookaroom.models.ActionResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ListingService {
    public static final String LISTING_MAIN_PICTURE_PARAM_NAME = "mainListingPictureFile";
    public static final String LISTING_ADDITIONAL_PICTURES_PARAM_NAME =
            "listingPictureFiles";

    @Multipart
    @POST("/listings/create")
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
}
