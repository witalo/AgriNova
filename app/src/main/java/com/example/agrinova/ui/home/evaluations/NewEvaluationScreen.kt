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
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewEvaluationScreen(
    navController: NavController,
    cartillaId: String,
    viewModel: NewEvaluationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isGpsEnabled by viewModel.isGpsEnabled.collectAsState()
    val locationData by viewModel.locationData.collectAsState()
    // Show location when captured
    locationData?.let { location ->
        Toast.makeText(
            context,
            "Lat: ${location.latitude}, Lon: ${location.longitude}",
            Toast.LENGTH_LONG
        ).show()
    }

    val saveStatus by viewModel.saveStatus.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    // Monitor save status
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
            onCheckedChange = {
                viewModel.onGpsCheckboxChanged(it)
            })

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
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            onCheckedChange(true)
        } else {
            Toast.makeText(context, "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }
    // Check and request location permissions if not granted
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            onCheckedChange(true)
//        } else {
//            // Handle permission denial
//            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
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
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Espaciado entre los elementos
        ) {

            // Checkbox
            Row(
                modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { checked ->
                        if (checked) {
                            // Verificar y solicitar permisos
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    // Ya tenemos permisos, proceder
                                    onCheckedChange(true)
                                }

                                activity?.shouldShowRequestPermissionRationale(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == true -> {
                                    // Mostrar explicación de por qué necesitamos el permiso
                                    Toast.makeText(
                                        context,
                                        "Se necesita acceso a la ubicación para esta función",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                else -> {
                                    // Solicitar permisos
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
                        // Check permissions before changing state
//                        if (ContextCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.ACCESS_FINE_LOCATION
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            // Request permission
//                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                        } else {
//                            // Permissions already granted, proceed
//                            onCheckedChange(it)
//                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF43BD28)
                    )
                )
                Text(
                    text = "GPS Automático",
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
            // Botón más moderno con animación
            IconButton(
                onClick = { viewModel.saveEvaluationDato() },
                modifier = Modifier
                    .size(56.dp) // Tamaño del botón
                    .padding(5.dp) // Margen interior
                    .clip(CircleShape) // Forma circular
                    .background(Color(0xFF1976D2)) // Fondo personalizado (azul intenso)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFBBDEFB), // Color de borde (azul claro)
                        shape = CircleShape
                    )
                    .animateContentSize() // Animación fluida
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Guardar",
                    modifier = Modifier.size(32.dp), // Tamaño del icono
                    tint = Color(0xFFFFFFFF) // Color del icono (blanco)
                )
            }

//            IconButton(
//                onClick = { viewModel.saveEvaluationDato() },
//                modifier = Modifier
//                    .size(48.dp)
//                    .padding(end = 8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.CheckCircle,
//                    contentDescription = "Guardar",
//                    modifier = Modifier.size(35.dp),
//                    tint = Color(0xFF43BD28)
//                )
//            }

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
                variableValues = variableValues,
                onExpandClick = { viewModel.toggleGroupExpansion(grupo.id) },
                onVariableValueChange = { variableId, value ->
                    viewModel.updateVariableValue(variableId, value)
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
    onVariableValueChange: (Int, String) -> Unit
) {
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
                                if (value.isEmpty() || value.toDoubleOrNull() != null) {
                                    onVariableValueChange(variable.id, value)
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun GrupoVariableCard(
//    grupo: GrupoVariableDomainModel,
//    isExpanded: Boolean,
//    variables: List<VariableGrupoDomainModel>,
//    variableValues: Map<Int, String>,
//    onExpandClick: () -> Unit,
//    onVariableValueChange: (Int, String) -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 6.dp, vertical = 3.dp),
////        colors = CardDefaults.cardColors(
////            containerColor = Color(0xFFFFFFFF)
////        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(modifier = Modifier.fillMaxWidth()) {
//            // Cabecera del grupo (siempre visible)
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onExpandClick() }
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = grupo.grupoNombre,
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Icon(
//                    imageVector = if (isExpanded) {
//                        Icons.Default.KeyboardArrowUp
//                    } else {
//                        Icons.Default.KeyboardArrowDown
//                    },
//                    contentDescription = if (isExpanded) "Contraer" else "Expandir"
//                )
//            }
//
//            // Contenido expandible (variables)
//            if (isExpanded) {
//                variables.forEach { variable ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp, vertical = 8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = variable.variableEvaluacionNombre,
//                            modifier = Modifier.weight(1f),
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                        OutlinedTextField(
//                            value = variableValues[variable.id] ?: "",
//                            onValueChange = { value ->
//                                // Validación: Aceptar solo valores decimales o un campo vacío
//                                if (value.isEmpty() || value.toDoubleOrNull() != null) {
//                                    onVariableValueChange(variable.id, value)
//                                }
//                            },
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(horizontal = 4.dp, vertical = 4.dp),
//                            label = {
//                                Text(
//                                    text = "Valor",
//                                    fontSize = 12.sp,
//                                    color = Color(0xFF2A69D5)
//                                )
//                            },
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Decimal // Teclado de números con punto decimal
//                            ),
//                            textStyle = TextStyle(
////                                background = Color(0xFFFFFFFF), // Fondo del texto (color azul claro)
//                                color = Color(0xFF2A69D5), // Color del texto azul (tema primario)
//                                textAlign = TextAlign.End, // Texto alineado a la derecha
//                                fontSize = 18.sp // Aumentar el tamaño del texto
//                            ),
//                            colors = TextFieldDefaults.outlinedTextFieldColors(
////                                containerColor = Color(0xFFFFFFFF), // Fondo del cuadro de texto (rosa claro)
//                                focusedBorderColor = Color(0xFF2970EA), // Borde azul al enfocarse
//                                unfocusedBorderColor = Color.Gray, // Borde gris sin enfoque
//                                errorBorderColor = Color.Red // Borde rojo en caso de error
//                            ),
//                            singleLine = true,
//                            shape = RoundedCornerShape(30.dp),
//                            isError = variableValues[variable.id]?.toDoubleOrNull() == null && variableValues[variable.id]?.isNotEmpty() == true
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
