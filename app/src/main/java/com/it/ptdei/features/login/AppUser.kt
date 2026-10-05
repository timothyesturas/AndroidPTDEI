package com.it.ptdei.features.login

data class AppUser(
    val displayName: String?,
    val menuAccess: List<String>?,
    val admin: Boolean
)
