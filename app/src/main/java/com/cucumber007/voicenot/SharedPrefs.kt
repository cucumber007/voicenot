package com.cucumber007.voicenot

import com.cucumber007.voicenot.utils.NonNullBooleanSetting
import com.cucumber007.voicenot.utils.StringListSetting
import com.cucumber007.voicenot.utils.StringSetting


public object SharedPrefs {

    public val isServiceActiveSetting = object : NonNullBooleanSetting() {
        override val key = "isServiceActiveSetting"
    }

    public val spellTitleSetting = object : NonNullBooleanSetting() {
        override val key = "spellTitleSetting"
    }

    public val headphonesOnlySetting = object : NonNullBooleanSetting() {
        override val key = "headphonesOnlySetting"
    }

    public val appWhitelistSetting = object : StringListSetting() {
        override val key = "appWhitelistSetting"
    }



}