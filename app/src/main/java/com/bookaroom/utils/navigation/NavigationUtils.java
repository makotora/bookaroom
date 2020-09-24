package com.bookaroom.utils.navigation;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.util.Consumer;

import com.bookaroom.R;
import com.bookaroom.activities.HomeActivity;
import com.bookaroom.activities.HostActivity;
import com.bookaroom.activities.LoginActivity;
import com.bookaroom.activities.RegisterActivity;
import com.bookaroom.enums.UserRole;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NavigationUtils {
    private static final Map<Integer, Class> menuItemIdToActivity;

    private static final int MENU_ITEM_MESSAGES_POSITION = 0;
    private static final int MENU_ITEM_PROFILE_POSITION = 1;
    private static final int MENU_ITEM_GUEST_POSITION = 2;
    private static final int MENU_ITEM_HOST_POSITION = 3;

    static {
        Map<Integer, Class> initMap = new HashMap<>();
        initMap.put(R.id.nav_guest, HomeActivity.class);
        initMap.put(R.id.nav_host, HostActivity.class);
        menuItemIdToActivity = Collections.unmodifiableMap(initMap);
    }

    private static final Map<Class, ActivityNavigationInfo> activityToNavigationInfo;
    static {
        Map<Class, ActivityNavigationInfo> initMap = new HashMap<>();
        initMap.put(HomeActivity.class, new ActivityNavigationInfo(MENU_ITEM_GUEST_POSITION,
                                                                   (Activity a) -> startHomeActivity(a)));
        initMap.put(HostActivity.class, new ActivityNavigationInfo(MENU_ITEM_HOST_POSITION,
                                                                   (Activity a) -> startHostActivity(a)));
        activityToNavigationInfo = Collections.unmodifiableMap(initMap);
    }

    private static final Map<Class, Consumer<NavigationInitInfo>> activityToBottomNavInit;
    static {
        Map<Class, Consumer<NavigationInitInfo>> initMap = new HashMap<>();
        initMap.put(HomeActivity.class,
                    (navigationInitInfo -> updateNavigationForGuest(navigationInitInfo)));
        initMap.put(HostActivity.class, (navigationInitInfo -> updateNavigationForHost(navigationInitInfo)));
        activityToBottomNavInit = Collections.unmodifiableMap(initMap);
    }

    public static void initializeBottomNavigationBar(Activity currentActivity) {
        BottomNavigationView bottomNavView = (BottomNavigationView) currentActivity.findViewById(R.id.bottom_navigation);

        Consumer<NavigationInitInfo> bottomNavigationViewInitializer =
                activityToBottomNavInit.get(currentActivity.getClass());
        if (bottomNavigationViewInitializer != null) {
            bottomNavigationViewInitializer.accept(new NavigationInitInfo(currentActivity, bottomNavView));
        }

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

    private static void updateNavigationForHost(NavigationInitInfo navigationInitInfo) {
        BottomNavigationView bottomNavigationView = navigationInitInfo.getBottomNavigationView();

        bottomNavigationView.getMenu().getItem(MENU_ITEM_GUEST_POSITION).setVisible(true);
        bottomNavigationView.getMenu().getItem(MENU_ITEM_HOST_POSITION).setVisible(false);
    }

    private static void updateNavigationForGuest(NavigationInitInfo navigationInitInfo) {
        BottomNavigationView bottomNavigationView = navigationInitInfo.getBottomNavigationView();

        bottomNavigationView.getMenu().getItem(MENU_ITEM_GUEST_POSITION).setVisible(false);
        bottomNavigationView.getMenu().getItem(MENU_ITEM_HOST_POSITION).setVisible(true);

        Activity currentActivity = navigationInitInfo.getCurrentActivity();
        UserRole userRole = SessionManager.getUserRole(currentActivity);
        if (userRole == UserRole.Guest) {
            bottomNavigationView.getMenu().getItem(MENU_ITEM_HOST_POSITION).setEnabled(false);
        }
    }

    public static void startRegisterActivity(Activity currentActivity) {
        Intent registerIntent = new Intent(currentActivity, RegisterActivity.class);
        currentActivity.startActivity(registerIntent);
    }

    public static void startLoginActivity(Activity currentActivity) {
        Intent homeIntent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(homeIntent);
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
