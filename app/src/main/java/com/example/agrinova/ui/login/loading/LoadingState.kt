package com.example.agrinova.ui.login.loading

sealed class LoadingState {
    data class Loading(val message: String) : LoadingState()
    object Success : LoadingState()
    data class Error(val message: String) : LoadingState()
    object Idle : LoadingState()
}
