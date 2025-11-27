package com.google.mediapipe.examples.gesturerecognizer.data.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.mediapipe.examples.gesturerecognizer.data.models.User

class AuthManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "AuthPrefs"
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_ROLE_KEY = "user_role"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    var authToken: String
        get() = sharedPreferences.getString(TOKEN_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(TOKEN_KEY, value).apply()
    
    var userId: String
        get() = sharedPreferences.getString(USER_ID_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(USER_ID_KEY, value).apply()
    
    var userName: String
        get() = sharedPreferences.getString(USER_NAME_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(USER_NAME_KEY, value).apply()
    
    var userEmail: String
        get() = sharedPreferences.getString(USER_EMAIL_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(USER_EMAIL_KEY, value).apply()
    
    var userRole: String
        get() = sharedPreferences.getString(USER_ROLE_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(USER_ROLE_KEY, value).apply()
    
    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_LOGGED_IN_KEY, value).apply()
    
    fun clearAuthData() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun saveUserData(user: User, token: String) {
        authToken = token
        userId = user.userId
        userName = user.name
        userEmail = user.email
        userRole = user.role
        isLoggedIn = true
    }
    
    fun getUser(): User {
        return User(
            userId = userId,
            name = userName,
            email = userEmail,
            role = userRole
        )
    }
}