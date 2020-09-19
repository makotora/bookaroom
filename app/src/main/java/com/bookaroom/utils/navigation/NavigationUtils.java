package com.bookaroom.utils.navigation;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.bookaroom.R;
import com.bookaroom.activities.HomeActivity;
import com.bookaroom.activities.HostActivity;
import com.bookaroom.activities.RegisterActivity;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.ActivityNavigationInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NavigationUtils {
    private static final Map<Integer, Class> menuItemIdToActivity;
    static {
        Map<Integer, Class> initMap = new HashMap<>();
        initMap.put(R.id.nav_home, HomeActivity.class);
        initMap.put(R.id.nav_host, HostActivity.class);
        menuItemIdToActivity = Collections.unmodifiableMap(initMap);
    }

    private static final Map<Class, ActivityNavigationInfo> activityToNavigationInfo;
    static {
        Map<Class, ActivityNavigationInfo> initMap = new HashMap<>();
        initMap.put(HomeActivity.class, new ActivityNavigationInfo(0, (Activity a) -> startHomeActivity(a)));
        initMap.put(HostActivity.class, new ActivityNavigationInfo(3, (Activity a) -> startHostActivity(a)));
        activityToNavigationInfo = Collections.unmodifiableMap(initMap);
    }

    public static void initializeBottomNavigationBar(Activity currentActivity) {
        BottomNavigationView bottomNavView = (BottomNavigationView) currentActivity.findViewById(R.id.bottom_navigation);

        ActivityNavigationInfo activityNavigationInfo = activityToNavigationInfo.get(currentActivity.getClass());
        if (activityNavigationInfo == null) {
            Utils.makeInternalErrorToast(currentActivity);
            return;
        }

        bottomNavView.getMenu().getItem(activityNavigationInfo.getMenuItemPosition()).setChecked(true);
        bottomNavView.setOnNavigationItemSelectedListener( (item) -> handleBottomNavBarSelection(currentActivity, item));
    }

    private static boolean handleBottomNavBarSelection(Activity currentActivity, MenuItem item) {
        Class targetActivityClass = menuItemIdToActivity.get(item.getItemId());
        if (targetActivityClass == null) {
            Utils.makeInternalErrorToast(currentActivity);
            return false;
        }

        ActivityNavigationInfo activityNavigationInfo = activityToNavigationInfo.get(targetActivityClass);
        if (activityNavigationInfo == null) {
            Utils.makeInternalErrorToast(currentActivity);
            return false;
        }

        activityNavigationInfo.startActivity(currentActivity);
        return true;
    }

    public static void startRegisterActivity(Activity currentActivity) {
        Intent registerIntent = new Intent(currentActivity, RegisterActivity.class);
        currentActivity.startActivity(registerIntent);
    }

    public static void startHomeActivity(Activity currentActivity) {
        Intent homeIntent = new Intent(currentActivity, HomeActivity.class);
        currentActivity.startActivity(homeIntent);
    }

    public static void startHostActivity(Activity currentActivity) {
        Intent hostIntent = new Intent(currentActivity, HostActivity.class);
        currentActivity.startActivity(hostIntent);
    }
}
