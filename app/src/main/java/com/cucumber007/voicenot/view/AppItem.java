package com.cucumber007.voicenot.view;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppItem implements Comparable<AppItem> {
    private ApplicationInfo applicationInfo;
    private String appName;
    private String packageName;
    private Drawable appIcon;
    private boolean isSelected;

    public AppItem(ApplicationInfo applicationInfo, String appName, String packageName, Drawable appIcon, boolean isSelected) {
        this.applicationInfo = applicationInfo;
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
        this.isSelected = isSelected;
    }

    public Drawable getImage() {
        return appIcon;
    }

    public String getTitle() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public int compareTo(AppItem o) {
        if(o.isSelected && isSelected()) return 0;
        if(o.isSelected) return 1;
        if(isSelected()) return -1;
        return appName.compareTo(o.getTitle());
    }
}