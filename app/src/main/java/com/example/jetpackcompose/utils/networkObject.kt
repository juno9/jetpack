package com.example.jetpackcompose.utils

object networkObject {
    const val IP = "54.252.203.120"
    const val PORT = "8080"

    fun getBaseUrl(): String {
        return "http://$IP:$PORT"
    }
}