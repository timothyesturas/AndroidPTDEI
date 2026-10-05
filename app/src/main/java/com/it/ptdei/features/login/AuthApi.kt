package com.it.ptdei.features.login

import com.it.ptdei.features.logout.LogoutRequest
import com.it.ptdei.features.logout.LogoutResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("LoginServlet")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse


    @POST("LogoutServlet")
    suspend fun logout(
        @Body request: LogoutRequest
    ): LogoutResponse

}