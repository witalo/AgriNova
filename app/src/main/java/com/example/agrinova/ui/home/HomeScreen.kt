package com.example.agrinova.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrinova.R
import com.example.agrinova.ui.home.screens.InitialScreen
import com.example.agriapp.ui.home.screens.ReportScreen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agrinova.ui.home.evaluations.EvaluationScreen
import com.example.agrinova.ui.home.evaluations.NewEvaluationScreen
import com.example.agrinova.ui.home.screens.ProfileScreen
import com.example.agrinova.ui.login.second.SecondLoginScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    onLogoutClick: () -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    fun navigateToScreen(option: String) {
        when (option) {
            "Evaluaciones" -> navController.navigate("EvaluationScreen")
            "NuevaEvaluacion" -> navController.navigate("NewEvaluationScreen"){
                // Agregamos opciones de navegación para asegurar un comportamiento correcto
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            "Opción 3" -> navController.navigate("option3Screen")
            else -> {}
        }
    }
    Scaffold(
        topBar = {
            // TopAppBar con Gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF43BD28), // Color inicial
                                Color(0xFF148102)  // Color final
                            )
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = selectedTab.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Fondo transparente para ver el gradiente
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = {
                            // Acción para "Upload data" (puedes cambiarla si necesitas)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Upload data",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre botones

                        // Botón de Cerrar Sesión
                        TextButton(
                            onClick = onLogoutClick,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salir", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )
            }
        },
        bottomBar = {
            // Box para envolver el NavigationBar y aplicar un gradiente de fondo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF43BD28), // Color inicial
                                Color(0xFF148102)  // Color final
                            )
                        )
                    )
            ) {
                NavigationBar(
                    modifier = Modifier.background(Color.Transparent) // Fondo transparente para la barra de navegación
                ) {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Profile,
                        BottomNavItem.Report
                    )

                    items.forEach { item ->
                        val isSelected = selectedTab == item
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                viewModel.onTabSelected(item)
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = item.selectedBackgroundColor,
                                selectedIconColor = item.selectedColor,
                                unselectedIconColor = item.unselectedColor
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                // Aquí pasamos el navController y la función de navegación a InitialScreen
                InitialScreen(navController = navController, onNavigate = ::navigateToScreen)
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(navController = navController) // También puedes pasar el navController si lo necesitas aquí
            }
            composable(BottomNavItem.Report.route) {
                ReportScreen(navController = navController) // Igualmente, si lo necesitas en ReportScreen
            }
            composable("EvaluationScreen") {
                EvaluationScreen(navController = navController, onNavigate = ::navigateToScreen) // Asegúrate de pasar navController
            }
            composable("NewEvaluationScreen") {
                NewEvaluationScreen(
                    navController = navController,
                    onNavigate = ::navigateToScreen
                )
            }
            composable("login") {
                SecondLoginScreen(
                    onNavigateToHome = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
// {
//    val selectedTab by viewModel.selectedTab.collectAsState()
//    fun navigateToScreen(option: String) {
//        when (option) {
//            "Evaluaciones" -> navController.navigate("EvaluationScreen")
//            "Opción 2" -> navController.navigate("option2Screen")
//            "Opción 3" -> navController.navigate("option3Screen")
//            else -> {}
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = selectedTab.title,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent, // Fondo transparente para ver el gradiente
//                    titleContentColor = Color.White
//                ),
//                actions = {
//                    IconButton(onClick = {
//                        // Acción para "Upload data" (puedes cambiarla si necesitas)
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                            contentDescription = "Upload data",
//                            tint = Color.White
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre botones
//
//                    // Botón de Cerrar Sesión
//                    TextButton(
//                        onClick = onLogoutClick,
//                        colors = ButtonDefaults.textButtonColors(
//                            contentColor = Color.White
//                        )
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
//                            contentDescription = "Cerrar sesión",
//                            tint = Color.White
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Salir", style = MaterialTheme.typography.bodyMedium)
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            NavigationBar {
//                val items = listOf(
//                    BottomNavItem.Home,
//                    BottomNavItem.Profile,
//                    BottomNavItem.Report
//                )
//
//                items.forEach { item ->
//                    val isSelected = selectedTab == item
//                    NavigationBarItem(
//                        icon = { Icon(item.icon, contentDescription = item.title) },
//                        label = { Text(item.title) },
//                        selected = isSelected,
//                        onClick = {
//                            viewModel.onTabSelected(item)
//                            navController.navigate(item.route) {
//                                popUpTo(navController.graph.startDestinationId)
//                                launchSingleTop = true
//                            }
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            indicatorColor = item.selectedBackgroundColor,
//                            selectedIconColor = item.selectedColor,
//                            unselectedIconColor = item.unselectedColor
//                        )
//                    )
//                }
//            }
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController = navController,
//            startDestination = BottomNavItem.Home.route,
//            modifier = Modifier.padding(paddingValues)
//        ) {
//            composable(BottomNavItem.Home.route) {
//                // Aquí pasamos el navController y la función de navegación a InitialScreen
//                InitialScreen(navController = navController, onNavigate = ::navigateToScreen)
//            }
//            composable(BottomNavItem.Profile.route) {
//                ProfileScreen(navController = navController) // También puedes pasar el navController si lo necesitas aquí
//            }
//            composable(BottomNavItem.Report.route) {
////                ReportScreen(navController = navController) // Igualmente, si lo necesitas en ReportScreen
//            }
//            composable("EvaluationScreen") {
//                EvaluationScreen(navController = navController) // Asegúrate de pasar navController
//            }
//            composable("login") {
//                SecondLoginScreen(
//                    onNavigateToHome = {
//                        // Navegar a Home desde la pantalla de login y limpiar el stack
//                        navController.navigate(BottomNavItem.Home.route) {
//                            popUpTo(0) { inclusive = true } // Esto asegura que el stack se limpia
//                        }
//                    }
//                )
//            }
//            composable(BottomNavItem.Home.route) {
//                InitialScreen()
//            }
//            composable(BottomNavItem.Profile.route) {
//                ProfileScreen()
//            }
//            composable(BottomNavItem.Report.route) {
//                ReportScreen()
//            }
//            composable("login") {
//                SecondLoginScreen(
//                    onNavigateToHome = {
//                        navController.navigate(BottomNavItem.Home.route) {
//                            // Limpia el stack para evitar volver a la pantalla de login
//                            popUpTo(0) { inclusive = true }
//                        }
//                    }
//                ) // Reemplaza con tu pantalla de inicio de sesión
//            }
//        }
//    }
//}
