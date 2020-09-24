package com.bookaroom.utils.navigation;

import android.app.Activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationInitInfo {
    private final Activity currentActivity;
    private final BottomNavigationView bottomNavigationView;

    public NavigationInitInfo(
            Activity currentActivity,
            BottomNavigationView bottomNavigationView) {
        this.currentActivity = currentActivity;
        this.bottomNavigationView = bottomNavigationView;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}
