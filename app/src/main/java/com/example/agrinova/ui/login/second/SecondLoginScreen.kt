package com.example.agrinova.ui.login.second

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
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
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondLoginScreen(
    viewModel: SecondLoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit  // Cambiado para recibir solo la función de navegación
) {
    // Define el estado del snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    // Control de coroutine para mostrar el snackbar
    val coroutineScope = rememberCoroutineScope()

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
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = empresaName ?: "-",
                style = MaterialTheme.typography.headlineSmall, // En lugar de h6
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

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
//                AutocompleteTextField(
//                    suggestions = fundos.map { it.nombre },  // Asegúrate de que 'nombre' esté en tu modelo de datos
//                    onSuggestionClick = { selectedFundoNombre ->
//                        // Buscar el fundo completo por nombre
//                        selectedFundo = fundos.firstOrNull { it.nombre == selectedFundoNombre }
//                    }
//                )
            } else {
                Text("No se encontraron fundos")
            }
            FundoComboBox(
                label = "Fundo",
                fundos = fundos,
                onFundoSelected = { fundo ->
                    selectedFundo = fundo
//                    println("Selected fundo: ${fundo.id} - ${fundo.nombre}")
                }
            )
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
                    val fundoId = selectedFundo?.id
                    val dniValue = dni
                    val moduleId = selectedModuleValue

                    if (fundoId != null && dniValue.isNotBlank() && moduleId != "Seleccionar Módulo") {
                        // Llama a la función si todos los valores están presentes
                        viewModel.validateUser(dniValue, fundoId, moduleId)
                    } else {
                        // Muestra el Snackbar si algún campo está incompleto
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Por favor, completa todos los campos",
                                duration = SnackbarDuration.Short // Aquí puedes cambiar a Long o Indefinite
                            )
                        }
                    }
//                    selectedFundo?.id?.let { id ->
//                        viewModel.validateUser(dni, id, selectedModuleValue)
//                    }
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
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AutocompleteTextField(
//    suggestions: List<String>,
//    onSuggestionClick: (String) -> Unit
//) {
//    var text by remember { mutableStateOf(TextFieldValue("")) }
//    var isDropdownExpanded by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { isDropdownExpanded = true } // Abre el menú desplegable al hacer clic
//                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
//                .padding(horizontal = 16.dp, vertical = 8.dp)
//        ) {
//            // Componente TextField con propiedades de Material3 y sin permitir la edición
//            TextField(
//                value = text,
//                onValueChange = { /* No hace nada para deshabilitar entrada de texto */ },
//                enabled = false,  // Deshabilita la entrada de texto
//                label = { Text("Fundo") },
//                trailingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.ArrowDropDown,
//                        contentDescription = "Dropdown Arrow",
//                        modifier = Modifier.clickable {
//                            isDropdownExpanded = !isDropdownExpanded
//                        }
//                    )
//                },
//                modifier = Modifier.fillMaxWidth(),
//                colors = TextFieldDefaults.textFieldColors(
//                    containerColor = Color.White,  // Fondo blanco completamente opaco
//                    cursorColor = MaterialTheme.colorScheme.primary, // Color del cursor
//                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Color del texto cuando está deshabilitado
//                    focusedLabelColor = MaterialTheme.colorScheme.primary, // Color de la etiqueta cuando está enfocado
//                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Color de la etiqueta cuando no está enfocado
//                ),
//                shape = RoundedCornerShape(8.dp) // Asegura que el borde esté redondeado
//            )
//        }
//
//        // Menú desplegable que muestra las opciones
//        DropdownMenu(
//            expanded = isDropdownExpanded,
//            onDismissRequest = { isDropdownExpanded = false }
//        ) {
//            suggestions.forEach { suggestion ->
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
//fun FundoAutocomplete(
//    label: String,
//    fundos: List<FundoDomainModel>,
//    onFundoSelected: (FundoDomainModel) -> Unit
//) {
//    var isOpen by remember { mutableStateOf(false) }
//    var searchTerm by remember { mutableStateOf("") }
//    var isFocused by remember { mutableStateOf(false) }
//
//    // Si el campo de búsqueda está vacío, muestra todos los fundos; de lo contrario, filtra según el término de búsqueda
//    val filteredFundos = if (searchTerm.isEmpty()) fundos else fundos.filter {
//        it.nombre.contains(searchTerm, ignoreCase = true)
//    }
//
//    OutlinedTextField(
//        value = searchTerm,
//        onValueChange = {
//            searchTerm = it
//            isOpen = true
//        },
//        label = { Text(label) },
//        modifier = Modifier
//            .fillMaxWidth()
//            .onFocusChanged { state ->
//                isFocused = state.isFocused
//            },
//        trailingIcon = {
//            Icon(
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = "Toggle Dropdown",
//                modifier = Modifier.clickable {
//                    isOpen = !isOpen // Cambia el estado de isOpen al hacer clic en el icono
//                }
//            )
//        }
//    )
//
//    if (isOpen && isFocused && filteredFundos.isNotEmpty()) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp, bottom = 8.dp)
//        ) {
//            items(filteredFundos) { fundo ->
//                DropdownOption(
//                    text = fundo.nombre,
//                    onClick = {
//                        onFundoSelected(fundo)
//                        searchTerm = fundo.nombre // Muestra el nombre seleccionado en el campo de texto
//                        isOpen = false
//                    }
//                )
//            }
//        }
//    }
//}
//@Composable
//fun DropdownOption(
//    text: String,
//    onClick: () -> Unit
//) {
//    Text(
//        text = text,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp, horizontal = 16.dp)
//            .clickable { onClick() },
//        style = MaterialTheme.typography.bodyMedium
//    )
//}

@Composable
fun FundoComboBox(
    label: String,
    fundos: List<FundoDomainModel>,
    onFundoSelected: (FundoDomainModel) -> Unit
) {
    var isOpen by remember { mutableStateOf(false) }
    var selectedFundo by remember { mutableStateOf<FundoDomainModel?>(null) }

    // Caja para contener todo y evitar que los elementos se empujen
    Box(
        modifier = Modifier.fillMaxWidth() // Para que el combo ocupe todo el ancho disponible
    ) {
        // Campo de selección
        OutlinedTextField(
            value = selectedFundo?.nombre ?: "",
            onValueChange = {}, // Campo solo lectura
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()  // El campo ocupa el 100% del ancho disponible
                .clickable { isOpen = !isOpen },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle Dropdown",
                    modifier = Modifier.clickable { isOpen = !isOpen }
                )
            }
        )

        // Menú desplegable con fondo blanco y ancho ajustado al tamaño del OutlinedTextField
        // Ahora usamos BoxWithConstraints para obtener el ancho del OutlinedTextField y ajustar el Dropdown
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val dropdownWidth = this.maxWidth // Obtiene el ancho del Box

            DropdownMenu(
                expanded = isOpen,
                onDismissRequest = { isOpen = false },
                modifier = Modifier
                    .width(dropdownWidth)  // El ancho del dropdown es igual al del OutlinedTextField
                    .background(Color.White) // Fondo blanco para el dropdown
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(10.dp)) // Borde del dropdown
            ) {
                fundos.forEach { fundo ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fundo.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            selectedFundo = fundo
                            onFundoSelected(fundo)
                            isOpen = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()  // Asegura que el item ocupe todo el ancho disponible
                            .background(Color.White) // Fondo blanco para el item
                            .padding(vertical = 8.dp, horizontal = 16.dp) // Espaciado para los elementos
                    )
                }
            }
        }
    }
}
