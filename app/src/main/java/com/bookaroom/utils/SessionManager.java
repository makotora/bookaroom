package com.bookaroom.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String SESSION_PREFERENCES_NAME = "sessionPreferences";
    private static final String AUTHENTICATION_TOKEN_KEY = "authenticationToken";

    public static void setAuthenticationToken(Activity activity, String authenticationToken) {
        SharedPreferences.Editor spEditor = getSharedPreferences(activity).edit();
        spEditor.putString(AUTHENTICATION_TOKEN_KEY, authenticationToken);
        spEditor.commit();
    }

    public static String getAuthenticationToken(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(AUTHENTICATION_TOKEN_KEY, null);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SESSION_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


}
