package com.example.agrinova.ui.login.first

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agrinova.R
import com.example.agrinova.ui.login.loading.ModernLoadingOverlay
import kotlinx.coroutines.launch

@Composable
fun FirstLoginScreen(
    viewModel: FirstLoginViewModel = hiltViewModel(),
    onLoginSuccess: (String, Int) -> Unit
) {
    var ruc by remember { mutableStateOf("20498655468") }
    var correo by remember { mutableStateOf("alozada@fundoamerica.com.pe") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Para mostrar mensajes de error en Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_tierra),
                    contentDescription = "Imagen de login",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = ruc,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) ruc = newValue
                    },
                    label = { Text("RUC de Empresa") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isLoading = true
                        viewModel.loginEmpresa(ruc, correo, password)
                    },
                    modifier = Modifier
                        .widthIn(min = 150.dp, max = 250.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = !isLoading
                ) {
                    Text(text = "Registrar", color = MaterialTheme.colorScheme.onPrimary)
                }

//                if (isLoading) {
//                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
//                }
            }
            // Observar el estado del login -----------------------------




        }
        val loginState by viewModel.loginState.collectAsState()
        AnimatedVisibility(
            visible = loginState is LoginState.Loading,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
            // Puedes ajustar las duraciones y curvas de animación
//                        animationSpec = tween(
//                        durationMillis = 2000,
//                easing = FastOutSlowInEasing
//            )
        ) {
            ModernLoadingOverlay(
                message = "Sincronizando datos de la empresa...\nPor favor, espere un momento"
            )
        }
        // Manejar estados
        LaunchedEffect(loginState) {
            when (loginState) {
                is LoginState.Success -> {
                    val state = loginState as LoginState.Success
                    onLoginSuccess(state.companyName, state.companyId.toInt())
                }
                is LoginState.Error -> {
                    val errorMessage = (loginState as LoginState.Error).message
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Long
                    )
                    isLoading = false // Asegúrate de resetear el estado de carga
                }
                is LoginState.Loading -> {
                    isLoading = true // Bloquea el botón mientras se carga
                }
                else -> {
                    isLoading = false // Asegúrate de que el botón esté habilitado en otros casos
                }
            }
        }
    }
}
