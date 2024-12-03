package com.example.agrinova.ui.home.evaluations

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.di.models.GrupoVariableDomainModel
import com.example.agrinova.di.models.ValvulaDomainModel
import com.example.agrinova.di.models.VariableGrupoDomainModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import com.example.agrinova.data.dto.LocationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import android.provider.Settings
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewEvaluationScreen(
    navController: NavController,
    cartillaId: String,
    viewModel: NewEvaluationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isGpsEnabled by viewModel.isGpsEnabled.collectAsState()
    // Create permission launcher at this level
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            viewModel.onGpsCheckboxChanged(true)
        } else {
            Toast.makeText(context, "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }
    // Usar rememberSaveable para mantener el estado a través de recreaciones
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    // Otros estados ya gestionados por el ViewModel
    val saveStatus by viewModel.saveStatus.collectAsState()

    // Evitar reiniciar el estado en cada rotación
    LaunchedEffect(saveStatus) {
        saveStatus?.fold(
            onSuccess = {
                showSuccessDialog = true
            },
            onFailure = { error ->
                errorMessage = error.message ?: "Error desconocido"
                showErrorDialog = true
            }
        )
    }
    // Método para cambiar el estado del GPS
    fun handleGpsToggle(checked: Boolean) {
        if (checked) {
            // Solicitar permisos si no están concedidos
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Lanzar solicitud de permisos
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                // Permisos ya concedidos, activar GPS
                viewModel.onGpsCheckboxChanged(true)
            }
        } else {
            // Desactivar GPS
            viewModel.onGpsCheckboxChanged(false)
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cabecera con filtros
        EvaluationHeader(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
//                .padding(0.dp),
            viewModel = viewModel,
            isChecked = isGpsEnabled,
            onCheckedChange = { handleGpsToggle(it) }
        )

        // Cuerpo scrollable con grupos y variables
        EvaluationBody(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewModel = viewModel,
            onSaveClick = { viewModel.saveEvaluationDato() }
        )
        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    navController.popBackStack()
                },
                title = { Text("Éxito") },
                text = { Text("Los datos se guardaron correctamente") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }
        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvaluationHeader(
    modifier: Modifier = Modifier,
    viewModel: NewEvaluationViewModel,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationGranted) {
            // Check if GPS is actually enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onCheckedChange(true)
            } else {
                // Prompt user to enable GPS
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            Toast.makeText(context, "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }
    val lotes by viewModel.lotes.collectAsState()
    val selectedLote = viewModel.selectedLote.collectAsState()

    val valvulas by viewModel.valvulas.collectAsState()
    val selectedValvula = viewModel.selectedValvula.collectAsState()


    Column(
        modifier = modifier.shadow(elevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Primer combo
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                expanded = viewModel.isCombo1Expanded.value,
                onExpandedChange = { viewModel.isCombo1Expanded.value = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(0.dp)
                    ,
                    readOnly = true,
                    value = selectedLote.value?.let { lote ->
                        "${lote.moduloCodigo}:${lote.loteCodigo}"
                    } ?: "",
                    onValueChange = {},
                    label = { Text("Modulo:Lote",  style = TextStyle(fontSize = 12.sp)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isCombo1Expanded.value)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                )

                ExposedDropdownMenu(
                    expanded = viewModel.isCombo1Expanded.value,
                    onDismissRequest = { viewModel.isCombo1Expanded.value = false }
                ) {
                    lotes.forEach { lote ->
                        DropdownMenuItem(
                            text = { Text("${lote.moduloCodigo}:${lote.loteCodigo}") },
                            onClick = {
                                viewModel.onLoteSelected(lote) // Actualiza el lote seleccionado en el ViewModel
                                viewModel.isCombo1Expanded.value = false // Cierra el menú
                            }
                        )
                    }
                }
            }

            // Segundo combo
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                expanded = viewModel.isCombo2Expanded.value,
                onExpandedChange = { viewModel.isCombo2Expanded.value = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = selectedValvula.value?.codigo ?: "",
                    onValueChange = {},
                    label = { Text("Valvula", style = TextStyle(fontSize = 12.sp)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isCombo2Expanded.value)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )

                ExposedDropdownMenu(
                    expanded = viewModel.isCombo2Expanded.value,
                    onDismissRequest = { viewModel.isCombo2Expanded.value = false }
                ) {
                    valvulas.forEach { valvula ->
                        DropdownMenuItem(
                            text = { Text(valvula.codigo) },
                            onClick = {
                                viewModel.onValvulaSelected(valvula) // Actualiza la válvula seleccionada en el ViewModel
                                viewModel.isCombo2Expanded.value = false // Cierra el menú
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // Ajusta el espacio vertical
            verticalAlignment = Alignment.CenterVertically // Centra verticalmente el contenido dentro de la fila
        ) {
            // Primera columna: Checkbox y texto
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp), // Espaciado lateral
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { checked ->
                        if (checked) {
                            // First, check permissions
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    // Check if GPS is enabled
                                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        onCheckedChange(true)
                                    } else {
                                        // Redirect to GPS settings
                                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                }
                                activity?.shouldShowRequestPermissionRationale(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == true -> {
                                    Toast.makeText(
                                        context,
                                        "Se necesita acceso a la ubicación para esta función",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        } else {
                            onCheckedChange(false)
                        }
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF43BD28))
                )
                Text(
                    text = "Activar(GPS)",
                    modifier = Modifier.padding(start = 1.dp) // Espaciado entre checkbox y texto
                )
            }

            // Segunda columna: Botón centrado
            Box(
                modifier = Modifier
                    .weight(1f), // Distribución igual con la primera columna
                contentAlignment = Alignment.Center // Centra el botón horizontal y verticalmente
            ) {
                IconButton(
                    onClick = { viewModel.saveEvaluationDato() },
                    modifier = Modifier
                        .size(45.dp) // Tamaño del botón
                        .clip(CircleShape) // Forma circular
                        .background(Color(0xFF1976D2)) // Fondo personalizado (azul intenso)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFD6ECFD), // Color del borde (azul claro)
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(30.dp), // Tamaño del ícono
                        tint = Color(0xFFFFFFFF) // Color del ícono (blanco)
                    )
                }
            }
        }


    }
}

@Composable
private fun EvaluationBody(
    modifier: Modifier = Modifier,
    viewModel: NewEvaluationViewModel,
    onSaveClick: () -> Unit
) {
    val grupos by viewModel.grupos.collectAsState()
    val variables by viewModel.variables.collectAsState()
    val expandedGroups by viewModel.expandedGroups.collectAsState()
    val variableValues by viewModel.variableValues.collectAsState()
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        items(grupos) { grupo ->
            GrupoVariableCard(
                grupo = grupo,
                isExpanded = expandedGroups.contains(grupo.id),
                variables = variables.filter { it.grupoVariableId == grupo.id },
                variableValues = variableValues.mapValues { it.value.first },
                viewModel = viewModel, // Add viewModel
                onExpandClick = { viewModel.toggleGroupExpansion(grupo.id) },
                onVariableValueChange = { variableId, value, location ->
                    viewModel.updateVariableValue(variableId, value, location)
                }
            )
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GrupoVariableCard(
    grupo: GrupoVariableDomainModel,
    isExpanded: Boolean,
    variables: List<VariableGrupoDomainModel>,
    variableValues: Map<Int, String>,
    onExpandClick: () -> Unit,
    onVariableValueChange: (Int, String, LocationModel?) -> Unit,
    viewModel: NewEvaluationViewModel // Add ViewModel parameter
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp), // Más compacto
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface, // Fondo adaptable
            contentColor = MaterialTheme.colorScheme.onSurface // Texto adaptable
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabecera del grupo (siempre visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(vertical = 8.dp, horizontal = 12.dp), // Compactación
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = grupo.grupoNombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = if (isExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.primary // Ícono del color primario
                )
            }

            // Contenido expandible (variables)
            if (isExpanded) {
                variables.forEach { variable ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp), // Más compacto
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = variable.variableEvaluacionNombre,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = variableValues[variable.id] ?: "",
                            onValueChange = { value ->
                                // Immediately update the value with an initial invalid location
                                onVariableValueChange(
                                    variable.id,
                                    value,
                                    LocationModel() // Default invalid location
                                )

                                // Only capture location if:
                                // 1. GPS checkbox is checked
                                // 2. Value is numeric or empty
                                if (viewModel.isGpsEnabled.value &&
                                    (value.isEmpty() || value.toDoubleOrNull() != null)) {

                                    viewModel.captureLocationAsync(context, variable.id) { locationModel ->
                                        // Update with the captured location
                                        onVariableValueChange(
                                            variable.id,
                                            value,
                                            locationModel
                                        )
                                    }
                                } else {
                                    // If GPS is not enabled, use (0,0) location
                                    onVariableValueChange(
                                        variable.id,
                                        value,
                                        LocationModel(0.0, 0.0)
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            label = {
                                Text(
                                    text = "Valor",
                                    style = MaterialTheme.typography.labelSmall.copy( // Etiqueta compacta
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy( // Texto principal
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.End
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
//                                textColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp), // Bordes más suaves
                            isError = variableValues[variable.id]?.toDoubleOrNull() == null &&
                                    variableValues[variable.id]?.isNotEmpty() == true
                        )
                    }
                }
            }
        }
    }
}