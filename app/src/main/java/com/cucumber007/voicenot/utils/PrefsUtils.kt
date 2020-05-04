package com.cucumber007.voicenot.utils

import android.preference.PreferenceManager
import com.cucumber007.voicenot.CustomApplication

abstract class Setting<T> {
    abstract val key: String
    val prefs = PreferenceManager.getDefaultSharedPreferences(CustomApplication.context)

    abstract fun load(): T
    abstract fun save(value: T)
}

abstract class StringSetting : Setting<String?>() {
    override fun load(): String? = prefs.getString(key, null)

    override fun save(value: String?) {
        prefs.edit().putString(key, value).apply()
    }
}

abstract class NonNullBooleanSetting : Setting<Boolean>() {
    override fun load(): Boolean = prefs.getBoolean(key, false)

    override fun save(value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}

abstract class StringListSetting : Setting<List<String>?>() {
    protected open val separator = "{;#.]"

    override fun load(): List<String>? = prefs.getString(key, null)?.split(separator)

    override fun save(value: List<String>?) {
        prefs.edit().putString(key, value?.joinToString(separator)).apply()
    }
}