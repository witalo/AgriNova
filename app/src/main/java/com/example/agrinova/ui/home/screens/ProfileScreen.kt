package com.example.agrinova.ui.home.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agrinova.data.repository.UsuarioRepository
import com.example.agrinova.di.models.UsuarioDomainModel
import com.example.agrinova.ui.navigation.AppNavigator
import kotlinx.coroutines.launch
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium
        )
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
