package com.example.agrinova.ui.home.evaluations

import android.util.Log
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

@Composable
fun NewEvaluationScreen(
    navController: NavController,
    cartillaId: String,
    viewModel: NewEvaluationViewModel = hiltViewModel(),
) {
    val saveStatus by viewModel.saveStatus.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
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
            viewModel = viewModel, isChecked = isChecked, onCheckedChange = { isChecked = it })

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvaluationHeader(
    modifier: Modifier = Modifier,
    viewModel: NewEvaluationViewModel,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val lotes by viewModel.lotes.collectAsState()
    var selectedLote by remember { mutableStateOf<LoteModuloDto?>(null) }

    val valvulas by viewModel.valvulas.collectAsState()
    var selectedValvula by remember { mutableStateOf<ValvulaDomainModel?>(null) }
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
            ExposedDropdownMenuBox(modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp, horizontal = 8.dp),
                expanded = viewModel.isCombo1Expanded.value,
                onExpandedChange = { viewModel.isCombo1Expanded.value = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
//                    value = viewModel.selectedCombo1.value?.name ?: "",
                    value = if (selectedLote?.loteCodigo != null && selectedLote?.moduloCodigo != null) {
                        "${selectedLote?.loteCodigo}:${selectedLote?.moduloCodigo}"
                    } else {
                        ""
                    },
                    onValueChange = {},
                    label = { Text("Lote") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isCombo1Expanded.value) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(expanded = viewModel.isCombo1Expanded.value,
                    onDismissRequest = { viewModel.isCombo1Expanded.value = false }) {
//                    viewModel.combo1Items.forEach { item ->
                    lotes.forEach { lote ->
                        DropdownMenuItem(text = { Text("${lote.loteCodigo}:${lote.moduloCodigo}") },
//                            onClick = {
//                                viewModel.onCombo1Selected(item)
//                                viewModel.isCombo1Expanded.value = false
//                            }
                            onClick = {
                                selectedLote = lote // Actuasliza el lote seleccionado
                                viewModel.onLoteSelected(lote) // Llama a la función del ViewModel si necesitas lógica adicional
                                viewModel.isCombo1Expanded.value = false // Cierra el menú
                            })
                    }
                }
            }

            // Segundo combo
            ExposedDropdownMenuBox(modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp, horizontal = 8.dp),
                expanded = viewModel.isCombo2Expanded.value,
                onExpandedChange = { viewModel.isCombo2Expanded.value = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = if (selectedValvula != null) {
                        "${selectedValvula?.codigo}:${selectedValvula?.nombre}"
                    } else {
                        ""
                    },
                    onValueChange = {},
                    label = { Text("Valvula") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isCombo2Expanded.value) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(expanded = viewModel.isCombo2Expanded.value,
                    onDismissRequest = { viewModel.isCombo2Expanded.value = false }) {
                    valvulas.forEach { valvula ->
                        DropdownMenuItem(text = { Text("${valvula.codigo}:${valvula.nombre}") },
                            onClick = {
                                selectedValvula = valvula
                                viewModel.onValvulaSelected(valvula)
                                viewModel.isCombo2Expanded.value = false
                            })
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
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF43BD28)
                    )
                )
                Text(
                    text = "GPS Automatico", modifier = Modifier.padding(start = 8.dp)
                )

            }
            // Botón con ícono
            IconButton(
                onClick = { viewModel.saveEvaluationDato() },
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Guardar",
                    modifier = Modifier.size(35.dp),
                    tint = Color(0xFF43BD28)
                )
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
            .padding(horizontal = 6.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabecera del grupo (siempre visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(16.dp),
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
                    contentDescription = if (isExpanded) "Contraer" else "Expandir"
                )
            }

            // Contenido expandible (variables)
            if (isExpanded) {
                variables.forEach { variable ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                // Validación: Aceptar solo valores decimales o un campo vacío
                                if (value.isEmpty() || value.toDoubleOrNull() != null) {
                                    onVariableValueChange(variable.id, value)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            label = { Text(text = "Valor", fontSize = 12.sp, color = Color(0xFF2A69D5)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal // Teclado de números con punto decimal
                            ),
                            textStyle = TextStyle(
//                                background = Color(0xFFFFFFFF), // Fondo del texto (color azul claro)
                                color = Color(0xFF2A69D5), // Color del texto azul (tema primario)
                                textAlign = TextAlign.End, // Texto alineado a la derecha
                                fontSize = 18.sp // Aumentar el tamaño del texto
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color(0xFFFFFFFF), // Fondo del cuadro de texto (rosa claro)
                                focusedBorderColor = Color(0xFF2970EA), // Borde azul al enfocarse
                                unfocusedBorderColor = Color.Gray, // Borde gris sin enfoque
                                errorBorderColor = Color.Red // Borde rojo en caso de error
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(30.dp),
                            isError = variableValues[variable.id]?.toDoubleOrNull() == null && variableValues[variable.id]?.isNotEmpty() == true
                        )
//                        OutlinedTextField(
//                            value = variableValues[variable.id] ?: "",
//                            onValueChange = { value ->
//                                // Validación según el tipo
//                                val isValid = when {
//                                    variable.tipo.equals("decimal", ignoreCase = true) ->
//                                        value.isEmpty() || value.toDoubleOrNull() != null
//
//                                    variable.tipo.equals("entero", ignoreCase = true) ->
//                                        value.isEmpty() || value.toIntOrNull() != null
//
//                                    else -> true
//                                }
//
//                                if (isValid) {
//                                    onVariableValueChange(variable.id, value)
//                                }
//                            },
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(start = 8.dp),
//                            label = { Text(text = "Valor") },
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = when {
//                                    variable.tipo.equals("decimal", ignoreCase = true) ->
//                                        KeyboardType.Decimal
//
//                                    variable.tipo.equals("entero", ignoreCase = true) ->
//                                        KeyboardType.Number
//
//                                    else -> KeyboardType.Text
//                                }
//                            ),
//                            singleLine = true,
//                            isError = when {
//                                variable.tipo.equals("decimal", ignoreCase = true) ->
//                                    variableValues[variable.id]?.toDoubleOrNull() == null && variableValues[variable.id]?.isNotEmpty() == true
//
//                                variable.tipo.equals("entero", ignoreCase = true) ->
//                                    variableValues[variable.id]?.toIntOrNull() == null && variableValues[variable.id]?.isNotEmpty() == true
//
//                                else -> false
//                            }
//                        )
                    }
                }
            }
        }
    }
}
