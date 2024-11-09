package com.example.agrinova.ui.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.agrinova.data.repository.UsuarioRepository
import com.example.agrinova.di.models.UsuarioDomainModel
import com.example.agrinova.ui.navigation.AppNavigator
import kotlinx.coroutines.launch
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel() // Inyección del ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF48C02D)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF5DC745)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF8C8B8B),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = uiState.userFirstName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = uiState.userLastName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                // Info Cards
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        InfoRow(
                            icon = Icons.Default.Create,
                            label = "DNI",
                            value = uiState.userDni
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(
                            icon = Icons.Default.Phone,
                            label = "Telefono",
                            value = uiState.userPhone
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Correo",
                            value = uiState.userEmail
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        InfoRow(
                            icon = if (uiState.userActive) Icons.Default.CheckCircle else Icons.Default.Close,
                            label = "Estado",
                            value = if (uiState.userActive) "Activo" else "Inactivo",
                            valueColor = if (uiState.userActive) Color(0xFF4CAF50) else Color.Red // Cambia color según estado
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = valueColor // Aplica el color pasado o usa el predeterminado
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor // Aplica color según el valor pasado
            )
        }
    }
}
//
//@Composable
//fun ProfileScreen(
//    usuarioRepository: UsuarioRepository,
//    appNavigator: AppNavigator,
//    navController: NavHostController
//) {
//    var usuario: UsuarioDomainModel? by remember { mutableStateOf(null) }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        // Cargar los datos del usuario que se ha logueado
//        val userId = usuarioRepository.getCurrentUserId()
//        usuario = usuarioRepository.getUserById(userId)
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        if (usuario == null) {
//            CircularProgressIndicator()
//        } else {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text("Perfil de Usuario")
//                Text("Nombre: ${usuario?.firstName} ${usuario?.lastName}")
//                Text("DNI: ${usuario?.document}")
//                Text("Telfono: ${usuario?.phone}")
//                Text("Correo: ${usuario?.email}")
//                Text("Estado: ${usuarioRepository.getUserStatusText(usuario)}")
//                Spacer(modifier = Modifier.height(16.dp))
//                Button(
//                    onClick = {
//                        // Usamos el alcance de corrutina para invocar funciones suspendidas
//                        coroutineScope.launch {
//                            usuarioRepository.logout()
//                            appNavigator.navigateToSecondLogin(navController)
//                        }
//                    }
//                ) {
//                    Text("Cerrar Sesión")
//                }
//            }
//        }
//    }
//}
