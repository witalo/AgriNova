package com.example.agrinova.ui.login.first

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agrinova.R

@Composable
fun FirstLoginScreen(
    viewModel: FirstLoginViewModel = hiltViewModel(), // Change to hiltViewModel()
    onLoginSuccess: (String, Int) -> Unit
){
    var ruc by remember { mutableStateOf("20451152549") }
    var correo by remember { mutableStateOf("empresa@gmail.com") }
    var password by remember { mutableStateOf("empresa") }
    var isLoading by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 400.dp) // Establece un ancho máximo para el contenido
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center, // Centra verticalmente
                horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agriapp),
                    contentDescription = "Imagen de login",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = ruc,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            ruc = newValue
                        }
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
                    modifier = Modifier.widthIn(min = 150.dp, max = 250.dp).padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
        //            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground),
                    enabled = !isLoading
                ) {
                    Text(text = "Registrar", color = MaterialTheme.colorScheme.onPrimary)
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    }

    // Observa el estado de autenticación
    val loginState by viewModel.loginState.collectAsState()
    when (loginState) {
        is LoginState.Success -> {
            val companyName = (loginState as LoginState.Success).companyName
            val companyId = (loginState as LoginState.Success).companyId
            LaunchedEffect(Unit) {
                onLoginSuccess(companyName, companyId)
                // Limpiar los campos después del inicio de sesión exitoso
                ruc = ""
                correo = ""
                password = ""
                isLoading = false // Reiniciar la carga
            }
        }
        is LoginState.Error -> {
            // Muestra un mensaje de error
            Text("Error: ${(loginState as LoginState.Error).message}", color = Color.Red)
            isLoading = false // Reiniciar la carga
        }
        is LoginState.Loading -> {
            CircularProgressIndicator()
        }
        is LoginState.Idle -> {
            // Puedes manejar el estado Idle si lo deseas
        }
    }
}
