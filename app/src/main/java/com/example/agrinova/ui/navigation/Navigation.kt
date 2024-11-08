package com.example.agrinova.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.ui.home.HomeScreen
import com.example.agrinova.ui.login.first.FirstLoginScreen
import com.example.agrinova.ui.login.loading.LottieAnimationScreen
import com.example.agrinova.ui.login.second.SecondLoginScreen
import kotlinx.coroutines.launch
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import javax.inject.Inject

@ActivityRetainedScoped
class AppNavigator @Inject constructor(
    private val userPreferences: UsePreferences
) {
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val isCompanyRegistered = userPreferences.isCompanyRegistered.collectAsState(initial = false).value
        val scope = rememberCoroutineScope()

        // Usamos LaunchedEffect para manejar la navegación inicial
        LaunchedEffect(isCompanyRegistered) {
            if (isCompanyRegistered) {
                // Si la compañía está registrada, configuramos la ruta inicial como lottie_animation
                // pero nos aseguramos de que vaya directamente a second_login después
                delay(2000) // Pequeño delay para asegurar que la navegación funcione correctamente
                navController.navigate("second_login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        NavHost(navController = navController, startDestination = "lottie_animation") {
            composable("lottie_animation") {
                LottieAnimationScreen(
                    onAnimationFinished = {
                        if (!isCompanyRegistered) {
                            navController.navigate("first_login") {
                                popUpTo("lottie_animation") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("first_login") {
                FirstLoginScreen(
                    viewModel = hiltViewModel(),
                    onLoginSuccess = { companyName, companyId ->
                        scope.launch {
                            try {
                                userPreferences.saveCompanyData(companyName, companyId)
                                navController.navigate("second_login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                Log.e("AppNavigation", "Error navigating: ${e.message}")
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

            composable("home") {
                HomeScreen(
                    viewModel = hiltViewModel(),
                    onLogoutClick = {
                        // Navega de vuelta a la SecondLoginScreen
                        navController.navigate("second_login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
//                HomeScreen(
//                    viewModel = hiltViewModel()
//                )
            }

        }
    }

    fun navigateToSecondLogin(navController: NavHostController) {
        navController.navigate("second_login") {
            popUpTo(0) { inclusive = true }
        }
    }

}
//@ActivityRetainedScoped
//class AppNavigator @Inject constructor(
//    private val userPreferences: UsePreferences
//) {
//    private var hasLoggedInSuccessfully = false
//
//    @Composable
//    fun AppNavigation() {
//        val navController = rememberNavController()
//        val isCompanyRegistered = userPreferences.isCompanyRegistered.collectAsState(initial = false).value
//        val scope = rememberCoroutineScope()
//
//        NavHost(navController = navController, startDestination = "lottie_animation") {
//            composable("lottie_animation") {
//                LottieAnimationScreen(
//                    onAnimationFinished = {
//                        if (isCompanyRegistered) {
//                            navController.navigate("second_login") {
//                                popUpTo("lottie_animation") { inclusive = true }
//                            }
//                        } else {
//                            navController.navigate("first_login") {
//                                popUpTo("lottie_animation") { inclusive = true }
//                            }
//                        }
//                    }
//                )
//            }
//
//            composable("first_login") {
//                FirstLoginScreen(
//                    viewModel = hiltViewModel(),
//                    onLoginSuccess = { companyName, companyId ->
//                        scope.launch {
//                            try {
//                                userPreferences.saveCompanyData(companyName, companyId)
//                                hasLoggedInSuccessfully = true
//                                navController.navigate("second_login") {
//                                    popUpTo("lottie_animation") { inclusive = true }
//                                }
//                            } catch (e: Exception) {
//                                // Handle the error
//                                Log.e("AppNavigation", "Error navigating: ${e.message}")
//                            }
//                        }
//                    }
//                )
//            }
//
//            composable("second_login") {
//                SecondLoginScreen(
//                    viewModel = hiltViewModel(),
//                    onNavigateToHome = {
//                        navController.navigate("home") {
//                            popUpTo("second_login") { inclusive = true }
//                        }
//                    }
//                )
//            }
//
//            composable("home") {
//                HomeScreen(
//                    viewModel = hiltViewModel()
//                )
//            }
//        }
//    }
//}
//@ActivityRetainedScoped
//class AppNavigator @Inject constructor(
//    private val userPreferences: UsePreferences
//) {
//    @Composable
//    fun AppNavigation() {
//        val navController = rememberNavController()
//        val isCompanyRegistered = userPreferences.isCompanyRegistered.collectAsState(initial = false).value
//        val coroutineScope = rememberCoroutineScope()
//
//        val startDestination = if (isCompanyRegistered) "second_login" else "first_login"
//
//        NavHost(navController = navController, startDestination = startDestination) {
//            composable("first_login") {
//                FirstLoginScreen(
//                    viewModel = hiltViewModel(),
//                    onLoginSuccess = { companyName, companyId ->
//                        coroutineScope.launch {
//                            try {
//                                userPreferences.saveCompanyData(companyName, companyId)
//                                navController.navigate("second_login") {
//                                    popUpTo("first_login") { inclusive = true }
//
//                                }
//                            } catch (e: Exception) {
//                                // Maneja el error aquí, tal vez mostrando un mensaje al usuario
//                                Log.e("AppNavigation", "Error al navegar: ${e.message}")
//                            }
//                        }
//                    }
//                )
//            }
//
//            composable("second_login") {
//                SecondLoginScreen(
//                    viewModel = hiltViewModel(),
//                    onNavigateToHome = {
//                        navController.navigate("home") {
//                            popUpTo("second_login") { inclusive = true }
//                        }
//                    }
//                )
//            }
//
//            composable("home") {
//                HomeScreen(
//                    viewModel = hiltViewModel()
//                )
//            }
//        }
//    }
//}