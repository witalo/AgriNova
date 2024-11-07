package com.example.agrinova.ui.login.second

import android.util.Log
import android.widget.Spinner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agrinova.di.models.FundoDomainModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondLoginScreen(
    viewModel: SecondLoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit  // Cambiado para recibir solo la función de navegación
) {
    val fundos by viewModel.fundos.collectAsState() // Lista de fundos
    val validationState by viewModel.validationState.collectAsState() // Estado de validación
    val syncState by viewModel.syncState.collectAsState() // Estado de sincronización

    var dni by remember { mutableStateOf("") }
    var selectedFundo by remember { mutableStateOf<FundoDomainModel?>(null) }

    var selectedModuleValue by remember { mutableStateOf("Seleccionar Módulo") }
    var isModuleDropdownOpen by remember { mutableStateOf(false) }


    // Observa companyId directamente desde el ViewModel
    val empresaId by viewModel.companyId.collectAsState(initial = null)
    val empresaName by viewModel.companyName.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
        modifier = Modifier.widthIn(max = 300.dp) // Establece un ancho máximo para el contenido
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

            // Selector de Modulo
            StyledSpinner(
                label = "Módulo",
                value = selectedModuleValue,
                isDropdownOpen = isModuleDropdownOpen,
                onDropdownToggle = { isModuleDropdownOpen = it },
                onValueChange = { value ->
                    selectedModuleValue = value // Aquí se guarda el valor seleccionado
                },
                items = listOf("SANIDAD", "FERTIRRIEGO", "CALIDAD")
            )

            // Muestra el valor seleccionado
//            Text(text = "Módulo seleccionado: $selectedModuleValue")
            Spacer(modifier = Modifier.height(8.dp))
            // Selector de Fundo
//            Spinner(
//                label = "Seleccionar Fundo",
//                value = selectedFundo?.nombre ?: "Seleccionar Fundo",
//                isDropdownOpen = isFundoDropdownOpen,
//                onDropdownToggle = { isFundoDropdownOpen = it },
//                onValueChange = { value ->
//                    selectedFundo = fundos.find { it.nombre == value }
//                    isFundoDropdownOpen = false
//                    // Hacer algo con el fundo seleccionado
//                },
//                items = fundos.map { it.nombre }
//            )
            // Selector de Fundo
            if (fundos.isNotEmpty()) {
                AutocompleteTextField(
                    suggestions = fundos.map { it.nombre },  // Asegúrate de que 'nombre' esté en tu modelo de datos
                    onSuggestionClick = { selectedFundoNombre ->
                        // Buscar el fundo completo por nombre
                        selectedFundo = fundos.firstOrNull { it.nombre == selectedFundoNombre }
                    }
                )
            } else {
                Text("No se encontraron fundos")
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Campo para el DNI
            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text("Ingrese su DNI") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
//            TextField(
//                value = dni,
//                onValueChange = { dni = it },
//                label = { Text("Ingrese su DNI") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier
//                    .fillMaxWidth() // Ocupa todo el ancho disponible
//                    .padding(start = 32.dp, end = 32.dp) // Padding a los lados
//                    .border(1.dp, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)), // Borde delgado y más sutil
//                textStyle = TextStyle(
//                    color = MaterialTheme.colorScheme.onSurface, // Color del texto
//                    fontSize = 16.sp // Tamaño de la fuente
//                ),
//                colors = TextFieldDefaults.textFieldColors(
//                    containerColor = Color.White, // Fondo blanco
//                    focusedIndicatorColor = Color.Transparent, // Sin borde inferior cuando está enfocado
//                    unfocusedIndicatorColor = Color.Transparent, // Sin borde inferior cuando no está enfocado
//                    cursorColor = MaterialTheme.colorScheme.primary, // Color del cursor
//                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) // Color del indicador cuando está deshabilitado
//                ),
//            )




            Spacer(modifier = Modifier.height(6.dp))
            // Botón Ingresar
            Button(
                onClick = {
                    // Verifica que el fundo esté seleccionado antes de llamar a la función
                    selectedFundo?.id?.let { id ->
                        viewModel.validateUser(dni, id)
                    }
                },
                modifier = Modifier.widthIn(min = 150.dp, max = 250.dp).padding(bottom = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón Sincronizar
            Button(
                onClick = {
                    empresaId?.let { id: Int -> // Asegurarse de que id no es nulo antes de llamar a syncData
                        viewModel.syncData(id)
                    }
                },
                modifier = Modifier.widthIn(min = 150.dp, max = 250.dp).padding(bottom = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
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
}
@Composable
fun SimpleComboBox(
    label: String,
    suggestions: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(start = 32.dp, end = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .clickable { isDropdownExpanded = !isDropdownExpanded }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (selectedValue.isNotBlank()) selectedValue else label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedValue.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Arrow",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    onClick = {
                        onValueSelected(suggestion)
                        isDropdownExpanded = false
                    },
                    text = { Text(text = suggestion) }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteTextField(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isDropdownExpanded = true } // Abre el menú desplegable al hacer clic
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Componente TextField con propiedades de Material3 y sin permitir la edición
            TextField(
                value = text,
                onValueChange = { /* No hace nada para deshabilitar entrada de texto */ },
                enabled = false,  // Deshabilita la entrada de texto
                label = { Text("Fundo") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        modifier = Modifier.clickable {
                            isDropdownExpanded = !isDropdownExpanded
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,  // Fondo blanco completamente opaco
                    cursorColor = MaterialTheme.colorScheme.primary, // Color del cursor
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Color del texto cuando está deshabilitado
                    focusedLabelColor = MaterialTheme.colorScheme.primary, // Color de la etiqueta cuando está enfocado
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Color de la etiqueta cuando no está enfocado
                ),
                shape = RoundedCornerShape(8.dp) // Asegura que el borde esté redondeado
            )
        }

        // Menú desplegable que muestra las opciones
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    onClick = {
                        text = TextFieldValue(suggestion)
                        onSuggestionClick(suggestion)
                        isDropdownExpanded = false
                    },
                    text = { Text(text = suggestion) }
                )
            }
        }
    }
}

@Composable
fun StyledSpinner(
    label: String,
    value: String,
    isDropdownOpen: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    items: List<String>
) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onDropdownToggle(!isDropdownOpen) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (value.isNotBlank()) value else "Seleccionar...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (isDropdownOpen) {
            Dialog(
                onDismissRequest = { onDropdownToggle(false) }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .padding(top = 40.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(3.dp), //padding trasparente que rodea al desplegable
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        items.forEach { item ->
                            Text(
                                text = item,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueChange(item) // Cambia el valor seleccionado
                                        onDropdownToggle(false) // Cierra el dropdown
                                    }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun AutocompleteTextField(
//    suggestions: List<String>,
//    onSuggestionClick: (String) -> Unit
//) {
//    var text by remember { mutableStateOf(TextFieldValue("")) }
//    var filteredSuggestions by remember { mutableStateOf(suggestions) }
//    var isDropdownExpanded by remember { mutableStateOf(false) }
//
//    Column (
//        modifier = Modifier
//            .padding(start = 32.dp, end = 32.dp) // Agrega padding para reducir el ancho
//    ){
//        TextField(
//            value = text,
//            onValueChange = { newText ->
//                text = newText
//                filteredSuggestions = suggestions.filter {
//                    it.contains(newText.text, ignoreCase = true)
//                }
//                isDropdownExpanded = filteredSuggestions.isNotEmpty()
//            },
//            label = { Text("Fundo") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .onFocusChanged { focusState ->
//                    isDropdownExpanded = focusState.isFocused && filteredSuggestions.isNotEmpty()
//                }
//        )
//
//        DropdownMenu(
//            expanded = isDropdownExpanded,
//            onDismissRequest = { isDropdownExpanded = false }
//        ) {
//            filteredSuggestions.forEach { suggestion ->
//                DropdownMenuItem(
//                    onClick = {
//                        text = TextFieldValue(suggestion)
//                        onSuggestionClick(suggestion)
//                        isDropdownExpanded = false
//                    },
//                    text = { Text(text = suggestion) }
//                )
//            }
//        }
//    }
//}
//@Composable
//fun Spinner(
//    label: String,
//    value: String,
//    isDropdownOpen: Boolean,
//    onDropdownToggle: (Boolean) -> Unit,
//    onValueChange: (String) -> Unit,
//    items: List<String>
//) {
//    Box(
//        modifier = Modifier
////            .padding(horizontal = 2.dp)
//            .width(250.dp)  // Ajusta el ancho aquí, o usa fillMaxWidth() si quieres que se ajuste al espacio disponible
//            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
//            .clickable { onDropdownToggle(!isDropdownOpen) }
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//    ) {
//        Column {
//            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = if (value.isNotBlank()) value else "Seleccionar...",
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = if (value.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray
//                )
//                Spacer(modifier = Modifier.weight(1f))
//                Icon(
//                    imageVector = Icons.Default.ArrowDropDown,
//                    contentDescription = "Dropdown Arrow",
//                    tint = MaterialTheme.colorScheme.onSurface
//                )
//            }
//        }
//
//        if (isDropdownOpen) {
//            Dialog(
//                onDismissRequest = { onDropdownToggle(false) }
//            ) {
//                Surface(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 40.dp)
//                        .padding(top = 55.dp)  // Ajusta la posición vertical del dropdown
//                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
//                        .padding(16.dp),
//                    shadowElevation = 4.dp,
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Column (
//                        modifier = Modifier.padding(horizontal = 10.dp)  // Espaciado interno en la lista
//                    ){
//                        items.forEach { item ->
//                            Text(
//                                text = item,
//                                style = MaterialTheme.typography.bodyMedium,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        onValueChange(item)
//                                        onDropdownToggle(false)
//                                    }
//                                    .padding(vertical = 8.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
