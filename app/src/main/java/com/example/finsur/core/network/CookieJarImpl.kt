package com.example.finsur.core.network

import android.content.Context
import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieJarImpl(context: Context) : CookieJar {
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()
    private val prefs = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    init {
        loadCookies()
        Log.d("CookieJar", "Loaded ${cookieStore.values.sumOf { it.size }} cookies from storage")
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        Log.d("CookieJar", "saveFromResponse for $host - received ${cookies.size} cookies")

        // Get the existing cookies for this host, or create a new list
        val existingCookies = cookieStore[host]?.toMutableList() ?: mutableListOf()

        cookies.forEach { cookie ->
            Log.d("CookieJar", "  Processing cookie: ${cookie.name}=${cookie.value.take(20)}... " +
                    "domain=${cookie.domain}, path=${cookie.path}, " +
                    "expiresAt=${cookie.expiresAt}, secure=${cookie.secure}, httpOnly=${cookie.httpOnly}")

            // Remove any existing cookie with the same name before adding the new one
            existingCookies.removeAll { it.name == cookie.name }

            // If cookie has no expiry (session cookie), set it to expire in 30 days
            val persistentCookie = if (cookie.expiresAt == 253402300799999L) { // Max date for session cookies
                Log.d("CookieJar", "  Converting session cookie to persistent (30 days)")
                Cookie.Builder()
                    .name(cookie.name)
                    .value(cookie.value)
                    .domain(cookie.domain) // Respect the original domain
                    .path(cookie.path)
                    .expiresAt(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) // 30 days
                    .apply {
                        if (cookie.secure) secure()
                        if (cookie.httpOnly) httpOnly()
                    }
                    .build()
            } else {
                cookie
            }
            existingCookies.add(persistentCookie)
        }

        // Store the updated list of cookies for this host
        cookieStore[host] = existingCookies
        saveCookies()
        Log.d("CookieJar", "Saved/Updated cookies for host: $host. Total now: ${existingCookies.size}")
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookies = cookieStore[host] ?: emptyList()
        val validCookies = cookies.filter { !it.expiresAt.isExpired() }

        // Clean up expired cookies
        if (validCookies.size != cookies.size) {
            cookieStore[host] = validCookies.toMutableList()
            saveCookies()
            Log.d("CookieJar", "Cleaned up ${cookies.size - validCookies.size} expired cookies for host: $host")
        }

        Log.d("CookieJar", "loadForRequest to $host - sending ${validCookies.size} cookies")
        validCookies.forEach { cookie ->
            Log.d("CookieJar", "  Sending cookie: ${cookie.name}=${cookie.value.take(20)}... " +
                    "domain=${cookie.domain}, path=${cookie.path}")
        }

        return validCookies
    }

    fun clearCookies() {
        cookieStore.clear()
        prefs.edit().clear().apply()
    }

    private fun Long.isExpired(): Boolean {
        return this < System.currentTimeMillis()
    }

    private fun saveCookies() {
        val editor = prefs.edit()
        cookieStore.forEach { (host, cookies) ->
            val cookieString = cookies.joinToString(";;;") { cookie ->
                "${cookie.name}=${cookie.value}|${cookie.domain}|${cookie.path}|${cookie.expiresAt}|${cookie.secure}|${cookie.httpOnly}"
            }
            editor.putString(host, cookieString)
        }
        editor.apply()
    }

    private fun loadCookies() {
        val allEntries = prefs.all
        allEntries.forEach { (host, value) ->
            if (value is String && value.isNotEmpty()) {
                val cookies = value.split(";;;").mapNotNull { cookieStr ->
                    parseCookie(cookieStr)
                }
                cookieStore[host] = cookies.toMutableList()
            }
        }
    }

    private fun parseCookie(cookieStr: String): Cookie? {
        return try {
            val parts = cookieStr.split("|")
            if (parts.size >= 6) {
                val nameValue = parts[0].split("=")
                if (nameValue.size == 2) {
                    Cookie.Builder()
                        .name(nameValue[0])
                        .value(nameValue[1])
                        .domain(parts[1])
                        .path(parts[2])
                        .expiresAt(parts[3].toLong())
                        .apply {
                            if (parts[4].toBoolean()) secure()
                            if (parts[5].toBoolean()) httpOnly()
                        }
                        .build()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
