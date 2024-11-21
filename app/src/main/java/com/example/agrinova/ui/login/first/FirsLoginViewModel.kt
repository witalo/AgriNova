package com.example.agrinova.ui.login.first
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.agrinova.GetEmpresaDataQuery
import com.example.agrinova.LoginEmpresaMutation
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.remote.GraphQLClient
import com.example.agrinova.data.remote.model.EmpresaDataModel
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.di.UsePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FirstLoginViewModel @Inject constructor(
    private val empresaDao: EmpresaDao,
    private val userPreferences: UsePreferences,
    private val empresaRepository: EmpresaRepository,
) : ViewModel() {
//    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
//    val loginState: StateFlow<LoginState> get() = _loginState
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    init {
        checkIfCompanyRegistered() // Verificar si la empresa ya está registrada
    }

//    private fun checkIfCompanyRegistered() {
//        viewModelScope.launch {
//            combine(
//                userPreferences.isCompanyRegistered,
//                userPreferences.companyName,
//                userPreferences.companyId
//            ) { isRegistered, name, id ->
//                if (isRegistered && name != null && id != null) {
//                    LoginState.Success(companyName = name, companyId = id)
//                } else {
//                    LoginState.Idle
//                }
//            }.collect { loginState ->
//                _loginState.value = loginState
//            }
//        }
//    }
    private fun checkIfCompanyRegistered() {
        viewModelScope.launch {
            combine(
                userPreferences.isCompanyRegistered,
                userPreferences.companyName,
                userPreferences.companyId
            ) { isRegistered, name, id ->
                if (isRegistered && name != null && id != null) {
                    LoginState.Success(companyName = name, companyId = id.toString())
                } else {
                    LoginState.Idle
                }
            }.collect { state ->
                _loginState.value = state
            }
        }
    }

//    fun loginEmpresa(ruc: String, correo: String, password: String) {
//        if (ruc.isEmpty() || correo.isEmpty() || password.isEmpty()) {
//            _loginState.value = LoginState.Error("Por favor, completa todos los campos")
//            return
//        }
//
//        viewModelScope.launch {
//            _loginState.value = LoginState.Loading
//            try {
//                val response = GraphQLClient.apolloClient.mutation(
//                    LoginEmpresaMutation(
//                        ruc,
//                        correo,
//                        password
//                    )
//                )
//                    .execute()
//                if (response.hasErrors()) {
//                    _loginState.value = LoginState.Error("Error en el inicio de sesión")
//                } else {
//                    val empresaData = response.data?.loginEmpresa?.empresa
//                    if (empresaData != null) {
//                        // Almacena el nombre y el ID de la empresa en UserPreferences
//                        userPreferences.saveCompanyData(empresaData.razonSocial!!, empresaData.id!!)
//
//                        // Sincroniza los datos de la empresa
//                        empresaRepository.syncCompanyData(empresaData.id.toInt())
////                        empresaRepository.syncEmpresaData(empresaData.id.toInt())
//                        // getEmpresaData(empresaData.id)
//
//                        _loginState.value = LoginState.Success(companyName = empresaData.razonSocial, companyId = empresaData.id)
//                    } else {
//                        _loginState.value = LoginState.Error("Datos de empresa no válidos")
//                    }
//                }
//            } catch (e: Exception) {
//                _loginState.value = LoginState.Error("${e.message}")
//            }
//        }
//    }
    fun loginEmpresa(ruc: String, correo: String, password: String) {
        // Validación de campos
        if (!validateInputs(ruc, correo, password)) {
            return
        }

        viewModelScope.launch {
            try {
                // Inicio del proceso de login
                updateLoadingState(LoadingStep.AUTHENTICATING)

                // Llamada al login
                val response = performLogin(ruc, correo, password)

                // Procesar respuesta
                handleLoginResponse(response)

            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
    private fun validateInputs(ruc: String, correo: String, password: String): Boolean {
        when {
            ruc.isEmpty() -> {
                _loginState.value = LoginState.Error("El RUC es requerido")
                return false
            }
            correo.isEmpty() -> {
                _loginState.value = LoginState.Error("El correo es requerido")
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                _loginState.value = LoginState.Error("El correo no es válido")
                return false
            }
            password.isEmpty() -> {
                _loginState.value = LoginState.Error("La contraseña es requerida")
                return false
            }
            password.length < 6 -> {
                _loginState.value = LoginState.Error("La contraseña debe tener al menos 6 caracteres")
                return false
            }
        }
        return true
    }
    private suspend fun performLogin(ruc: String, correo: String, password: String): ApolloResponse<LoginEmpresaMutation.Data> {
        return GraphQLClient.apolloClient.mutation(
            LoginEmpresaMutation(
                ruc = ruc,
                email = correo,
                password = password
            )
        ).execute()
    }
    private suspend fun handleLoginResponse(response: ApolloResponse<LoginEmpresaMutation.Data>) {
        if (response.hasErrors()) {
            val errorMessage = response.errors?.firstOrNull()?.message ?: "Error en el inicio de sesión"
            _loginState.value = LoginState.Error(errorMessage)
            return
        }

        val empresaData = response.data?.loginEmpresa?.empresa
        if (empresaData == null) {
            _loginState.value = LoginState.Error("Datos de empresa no válidos")
            return
        }

        // Actualizar estado a sincronización
        updateLoadingState(LoadingStep.SYNCING_COMPANY)

        // Guardar datos de la empresa
        userPreferences.saveCompanyData(empresaData.razonSocial!!, empresaData.id!!)

        // Iniciar sincronización
        updateLoadingState(LoadingStep.SYNCING_DATA)
        syncCompanyData(empresaData.id.toInt())

        // Finalizar proceso
        updateLoadingState(LoadingStep.FINALIZING)

        // Actualizar estado a éxito
        _loginState.value = LoginState.Success(
            companyName = empresaData.razonSocial,
            companyId = empresaData.id.toString()
        )
    }

    private suspend fun syncCompanyData(companyId: Int) {
        try {
            empresaRepository.syncCompanyData(companyId)
        } catch (e: Exception) {
            throw Exception("Error al sincronizar datos: ${e.message}")
        }
    }

    private fun updateLoadingState(step: LoadingStep) {
        val message = when (step) {
            LoadingStep.AUTHENTICATING -> "Validando credenciales..."
            LoadingStep.SYNCING_COMPANY -> "Obteniendo información de la empresa..."
            LoadingStep.SYNCING_DATA -> "Sincronizando datos..."
            LoadingStep.FINALIZING -> "Finalizando..."
        }

        val progress = when (step) {
            LoadingStep.AUTHENTICATING -> 0.25f
            LoadingStep.SYNCING_COMPANY -> 0.50f
            LoadingStep.SYNCING_DATA -> 0.75f
            LoadingStep.FINALIZING -> 0.95f
        }

        _loginState.value = LoginState.Loading(
            message = message,
            progress = progress,
            step = step
        )
    }

    private fun handleError(error: Exception) {
        val errorMessage = when (error) {
            is IOException -> "Error de conexión. Verifica tu conexión a internet."
            is ApolloException -> "Error en el servicio. Intenta más tarde."
            else -> error.message ?: "Error desconocido"
        }
        _loginState.value = LoginState.Error(errorMessage)
    }

    // Método para limpiar el estado
    fun resetState() {
        _loginState.value = LoginState.Idle
    }

}


// Pasos del proceso de login
enum class LoadingStep {
    AUTHENTICATING,
    SYNCING_COMPANY,
    SYNCING_DATA,
    FINALIZING
}