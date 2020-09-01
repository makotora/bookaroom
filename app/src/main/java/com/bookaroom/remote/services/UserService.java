package com.bookaroom.remote.services;

import com.bookaroom.models.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("/login")
    Call<Void> login(@Body LoginRequest loginRequest);
}
