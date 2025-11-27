package com.example.finsur.core.network

import android.util.Log
import com.example.finsur.core.auth.AuthStateManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authStateManager: AuthStateManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Check for 401 Unauthorized responses
        if (response.code == 401) {
            Log.w("AuthInterceptor", "401 Unauthorized response from ${request.url}")
            Log.w("AuthInterceptor", "This indicates the backend authentication middleware is not recognizing the session cookie")

            // Clear authentication state
            authStateManager.clearAuthentication()

            // Note: We don't redirect here because interceptors run on background threads
            // The repositories will handle the 401 error and the UI will navigate to login
        }

        return response
    }
}
