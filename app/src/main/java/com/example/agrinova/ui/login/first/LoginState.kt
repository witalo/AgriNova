package com.example.agrinova.ui.login.first

//sealed class LoginState {
//    object Idle : LoginState()
//    object Loading : LoginState()
//    //    object Success : LoginState()
//    data class Success(val companyName: String, val companyId: Int) : LoginState()
//    data class Error(val message: String) : LoginState()
//}
// Estados para manejar el proceso de login y sincronización
sealed class LoginState {
    object Idle : LoginState()
    data class Loading(
        val message: String = "Sincronizando datos...",
        val progress: Float = 0f,
        val step: LoadingStep = LoadingStep.AUTHENTICATING
    ) : LoginState()
    data class Success(val companyName: String, val companyId: String) : LoginState()
    data class Error(val message: String) : LoginState()
}