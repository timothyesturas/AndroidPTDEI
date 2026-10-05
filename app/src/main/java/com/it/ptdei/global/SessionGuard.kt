package com.it.ptdei.global

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController


@Composable
fun SessionGuard(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    // Check session validity
    val isLoggedIn = sessionManager.isLoggedIn() // <-- use isLoggedIn() instead

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (isLoggedIn) {
        content()
    }
}
