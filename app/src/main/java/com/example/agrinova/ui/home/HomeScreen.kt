package com.example.agrinova.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrinova.R
import com.example.agriapp.ui.home.screens.InitialScreen
import com.example.agriapp.ui.home.screens.ReportScreen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedTab.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF43BD28),
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
        },
        bottomBar = {
            NavigationBar {
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                InitialScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
            composable(BottomNavItem.Report.route) {
                ReportScreen()
            }
            composable("login") {
                SecondLoginScreen(
                    onNavigateToHome = {
                        navController.navigate(BottomNavItem.Home.route) {
                            // Limpia el stack para evitar volver a la pantalla de login
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) // Reemplaza con tu pantalla de inicio de sesión
            }
        }
    }
}