package com.cucumber007.voicenot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cucumber007.reusables.ContextApplication;
import com.cucumber007.reusables.preferences.BooleanPreference;
import com.cucumber007.reusables.preferences.Setting;
import com.cucumber007.voicenot.reusables.BasePreferenceModel;

public class PreferencesModel extends BasePreferenceModel {
    private static PreferencesModel instance;
    public static PreferencesModel getInstance() {
        if (instance == null) {
            instance = new PreferencesModel(PreferenceManager.getDefaultSharedPreferences(ContextApplication.getContext()));
        }
        return instance;
    }
    PreferencesModel(SharedPreferences preferences) {
        super(preferences);
    }

    public Setting<Boolean> isServiceActiveSetting() {
        return new BooleanPreference(getPreferences()) {
            @Override
            public String getKey() {
                return "IsServiceActive";
            }
        };
    }

    public Setting<Boolean> spellTitleSetting() {
        return new BooleanPreference(getPreferences()) {
            @Override
            public String getKey() {
                return "SpellTitle";
            }
        };
    }

    public Setting<Boolean> headphonesOnlySetting() {
        return new BooleanPreference(getPreferences()) {
            @Override
            public String getKey() {
                return "HeadphonesOnly";
            }

            @Override
            public Boolean load() {
                return getPreferences().getBoolean(getKey(), true);
            }
        };
    }
}
