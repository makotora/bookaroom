package com.bookaroom.utils;

public class SessionManager {
    private static String authenticationToken;

    public static void setAuthenticationToken(String authenticationToken) {
        SessionManager.authenticationToken = authenticationToken;
        storeAuthenticationToken(authenticationToken);
    }

    private static void storeAuthenticationToken(String authenticationToken) {
        // TODO
    }

    public static String getAuthenticationToken() {
        if (authenticationToken == null) {
            authenticationToken = getStoredAuthenticationToken();
        }

        return authenticationToken;
    }

    private static String getStoredAuthenticationToken() {
        // TODO
        return null;
    }

}
