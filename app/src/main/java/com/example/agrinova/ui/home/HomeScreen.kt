package com.example.agrinova.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import com.example.agriapp.ui.home.screens.InitialScreen
import com.example.agriapp.ui.home.screens.ProfileScreen
import com.example.agriapp.ui.home.screens.ReportScreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedTab.title,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == item,
                        onClick = {
                            viewModel.onTabSelected(item)
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
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
        }
    }
}