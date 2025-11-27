package com.example.finsur.core.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _isAuthenticated = MutableStateFlow(isUserAuthenticated())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _userId = MutableStateFlow<Int?>(getUserId())
    val userId: StateFlow<Int?> = _userId.asStateFlow()

    init {
        Log.d("AuthStateManager", "Initialized with authenticated=${_isAuthenticated.value}, userId=${_userId.value}")
    }

    fun setAuthenticated(userId: Int, email: String) {
        Log.d("AuthStateManager", "Setting authenticated: userId=$userId, email=$email")
        prefs.edit().apply {
            putBoolean("is_authenticated", true)
            putInt("user_id", userId)
            putString("user_email", email)
            apply()
        }
        _isAuthenticated.value = true
        _userId.value = userId
    }

    fun clearAuthentication() {
        Log.d("AuthStateManager", "Clearing authentication")
        prefs.edit().apply {
            putBoolean("is_authenticated", false)
            remove("user_id")
            remove("user_email")
            apply()
        }
        _isAuthenticated.value = false
        _userId.value = null
    }

    fun isUserAuthenticated(): Boolean {
        return prefs.getBoolean("is_authenticated", false)
    }

    fun getUserId(): Int? {
        if (!isUserAuthenticated()) return null
        val userId = prefs.getInt("user_id", -1)
        return if (userId != -1) userId else null
    }

    fun getUserEmail(): String? {
        if (!isUserAuthenticated()) return null
        return prefs.getString("user_email", null)
    }
}
