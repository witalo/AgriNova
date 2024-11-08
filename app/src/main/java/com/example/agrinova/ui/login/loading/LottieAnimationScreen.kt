package com.example.agrinova.ui.login.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.agrinova.R
import kotlinx.coroutines.delay

@Composable
fun LottieAnimationScreen(
    onAnimationFinished: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.granja))

    // Estado para controlar si la animación debe continuar
    var isPlaying by remember { mutableStateOf(true) }

    // Efecto que se ejecutará después de un delay específico
    LaunchedEffect(Unit) {
        delay(3000) // 3 segundos, puedes ajustar este valor
        isPlaying = false
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            // La animación se reproducirá solo una vez
            iterations = 1,
            // Controlamos si la animación está reproduciéndose
            isPlaying = isPlaying,
            modifier = Modifier
                .size(200.dp)
        )
    }
}
