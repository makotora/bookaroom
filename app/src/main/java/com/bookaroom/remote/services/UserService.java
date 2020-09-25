package com.bookaroom.remote.services;

import com.bookaroom.models.ActionResponse;
import com.bookaroom.models.BooleanResponse;
import com.bookaroom.models.LoginRequest;
import com.bookaroom.models.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {
    String ENDPOINT_PATH = "/users";

    String REGISTER_PART_USERNAME = "username";
    String REGISTER_PART_PASSWORD = "password";
    String REGISTER_PART_NAME = "name";
    String REGISTER_PART_SURNAME = "surname";
    String REGISTER_PART_EMAIL = "email";
    String REGISTER_PART_PHONE = "phone";
    String REGISTER_PART_ROLES = "userRole";
    String REGISTER_PART_USER_IMAGE = "userImage";

    @POST("login") // Endpoint path not applicable
    Call<Void> login(@Body LoginRequest loginRequest);

    @Multipart
    @POST(ENDPOINT_PATH + "/register")
    Call<ActionResponse> register(@Part(REGISTER_PART_USERNAME) RequestBody username,
                                  @Part(REGISTER_PART_PASSWORD) RequestBody password,
                                  @Part(REGISTER_PART_NAME) RequestBody name,
                                  @Part(REGISTER_PART_SURNAME) RequestBody surname,
                                  @Part(REGISTER_PART_EMAIL) RequestBody email,
                                  @Part(REGISTER_PART_PHONE) RequestBody phone,
                                  @Part(REGISTER_PART_ROLES) RequestBody userRole,
                                  @Part MultipartBody.Part userImage);

    @Multipart
    @POST(ENDPOINT_PATH + "/update")
    Call<ActionResponse> update(@Part(REGISTER_PART_USERNAME) RequestBody username,
                                  @Part(REGISTER_PART_PASSWORD) RequestBody password,
                                  @Part(REGISTER_PART_NAME) RequestBody name,
                                  @Part(REGISTER_PART_SURNAME) RequestBody surname,
                                  @Part(REGISTER_PART_EMAIL) RequestBody email,
                                  @Part(REGISTER_PART_PHONE) RequestBody phone,
                                  @Part(REGISTER_PART_ROLES) RequestBody userRole,
                                  @Part MultipartBody.Part userImage);

    @GET(ENDPOINT_PATH + "/userIsHost")
    Call<BooleanResponse> userIsHost();

    @GET(ENDPOINT_PATH + "/userHasListing")
    Call<BooleanResponse> userHasListing();

    @GET(ENDPOINT_PATH + "/getCurrentUser")
    Call<UserResponse> getCurrentUser();
}
