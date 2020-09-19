package com.bookaroom.utils;

import android.app.Activity;
import android.widget.Toast;

import androidx.core.util.Consumer;

import com.bookaroom.models.ActionResponse;

import retrofit2.Response;

public class ResponseUtils {
    public static void handleActionResponse(
            Activity currentActivity,
            Response<ActionResponse> response,
            Consumer<ActionResponse> onSuccess,
            Consumer<ActionResponse> onFailure) {
        if (response.isSuccessful()) {
            ActionResponse actionResponse = response.body();

            if (actionResponse == null) {
                Utils.makeInternalErrorToast(currentActivity);
                return;
            }

            Toast.makeText(currentActivity,
                           actionResponse.getMessage(),
                           Toast.LENGTH_SHORT).show();

            if (actionResponse.isSuccess()) {
                onSuccess.accept(actionResponse);
            }
            else {
                onFailure.accept(actionResponse);
            }
        } else {
            Utils.makeInternalErrorToast(currentActivity);
        }

    }
}
