package com.it.ptdei.global

import android.content.Context
import android.util.Log
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COOKIE = "cookie"
        private const val KEY_USERNAME = "username"
        private const val KEY_BUS_UNIT = "bus_unit"
        private const val KEY_ADMIN = "admin"
        private const val KEY_DISPLAY_NAME = "first_name"
        private const val KEY_USER_ACCESS = "user_access"
    }

    fun saveCookie(cookie: String) {

        prefs.edit {
            putString(KEY_COOKIE, cookie)
        }

        Log.d(
            "COOKIE_DEBUG", "Saved cookie = ${prefs.getString(KEY_COOKIE, null)}"
        )
    }

    fun getCookie(): String? {

        val cookie = prefs.getString(KEY_COOKIE, null)

        Log.d(
            "COOKIE_DEBUG", "Read cookie = $cookie"
        )

        return cookie
    }

    fun setUsername(username: String) {
        prefs.edit { putString(KEY_USERNAME, username) }
    }

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "nullable") ?: "nullable"

    fun setBusUnit(busUnit: String) {
        prefs.edit { putString(KEY_BUS_UNIT, busUnit) }
    }

    fun getBusUnit(): String = prefs.getString(KEY_BUS_UNIT, "nullable") ?: "nullable"

    fun setDisplayName(displayDisplay: String?) {
        prefs.edit { putString(KEY_DISPLAY_NAME, displayDisplay) }
    }

    fun getDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)


    fun setAdmin(admin: Boolean) {
        prefs.edit { putBoolean(KEY_ADMIN, admin) }
    }

    fun getAdmin(): Boolean = prefs.getBoolean(KEY_ADMIN, false)


    fun setUserAccess(access: List<String>?) {
        prefs.edit {
            putStringSet(KEY_USER_ACCESS, access?.toSet())
        }
    }

    fun getUserAccess(): List<String> {
        return prefs.getStringSet(KEY_USER_ACCESS, emptySet())?.toList() ?: emptyList()
    }

    fun clearSession() {
        prefs.edit { clear() }
    }

    fun isLoggedIn(): Boolean {
        return !getCookie().isNullOrEmpty()
    }
}