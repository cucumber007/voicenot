package com.cucumber007.voicenot;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cucumber007.reusables.ContextApplication;
import com.cucumber007.reusables.preferences.BasePreferenceModel;
import com.cucumber007.reusables.preferences.BooleanPreference;
import com.cucumber007.reusables.preferences.Setting;
import com.cucumber007.reusables.utils.logging.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public Setting<Set<String>> appWhitelistSetting() {
        return new Setting<Set<String>>() {
            @Override
            public String getKey() {
                return "appWhitelist";
            }

            @Override
            public void save(Set<String> appItems) {
                getPreferences().edit().putStringSet(this.getKey(),
                        appItems).apply();
                LogUtil.logList(new ArrayList<>(appItems));
            }

            @Override
            public Set<String> load() {
                Set<String> res = getPreferences().getStringSet(getKey(), new HashSet<>());
                res.add(VoicenotApplication.getContext().getPackageName());
                return res;
            }
        };
    }
}
