package com.bookaroom.utils.session;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.bookaroom.enums.UserRole;

public class SessionManager {
    private static final String SESSION_PREFERENCES_NAME = "sessionPreferences";
    private static final String AUTHENTICATION_TOKEN_KEY = "authenticationToken";
    private static final String USER_ROLE_KEY = "userRole";

    public static void setAuthenticationToken(Activity activity, String authenticationToken) {
        SharedPreferences.Editor spEditor = getSharedPreferences(activity).edit();
        spEditor.putString(AUTHENTICATION_TOKEN_KEY, authenticationToken);
        spEditor.commit();
    }

    public static String getAuthenticationToken(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(AUTHENTICATION_TOKEN_KEY, null);
    }

    public static void setUserRole(Activity activity, UserRole userRole) {
        SharedPreferences.Editor spEditor = getSharedPreferences(activity).edit();
        spEditor.putString(USER_ROLE_KEY, userRole.name());
        spEditor.commit();
    }

    public static UserRole getUserRole(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        String userRoleString = sp.getString(USER_ROLE_KEY, null);

        UserRole userRole = null;

        if (userRoleString != null) {
            try {
                userRole = UserRole.valueOf(userRoleString);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return userRole;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SESSION_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


}
