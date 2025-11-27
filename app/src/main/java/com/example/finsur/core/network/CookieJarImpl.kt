package com.example.finsur.core.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieJarImpl(context: Context) : CookieJar {
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()
    private val prefs = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    init {
        loadCookies()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        cookieStore[host] = cookies.toMutableList()
        saveCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookies = cookieStore[host] ?: emptyList()
        return cookies.filter { !it.expiresAt.isExpired() }
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
