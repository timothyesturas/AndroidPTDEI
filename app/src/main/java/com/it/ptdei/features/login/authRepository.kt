package com.it.ptdei.features.login

import android.util.Log
import com.it.ptdei.features.logout.LogoutRequest
import com.it.ptdei.features.logout.LogoutResponse
import com.it.ptdei.global.SessionManager

class AuthRepository(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) {

    suspend fun login(request: LoginRequest): LoginResponse {

        val response = api.login(request)

        if (response.success) {
            sessionManager.setUsername(request.username)
            sessionManager.setBusUnit(request.busUnit)
            Log.d("pass username: ", request.username)
            response.appUser?.let {
                sessionManager.setAdmin(it.admin)
                sessionManager.setDisplayName(it.displayName)
                sessionManager.setUserAccess(it.menuAccess)
            }
        }

        return response
    }

    suspend fun logout(request: LogoutRequest): LogoutResponse {
        val response = api.logout(request)
        if (response.success) {
            sessionManager.clearSession()
        }
        return response
    }
}