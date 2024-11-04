//package com.example.agrinova.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import kotlinx.coroutines.launch
//import dagger.hilt.android.scopes.ActivityRetainedScoped
//import javax.inject.Inject
//
//@ActivityRetainedScoped
//class AppNavigator @Inject constructor(
//    private val userPreferences: UserPreferences
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
//                            userPreferences.saveCompanyData(companyName, companyId)
//                            navController.navigate("second_login") {
//                                popUpTo("first_login") { inclusive = true }
//                            }
//                        }
//                    }
//                )
//            }
//
////            composable("second_login") {
////                SecondLoginScreen(
////                    viewModel = hiltViewModel(),
////                    onNavigateToHome = {
////                        navController.navigate("home") {
////                            popUpTo("second_login") { inclusive = true }
////                        }
////                    }
////                )
////            }
////
////            composable("home") {
////                HomeScreen(
////                    viewModel = hiltViewModel()
////                )
////            }
//        }
//    }
//}