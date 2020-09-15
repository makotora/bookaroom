package com.bookaroom.utils;

import android.Manifest;

public class Constants {

    public static final String BASE_URL = "https://10.0.2.2";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final int PICK_IMAGE_REQUEST_CODE = 1;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;

    public static final String INTENT_TYPE_IMAGE = "image/*";
    public static final String[] READ_STORAGE_PERMISSIONS_ARRAY = {Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] READ_LOCATION_PERMISSIONS_ARRAY = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    public static final int PROFILE_PICTURE_QUALITY = 90;
    public static final float DEFAULT_MAP_ZOOM = 5;
}
