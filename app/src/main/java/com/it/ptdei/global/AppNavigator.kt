package com.it.ptdei.global

import com.it.ptdei.menu.salesapproval.features.getOrders.OrderScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.it.ptdei.features.login.AuthRepository
import com.it.ptdei.features.login.LoginScreen
import com.it.ptdei.features.login.LoginViewModel
import com.it.ptdei.menu.customerPo.ui.CustomerPOScreen
import com.it.ptdei.menu.customerPo.CustomerPOViewModel
import com.it.ptdei.menu.returns.ProcessScreen
import com.it.ptdei.menu.returns.ReturnsScreen
import com.it.ptdei.menu.returns.ReturnsViewModel
import com.it.ptdei.menu.salesapproval.SalesApprovalViewModel
import com.it.ptdei.menu.salesapproval.SalesApprovalRepository

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigator(
    modifier: Modifier,
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ---------------- SESSION ----------------
    val sessionManager = remember {
        SessionManager(context)
    }

    val okHttpClient = remember {
        getOkHttpClient(sessionManager)
    }

    val authApi = remember {
        RetrofitClient.createAuthApi(
            baseUrl = ApiConfig.BASE_URL, okHttpClient = okHttpClient
        )
    }

    val authRepository = remember {
        AuthRepository(authApi, sessionManager)
    }


    val loginViewModel = remember {
        LoginViewModel(authRepository)
    }

    // ---------------- NAV HOST ----------------
    NavHost(
        navController = navController, startDestination = "login"
    ) {

        // ---------------- LOGIN ----------------
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel, modifier = Modifier.fillMaxSize(), onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                })
        }

        // ---------------- HOME ----------------
        composable("home") {
            SessionGuard(sessionManager, navController) {
                HomeScreen(
                    sessionManager = sessionManager,
                    loginViewModel = loginViewModel,
                    onLogout = {
                        loginViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onSalesApprovalClick = {
                        navController.navigate("SalesApproval")
                    },
                    onUnitEqApprovalClick = {},
                    onReturnsClick = {
                        navController.navigate("returns")
                    },
                    onReverseClick = {
                        navController.navigate("ProcessScreen")
                    },
                    onCustomerPOClick = {
                        navController.navigate("customerPo")
                    })
            }
        }


        // ---------------- CUSTOMER PO ----------------
        composable("customerPo") {

            val returnsViewModel = remember {
                CustomerPOViewModel(okHttpClient)
            }

            SessionGuard(sessionManager, navController) {
                CustomerPOScreen(
                    viewModel = returnsViewModel, onBack = {
                        navController.popBackStack()
                    }, onLogout = {
                        sessionManager.clearSession()
                        loginViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }, okHttpClient = okHttpClient, sessionManager = sessionManager
                )
            }
        }


        // ---------------- RETURNS ----------------
        composable("returns") {

//            val returnsApi = remember {
//                RetrofitClient.createReturnsApi(
//                    baseUrl = ApiConfig.BASE_URL,
//                    okHttpClient = okHttpClient
//                )
//            }
//            val returnsRepository = remember {
//                ReturnsRepository(returnsApi)
//            }

            val returnsViewModel = remember {
                ReturnsViewModel(okHttpClient)
            }

            SessionGuard(sessionManager, navController) {
                ReturnsScreen(
                    viewModel = returnsViewModel, onBack = {
                        navController.popBackStack()
                    }, onLogout = {
                        sessionManager.clearSession()
                        loginViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }, okHttpClient = okHttpClient, sessionManager = sessionManager
                )
            }
        }



        // ---------------- RETURNS ----------------
        composable("ProcessScreen") {

            val returnsViewModel = remember {
                ReturnsViewModel(okHttpClient)
            }

            SessionGuard(sessionManager, navController) {
                ProcessScreen(
                    viewModel = returnsViewModel, onBack = {
                        navController.popBackStack()
                    }, okHttpClient = okHttpClient
                )
            }
        }


        // ---------------- SALES APPROVAL ----------------
        composable("SalesApproval") {
            val salesApprovalApi = remember {
                RetrofitClient.createSalesApprovalApi(
                    baseUrl = ApiConfig.BASE_URL, okHttpClient = okHttpClient
                )
            }


            val salesApprovalRepository = remember {
                SalesApprovalRepository(salesApprovalApi)
            }


            val salesApprovalViewModel = remember {
                SalesApprovalViewModel(
                    repo = salesApprovalRepository
                )
            }

            SessionGuard(sessionManager, navController) {

                OrderScreen(
                    viewModel = salesApprovalViewModel, onBack = {
                        navController.popBackStack()
                    }, onLogout = {
                        sessionManager.clearSession()
                        loginViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }, okHttpClient = okHttpClient
                )
            }
        }
    }
}
