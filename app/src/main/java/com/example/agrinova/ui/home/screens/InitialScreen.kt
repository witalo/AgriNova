package com.example.agrinova.ui.home.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun InitialScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController,
    onNavigate: (String) -> Unit,
) {
    val options = listOf("Evaluaciones", "NewEvaluacion", "Opción 3", "Opción 4")  // Opciones de ejemplo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                OptionCard(
                    option = option,
                    onOptionClick = {
                        onNavigate(option)
                    }
                )
            }
        }
    }
}

@Composable
fun OptionCard(option: String, onOptionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onOptionClick() },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16B02C)  // Color personalizado en hexadecimal
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFCFDFD)
                )
                Text(
                    text = "Descripción de la opción",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFAF9F8)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Ir a $option",
                tint = Color(0xFFFAF8F8)  // También puedes usar un color del esquema de color, e.g., MaterialTheme.colorScheme.primary
            )
        }
    }
}
//@Composable
//fun InitialScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Bienvenido a Inicio",
//            style = MaterialTheme.typography.headlineMedium
//        )
//    }
//}