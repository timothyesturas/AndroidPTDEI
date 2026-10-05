package com.it.ptdei.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it.ptdei.features.logout.LogoutRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repo: AuthRepository,
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String, busUnit: String) {

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = repo.login(
                    LoginRequest(username, password, busUnit)
                )

                _loginState.value = if (response.success) {
                    LoginState.LoginSuccess
                } else {
                    LoginState.Error("Invalid login")
                }


            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error")
            }
        }
    }

    fun logout(request: LogoutRequest) {


        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = repo.logout(request)

                _loginState.value = if (response.success) {
                    LoginState.LogoutSuccess
                } else {
                    LoginState.Error("Logout failed")
                }

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object LoginSuccess : LoginState()
        object LogoutSuccess : LoginState()
        data class Error(val message: String) : LoginState()
    }
}