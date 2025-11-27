package com.example.finsur.core.config

object ApiConfig {
    // TODO: Replace with your actual API URL
    // For local development: http://10.0.2.2:3000 (Android emulator)
    // For physical device: http://YOUR_LOCAL_IP:3000
    // For production: https://your-api.com
    //const val BASE_URL = "http://192.168.141.92:3000/api/v1/"

    const val BASE_URL = "http://10.0.2.2:3000/api/v1/"

    // Timeout configurations
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
