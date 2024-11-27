package com.example.agrinova.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.agrinova.ui.home.evaluations.EvaluationScreen
import com.example.agrinova.ui.home.evaluations.NewEvaluationScreen
import com.example.agrinova.ui.home.screens.ProfileScreen
import com.example.agrinova.ui.login.second.SecondLoginScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
//    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    onLogoutClick: () -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    //Nueva navegacion con texto
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.onRouteChanged(destination.route ?: "")
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
                                Color(0xFF106901)  // Color final
                            )
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF0D7BD2), // Fondo azul personalizado
                                    shape = RoundedCornerShape(6.dp) // Bordes redondeados
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFBBDEFB), // Borde azul claro
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(0.dp) // Espaciado entre el borde y el contenido
                        ) {
                            Text(
                                text = selectedTab.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White, // Texto blanco
                                    fontWeight = FontWeight.Bold // Negrita para resaltar
                                ),
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp) // Espaciado interno del texto
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Fondo transparente para ver el gradiente
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(
                            onClick = {
                                // Acción para "Descargar datos" (cambia la lógica según tus necesidades)
                            },
                            modifier = Modifier
                                .size(50.dp) // Tamaño adecuado para un botón prominente
                                .padding(8.dp) // Espaciado interno para evitar que el ícono quede pegado al borde
                                .clip(CircleShape) // Forma circular
                                .background(Color(0xFF0D7BD2)) // Fondo azul personalizado
                                .border(2.dp, Color(0xFFBBDEFB), CircleShape) // Borde de color claro
                                .animateContentSize() // Animación fluida al interactuar
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download, // Ícono de descarga
                                contentDescription = "Descargar datos", // Descripción para accesibilidad
                                modifier = Modifier.size(25.dp), // Tamaño del ícono
                                tint = Color.White // Color del ícono
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre botones

                        // Botón de Cerrar Sesión
                        IconButton(
                            onClick = onLogoutClick, // Acción para cerrar sesión
                            modifier = Modifier
                                .size(50.dp) // Tamaño prominente
                                .padding(8.dp) // Espaciado interno
                                .clip(CircleShape) // Forma circular
                                .background(Color(0xFF0D7BD2)) // Fondo rojo para cerrar sesión (puedes cambiar)
                                .border(2.dp, Color(0xFFFFFFFF), CircleShape) // Borde rojo claro para contraste
                                .animateContentSize() // Animación fluida
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp, // Ícono de "salir"
                                contentDescription = "Cerrar sesión", // Descripción accesible
                                modifier = Modifier.size(25.dp), // Tamaño del ícono
                                tint = Color.White // Ícono blanco para contraste
                            )
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
    )
    { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                InitialScreen(navController = navController)
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
            composable(BottomNavItem.Report.route) {
                ReportScreen()
            }
            composable("evaluation") {
                EvaluationScreen(navController = navController)
            }
            composable("newEvaluation/{cartillaId}") {backStackEntry->
                NewEvaluationScreen(
                    navController,
                    backStackEntry.arguments?.getString("cartillaId")!!
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