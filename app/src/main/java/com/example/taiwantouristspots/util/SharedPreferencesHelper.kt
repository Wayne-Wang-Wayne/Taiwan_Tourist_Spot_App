package com.example.taiwantouristspots.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.withContext

class SharedPreferencesHelper {

    companion object {
        private var prefs: SharedPreferences? = null
        private const val PREF_TIME = "Pref time"

        @Volatile
        private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SharedPreferencesHelper =
            instance ?: synchronized(LOCK) {
                instance ?: buildSharedPreferencesHelper(context).also {
                    instance = it
                }
            }

        private fun buildSharedPreferencesHelper(context: Context): SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }

    }

    fun saveUpdateTime(time: Long) {
        if (prefs == null) {
            return
        }
        prefs!!.edit().putLong(PREF_TIME, time).commit()

    }

    fun getUpdateTime() = prefs?.getLong(PREF_TIME, 0)


}