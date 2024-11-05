package com.example.agrinova.ui.login.second

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agrinova.di.models.FundoDomainModel

@Composable
fun SecondLoginScreen(
    viewModel: SecondLoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit  // Cambiado para recibir solo la función de navegación
) {
    val fundus by viewModel.fundos.collectAsState() // Lista de fundos
    val validationState by viewModel.validationState.collectAsState() // Estado de validación
    val syncState by viewModel.syncState.collectAsState() // Estado de sincronización

    var dni by remember { mutableStateOf("") }
    var selectedFundo by remember { mutableStateOf<FundoDomainModel?>(null) }

    // Observa companyId directamente desde el ViewModel
    val empresaId by viewModel.companyId.collectAsState(initial = null)
    val empresaName by viewModel.companyName.collectAsState(initial = null)

    Box(
        modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio disponible
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Login",
                style = MaterialTheme.typography.headlineSmall, // En lugar de h6
                textAlign = TextAlign.Center
            )
            Text(
                text = empresaName ?: "-",
                style = MaterialTheme.typography.headlineSmall, // En lugar de h6
                textAlign = TextAlign.Center
            )

            // Selector de Fundo
            Text("Seleccione el Fundo:")
            LazyColumn(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(fundus) { fundo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedFundo == fundo,
                            onClick = { selectedFundo = fundo }
                        )
                        Text(fundo.nombre)
                    }
                }
            }

            // Campo para el DNI
            TextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text("Ingrese su DNI") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Botón Ingresar


            Button(
                onClick = {
                    viewModel.validateUser(dni, selectedFundo?.id)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Sincronizar
            Button(
                onClick = {
                    empresaId?.let { id: Int -> // Asegurarse de que id no es nulo antes de llamar a syncData
                        viewModel.syncData(id)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sincronizar")
            }

            // Estado de sincronización
            when (syncState) {
                is SyncState.Loading -> {
                    CircularProgressIndicator()
                    Text("Sincronizando datos...")
                }

                is SyncState.Error -> {
                    Text("Error al sincronizar datos", color = MaterialTheme.colorScheme.error)
                }

                is SyncState.Success -> {
                    Text("Datos sincronizados exitosamente")
//                viewModel.loadFundos() // Cargar los fundos después de la sincronización
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estado de validación
            when (validationState) {
                is ValidationState.Loading -> {
                    CircularProgressIndicator()
                    Text("Validando usuario...")
                }

                is ValidationState.Valid -> {
                    // Navegar a HomeScreen cuando el usuario es válido
                    LaunchedEffect(Unit) {
                        onNavigateToHome() // Llama a la función que navega a HomeScreen
                    }
                }

                is ValidationState.Invalid -> {
                    Text(
                        "Usuario no válido. Verifique el DNI y el fundo seleccionado.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }
        }
    }
}
