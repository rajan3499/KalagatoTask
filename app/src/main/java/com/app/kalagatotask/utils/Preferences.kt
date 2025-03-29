package com.app.kalagatotask.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Locale

class Preferences(private val parentContext: Context) {

    private val pref: SharedPreferences by lazy {
        parentContext.getSharedPreferences(
            appKey,
            Context.MODE_PRIVATE
        )
    }
    private val appKey: String = parentContext.packageName.replace("\\.".toRegex(), "_")
        .lowercase(Locale.getDefault())

    fun setString(key: String, value: String) = pref.edit {
        putString(key, value)
    }

    fun getString(key: String, value: String = ""): String? {
        return pref.getString(key, value)
    }

    fun setDouble(key: String, value: Double) = pref.edit {
        putString(key, "$value")
    }

    fun getDouble(key: String, value: Double = 0.0): Double? {
        return if (pref.getString(key, "$value")!!.isNotEmpty()) {
            pref.getString(key, "$value")!!.toDouble()
        } else {
            null
        }
    }

    fun setBoolean(key: String, value: Boolean) = pref.edit {
        putBoolean(key, value)
    }

    fun getBoolean(key: String, value: Boolean = false): Boolean {
        return pref.getBoolean(key, value)
    }

    fun setInt(key: String, value: Int) = pref.edit {
        putInt(key, value)
    }

    fun getInt(key: String, value: Int = 0): Int {
        return pref.getInt(key, value)
    }

    fun setLong(key: String, value: Long) = pref.edit {
        putLong(key, value)
    }

    fun getLong(key: String, value: Long = 0): Long {
        return pref.getLong(key, value)
    }

    fun isExist(key: String): Boolean {
        return pref.contains(key)
    }

    fun clearData() = pref.edit { clear() }
}