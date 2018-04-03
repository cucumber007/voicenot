package com.cucumber007.voicenot.reusables;

import android.content.SharedPreferences;

public abstract class BasePreferenceModel {

    private SharedPreferences preferences;

    public BasePreferenceModel(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
