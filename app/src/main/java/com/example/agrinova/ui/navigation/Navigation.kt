package com.example.agrinova.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.ui.home.HomeScreen
import com.example.agrinova.ui.login.first.FirstLoginScreen
import com.example.agrinova.ui.login.second.SecondLoginScreen
import kotlinx.coroutines.launch
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class AppNavigator @Inject constructor(
    private val userPreferences: UsePreferences
) {
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val isCompanyRegistered = userPreferences.isCompanyRegistered.collectAsState(initial = false).value
        val coroutineScope = rememberCoroutineScope()

        val startDestination = if (isCompanyRegistered) "second_login" else "first_login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable("first_login") {
                FirstLoginScreen(
                    viewModel = hiltViewModel(),
                    onLoginSuccess = { companyName, companyId ->
                        coroutineScope.launch {
                            try {
                                userPreferences.saveCompanyData(companyName, companyId)
                                navController.navigate("second_login") {
                                    popUpTo("first_login") { inclusive = true }

                                }
                            } catch (e: Exception) {
                                // Maneja el error aquí, tal vez mostrando un mensaje al usuario
                                Log.e("AppNavigation", "Error al navegar: ${e.message}")
                            }
                        }
                    }
                )
            }

            composable("second_login") {
                SecondLoginScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("second_login") { inclusive = true }
                        }
                    }
                )
            }
//
//            composable("home") {
//                HomeScreen(
//                    viewModel = hiltViewModel()
//                )
//            }
        }
    }
}