package com.bookaroom.utils;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestUtils {
    public static MultipartBody.Part getMultipartBodyPartForFile(File file, String requestParameterName) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(requestParameterName, file.getName(), requestFile);
    }

    public static RequestBody getRequestBodyForString(String str) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), str);
    }

    public static RequestBody getRequestBodyForList(List<Object> list) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, new Gson().toJson(list));
    }
}
