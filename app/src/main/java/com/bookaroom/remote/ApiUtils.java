package com.bookaroom.remote;

import com.bookaroom.remote.services.UserService;

import static com.bookaroom.utils.Constants.BASE_URL;

public class ApiUtils {

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
