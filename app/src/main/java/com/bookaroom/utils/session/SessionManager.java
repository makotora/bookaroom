package com.bookaroom.utils.session;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bookaroom.enums.UserRole;
import com.bookaroom.enums.ViewMode;

public class SessionManager {
    private static final String SESSION_PREFERENCES_NAME = "sessionPreferences";
    private static final String AUTHENTICATION_TOKEN_KEY = "authenticationToken";
    private static final String USER_ROLE_KEY = "userRole";
    private static final String VIEW_MODE_KEY = "userRole";

    private static String authenticationToken;
    private static ViewMode viewMode;
    private static UserRole userRole;

    public static void setAuthenticationToken(Activity activity, String authenticationToken) {
        SessionManager.authenticationToken = authenticationToken;

        // Also save token to shared preferences
        SharedPreferences.Editor spEditor = getSharedPreferences(activity).edit();
        spEditor.putString(AUTHENTICATION_TOKEN_KEY, authenticationToken);
        spEditor.commit();
    }

    public synchronized static String getAuthenticationToken(Context context) {
        if (authenticationToken == null) {
            // Try to retrieve the token from the shared preferences
            SharedPreferences sp = getSharedPreferences(context);
            authenticationToken = sp.getString(AUTHENTICATION_TOKEN_KEY, null);
        }

        return authenticationToken;
    }

    public synchronized static void setUserRole(Activity activity, UserRole userRole) {
        SessionManager.userRole = userRole;
    }

    public synchronized static UserRole getUserRole(Activity activity) {
        return userRole;
    }

    public synchronized static void setViewMode(Activity activity, ViewMode viewMode) {
        SessionManager.viewMode = viewMode;
    }

    public synchronized static ViewMode getViewMode(Activity activity) {
        return viewMode;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }


}
