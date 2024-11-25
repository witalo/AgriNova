package com.example.agrinova.ui.login.second

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.agrinova.R
import com.example.agrinova.ui.home.evaluations.UploadState
import com.example.agrinova.ui.login.loading.LoadingDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondLoginScreen(
    viewModel: SecondLoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit  // Cambiado para recibir solo la función de navegación
) {
    val context = LocalContext.current
    // Define el estado del snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    // Control de coroutine para mostrar el snackbar
    val coroutineScope = rememberCoroutineScope()

    val fundos by viewModel.fundos.collectAsState() // Lista de fundos
    val validationState by viewModel.validationState.collectAsState() // Estado de validación
    val syncState by viewModel.syncState.collectAsState() // Estado de sincronización

    var dni by remember { mutableStateOf("") }
    var selectedFundo by remember { mutableStateOf<FundoDomainModel?>(null) }

//    var selectedModuleValue by remember { mutableStateOf("Seleccionar Módulo") }
//    var isModuleDropdownOpen by remember { mutableStateOf(false) }
    var selectedModulo by remember { mutableStateOf<ModuloDomainModel?>(null) }
    val modules = remember {
        listOf(
            ModuloDomainModel(1, "Sanidad"),
            ModuloDomainModel(2, "Calidad"),
            ModuloDomainModel(3, "Fertirriego")
        )
    }


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

            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_verde),
                contentDescription = "Imagen de login",
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = empresaName ?: "-",
                style = MaterialTheme.typography.headlineSmall, // En lugar de h6
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Modulo
//            StyledSpinner(
//                label = "Módulo",
//                value = selectedModuleValue,
//                isDropdownOpen = isModuleDropdownOpen,
//                onDropdownToggle = { isModuleDropdownOpen = it },
//                onValueChange = { value ->
//                    selectedModuleValue = value // Aquí se guarda el valor seleccionado
//                },
//                items = listOf("SANIDAD", "FERTIRRIEGO", "CALIDAD")
//            )
            // Selector de Fundos
            GenericSelector(
                items = fundos,
                selectedItem = selectedFundo,
                onItemSelected = { selectedFundo = it },
                getDisplayText = { it.nombre },
                label = "Fundos"
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Selector de Módulos
            GenericSelector(
                items = modules,
                selectedItem = selectedModulo,
                onItemSelected = { selectedModulo = it },
                getDisplayText = { it.nombre },
                label = "Módulos"
            )
//            FundoComboBox(
//                label = "Fundo",
//                fundos = fundos,
//                onFundoSelected = { fundo ->
//                    selectedFundo = fundo
////                    println("Selected fundo: ${fundo.id} - ${fundo.nombre}")
//                }
//            )

            // Mostrar información adicional del fundo seleccionado si es necesario
//            selectedFundo?.let { fundo ->
//                Spacer(modifier = Modifier.height(16.dp))
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceVariant
//                    )
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text(
//                            text = "Fundo seleccionado:",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Text(
//                            text = "Nombre: ${fundo.nombre}",
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        Text(
//                            text = "ID: ${fundo.id}",
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
//                }
//            }
            Spacer(modifier = Modifier.height(10.dp))

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
                    val moduleId = selectedModulo?.id

                    if (fundoId != null && dniValue.isNotBlank() && moduleId != null) {
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
                        Toast.makeText(
                            context,
                            "Data sincronizada",
                            Toast.LENGTH_LONG
                        ).show()
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
                        text = "Usuario no válido. Verifique los datos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall, // Texto más pequeño
                        textAlign = TextAlign.Center // Centra el texto
                    )
                }

                else -> {}
            }
        }
    }
    }
        // Mostrar el LoadingDialog cuando estamos sincronizando
    if (syncState is SyncState.Loading) {
        LoadingDialog(
            message = "Sincronizando datos de la empresa\nEsto puede tomar unos momentos..."
        )
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericSelector(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    getDisplayText: (T) -> String,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItem?.let { getDisplayText(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(getDisplayText(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
data class ModuloDomainModel(
    val id: Int,
    val nombre: String
)
