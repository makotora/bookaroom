package com.bookaroom.utils;

import com.bookaroom.adapters.data.AvailabilityRange;
import com.bookaroom.interfaces.RequestStringConvertible;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;

public class RequestUtils {

    public static MultipartBody.Part getMultipartBodyPartForFile(File file, String requestParameterName) {
        return getMultipartBodyPartForFile(file, requestParameterName, "multipart/form-data");
    }

    public static MultipartBody.Part getMultipartBodyPartForFile(File file,
                                                                 String requestParameterName,
                                                                 String mediaType) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(mediaType), file);
        return MultipartBody.Part.createFormData(requestParameterName, file.getName(), requestFile);
    }

    public static RequestBody getRequestBodyForString(String str) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), str);
    }

    public static RequestBody getRequestBodyForObject(Object obj) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, new Gson().toJson(obj));
    }

    public static <E> RequestBody getRequestBodyForList(List<E> list) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, new Gson().toJson(list));
    }

    public static List<MultipartBody.Part> getMultipartsForFiles(Collection<File> files,
                                                                 String requestParamName) {
        List<MultipartBody.Part> multiparts = new ArrayList<>();

        int i = 0;
        for (File file : files) {
            i++;
            multiparts.add(getMultipartBodyPartForFile(file,
                                                       requestParamName));
        }

        return multiparts;
    }

    public static String toRequestString(Collection<? extends RequestStringConvertible> rsConvertibles) {
        StringBuilder requestString = new StringBuilder();
        boolean isFirst = true;

        for (RequestStringConvertible rsc : rsConvertibles) {
            if (!isFirst) {
                requestString.append(";");
            }
            requestString.append(rsc.toRequestString());

            isFirst = false;
        }

        return requestString.toString();
    }
}
