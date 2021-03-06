package com.bookaroom.utils.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.view.MenuItem;

import androidx.core.util.Consumer;

import com.bookaroom.R;
import com.bookaroom.activities.ConversationsActivity;
import com.bookaroom.activities.HomeActivity;
import com.bookaroom.activities.HostActivity;
import com.bookaroom.activities.HostProfileActivity;
import com.bookaroom.activities.ListingActivity;
import com.bookaroom.activities.LoginActivity;
import com.bookaroom.activities.MessagesActivity;
import com.bookaroom.activities.ProfileActivity;
import com.bookaroom.activities.RegisterActivity;
import com.bookaroom.enums.UserRole;
import com.bookaroom.enums.ViewMode;
import com.bookaroom.models.ListingSearchRequest;
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

    private static final ViewMode DEFAULT_VIEW_MODE = ViewMode.Guest;

    static {
        Map<Integer, Class> initMap = new HashMap<>();
        initMap.put(R.id.nav_guest, HomeActivity.class);
        initMap.put(R.id.nav_host, HostActivity.class);
        initMap.put(R.id.nav_messages, ConversationsActivity.class);
        initMap.put(R.id.nav_profile, ProfileActivity.class);
        menuItemIdToActivity = Collections.unmodifiableMap(initMap);
    }

    private static final Map<Class, ActivityNavigationInfo> activityToNavigationInfo;
    static {
        Map<Class, ActivityNavigationInfo> initMap = new HashMap<>();
        initMap.put(HomeActivity.class, new ActivityNavigationInfo(MENU_ITEM_GUEST_POSITION,
                                                                   (Activity a) -> handleHomeActivityNavigation(a)));
        initMap.put(ListingActivity.class, new ActivityNavigationInfo(MENU_ITEM_GUEST_POSITION,
                                                                   (Activity a) -> {}));
        initMap.put(HostProfileActivity.class, new ActivityNavigationInfo(MENU_ITEM_GUEST_POSITION,
                                                                      (Activity a) -> {}));
        initMap.put(ConversationsActivity.class, new ActivityNavigationInfo(MENU_ITEM_MESSAGES_POSITION,
                                                                          (Activity a) -> startConversationsActivity(a)));
        initMap.put(MessagesActivity.class, new ActivityNavigationInfo(MENU_ITEM_MESSAGES_POSITION,
                                                              (Activity a) -> {}));
        initMap.put(ProfileActivity.class, new ActivityNavigationInfo(MENU_ITEM_PROFILE_POSITION,
                                                                       (Activity a) -> startProfileActivity(a)));
        initMap.put(HostActivity.class, new ActivityNavigationInfo(MENU_ITEM_HOST_POSITION,
                                                                   (Activity a) -> handleHostActivityNavigation(a)));
        activityToNavigationInfo = Collections.unmodifiableMap(initMap);
    }

    private static final Map<Class, Consumer<NavigationInitInfo>> activityToBottomNavInit;
    static {
        Map<Class, Consumer<NavigationInitInfo>> initMap = new HashMap<>();
        // Not used currently but feature works
        activityToBottomNavInit = Collections.unmodifiableMap(initMap);
    }

    public static void initializeBottomNavigationBar(Activity currentActivity) {
        BottomNavigationView bottomNavView = (BottomNavigationView) currentActivity.findViewById(R.id.bottom_navigation);

        NavigationInitInfo navigationInitInfo = new NavigationInitInfo(currentActivity,
                                                                       bottomNavView);

        Consumer<NavigationInitInfo> bottomNavigationViewInitializer =
                activityToBottomNavInit.get(currentActivity.getClass());
        if (bottomNavigationViewInitializer != null) {
            bottomNavigationViewInitializer.accept(navigationInitInfo);
        }

        updateNavigationBarForViewMode(navigationInitInfo);

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

    private static void updateNavigationBarForViewMode(NavigationInitInfo navigationInitInfo) {
        Activity currentActivity = navigationInitInfo.getCurrentActivity();
        ViewMode currentViewMode = SessionManager.getViewMode(currentActivity);

        if (currentViewMode == null) {
            currentViewMode = DEFAULT_VIEW_MODE;
        }

        if (currentViewMode == ViewMode.Guest) {
            updateNavigationForGuest(navigationInitInfo);
        }
        else if (currentViewMode == ViewMode.Host) {
            updateNavigationForHost(navigationInitInfo);
        }
        else {
            Utils.makeInternalErrorToast(currentActivity);
        }
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
        bottomNavigationView.getMenu().getItem(MENU_ITEM_HOST_POSITION).setEnabled(userIsHost(navigationInitInfo));
    }

    private static boolean userIsHost(NavigationInitInfo navigationInitInfo) {
        Activity currentActivity = navigationInitInfo.getCurrentActivity();
        UserRole userRole = SessionManager.getUserRole(currentActivity);
        return userRole == UserRole.Host;
    }

    public static void startRegisterActivity(Activity currentActivity) {
        Intent registerIntent = new Intent(currentActivity, RegisterActivity.class);
        currentActivity.startActivity(registerIntent);
    }

    public static void startLoginActivity(Activity currentActivity) {
        Intent homeIntent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(homeIntent);
    }

    private static void handleHomeActivityNavigation(Activity currentActivity) {
        SessionManager.setViewMode(currentActivity, ViewMode.Guest);
        startHomeActivity(currentActivity);
    }

    public static void startHomeActivity(Activity currentActivity) {
        Intent homeIntent = new Intent(currentActivity, HomeActivity.class);
        currentActivity.startActivity(homeIntent);
    }

    private static void handleHostActivityNavigation(Activity currentActivity) {
        SessionManager.setViewMode(currentActivity, ViewMode.Host);
        startHostActivity(currentActivity);
    }

    public static void startHostActivity(Activity currentActivity) {
        Intent hostIntent = new Intent(currentActivity, HostActivity.class);
        currentActivity.startActivity(hostIntent);
    }

    public static void startListingActivity(Activity currentActivity, long listingId,
                                            ListingSearchRequest lastSearchRequest) {
        Intent hostIntent = new Intent(currentActivity, ListingActivity.class);
        hostIntent.putExtra(ListingActivity.INTENT_EXTRA_LISTING_ID_NAME, listingId);

        if (lastSearchRequest != null) {
            if (lastSearchRequest.getCheckIn() != null) {
                hostIntent.putExtra(ListingActivity.INTENT_EXTRA_CHECK_IN_NAME,
                                    lastSearchRequest.getCheckIn().getTime());
            }
            if (lastSearchRequest.getCheckOut() != null) {
                hostIntent.putExtra(ListingActivity.INTENT_EXTRA_CHECK_OUT_NAME,
                                    lastSearchRequest.getCheckOut().getTime());
            }
            hostIntent.putExtra(ListingActivity.INTENT_EXTRA_NUM_GUESTS_NAME,
                                lastSearchRequest.getNumberOfGuests());
        }

        currentActivity.startActivity(hostIntent);
    }

    public static void startHostProfileActivity(Activity currentActivity, Long hostId) {
        Intent hostProfileIntent = new Intent(currentActivity, HostProfileActivity.class);
        hostProfileIntent.putExtra(HostProfileActivity.INTENT_EXTRA_HOST_ID_NAME, hostId);

        currentActivity.startActivity(hostProfileIntent);
    }

    public static void startConversationsActivity(Activity currentActivity) {
        Intent conversationsIntent = new Intent(currentActivity, ConversationsActivity.class);
        currentActivity.startActivity(conversationsIntent);
    }

    public static void startProfileActivity(Activity currentActivity) {
        Intent conversationsIntent = new Intent(currentActivity, ProfileActivity.class);
        currentActivity.startActivity(conversationsIntent);
    }

    public static void startMessagesActivity(Activity currentActivity, Long conversationId) {
        Intent messagesIntent = new Intent(currentActivity, MessagesActivity.class);
        messagesIntent.putExtra(MessagesActivity.INTENT_EXTRA_CONVERSATION_ID_NAME,
                                new Long(conversationId));
        SessionManager.conversationId = conversationId;

        currentActivity.startActivity(messagesIntent);
    }
}
