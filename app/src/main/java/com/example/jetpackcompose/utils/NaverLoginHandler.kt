package com.example.jetpackcompose.utils


import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class NaverLoginHandler(
    private val onSuccess: (String) -> Unit,
    private val onFailure: (String) -> Unit
) : OAuthLoginCallback {
    override fun onSuccess() {
        val accessToken = NaverIdLoginSDK.getAccessToken()
        if (!accessToken.isNullOrEmpty()) {
            onSuccess(accessToken)
        } else {
            onFailure("Access Token is empty")
        }
    }

    override fun onFailure(httpStatus: Int, message: String) {
        onFailure("Login failed: $message")
    }

    override fun onError(errorCode: Int, message: String) {
        onFailure("Error occurred: $message")
    }
}
