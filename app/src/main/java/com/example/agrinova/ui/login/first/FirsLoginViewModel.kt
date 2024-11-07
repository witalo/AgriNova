package com.example.agrinova.ui.login.first
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstLoginViewModel @Inject constructor(
    private val empresaDao: EmpresaDao,
    private val userPreferences: UsePreferences,
    private val empresaRepository: EmpresaRepository,
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    init {
        checkIfCompanyRegistered() // Verificar si la empresa ya está registrada
    }

    private fun checkIfCompanyRegistered() {
        viewModelScope.launch {
            combine(
                userPreferences.isCompanyRegistered,
                userPreferences.companyName,
                userPreferences.companyId
            ) { isRegistered, name, id ->
                if (isRegistered && name != null && id != null) {
                    LoginState.Success(companyName = name, companyId = id)
                } else {
                    LoginState.Idle
                }
            }.collect { loginState ->
                _loginState.value = loginState
            }
        }
    }

    fun loginEmpresa(ruc: String, correo: String, password: String) {
        if (ruc.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = GraphQLClient.apolloClient.mutate(LoginEmpresaMutation(ruc, correo, password)).execute()
                if (response.hasErrors()) {
                    _loginState.value = LoginState.Error("Error en el inicio de sesión")
                } else {
                    val empresaData = response.data?.loginEmpresa?.empresa
                    Log.d("Italo First Login", empresaData.toString())
                    if (empresaData != null) {
                        // Almacena el nombre y el ID de la empresa en UserPreferences
                        userPreferences.saveCompanyData(empresaData.razonSocial!!, empresaData.id!!)

                        // Sincroniza los datos de la empresa
                        empresaRepository.syncEmpresaData(empresaData.id.toInt())
                        // getEmpresaData(empresaData.id)

                        _loginState.value = LoginState.Success(companyName = empresaData.razonSocial, companyId = empresaData.id)
                    } else {
                        _loginState.value = LoginState.Error("Datos de empresa no válidos")
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }

    private fun getEmpresaData(empresaId: Int) {
        viewModelScope.launch {
            try {
                val response = GraphQLClient.apolloClient.query(GetEmpresaDataQuery(empresaId.toString())).execute()
                if (response.hasErrors()) {
                    _loginState.value = LoginState.Error("Error al obtener los datos de la empresa")
                } else {
                    val empresaData = response.data?.empresaById
                    Log.d("Italo2", empresaData.toString())
                    if (empresaData != null) {
                        val empresaEntity = EmpresaDataModel.fromGraphQL(empresaData)
                        empresaDao.insertEmpresa(empresaEntity.toEntity()) // Guarda en la base de datos
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }

}
