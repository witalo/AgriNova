package com.example.agrinova.ui.login.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingScreen() {
    // Animación de rotación infinita
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Logotipo o texto de la aplicación
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AgriNova", // Cambia esto al nombre de tu app o logotipo
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Círculo giratorio
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp).rotate(rotation),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 4.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cargando...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
