package com.it.ptdei.features.login

data class LoginRequest(
    val username: String,
    val password: String,
    val busUnit: String
)
