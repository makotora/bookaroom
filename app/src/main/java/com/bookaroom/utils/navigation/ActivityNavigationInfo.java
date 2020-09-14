package com.bookaroom.utils.navigation;

import android.app.Activity;

import androidx.core.util.Consumer;

public class ActivityNavigationInfo {
    private final int menuItemPosition;
    private final Consumer<Activity> startFromActivityConsumer;

    public ActivityNavigationInfo(int menuItemPosition, Consumer<Activity> startFromActivityConsumer) {
        this.menuItemPosition = menuItemPosition;
        this.startFromActivityConsumer = startFromActivityConsumer;
    }

    public int getMenuItemPosition() {
        return menuItemPosition;
    }

    public void startActivity(Activity currentActivity) {
        this.startFromActivityConsumer.accept(currentActivity);
    }
}
