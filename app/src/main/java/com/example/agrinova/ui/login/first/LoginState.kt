package com.example.agrinova.ui.login.first

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    //    object Success : LoginState()
    data class Success(val companyName: String, val companyId: Int) : LoginState()
    data class Error(val message: String) : LoginState()
}
