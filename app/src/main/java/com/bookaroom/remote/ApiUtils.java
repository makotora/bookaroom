package com.bookaroom.remote;

import android.app.Activity;

import com.bookaroom.remote.services.ListingReviewsService;
import com.bookaroom.remote.services.ListingService;
import com.bookaroom.remote.services.UserService;

import static com.bookaroom.utils.Constants.BASE_URL;

public class ApiUtils {

    public static UserService getUserService(Activity currentActivity){
        return RetrofitClient.getClient(currentActivity, BASE_URL).create(UserService.class);
    }

    public static ListingService getListingService(Activity currentActivity){
        return RetrofitClient.getClient(currentActivity, BASE_URL).create(ListingService.class);
    }

    public static ListingReviewsService getListingReviewsService(Activity currentActivity){
        return RetrofitClient.getClient(currentActivity, BASE_URL).create(ListingReviewsService.class);
    }
}
