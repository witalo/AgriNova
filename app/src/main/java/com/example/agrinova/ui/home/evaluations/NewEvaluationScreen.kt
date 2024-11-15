package com.example.agrinova.ui.home.evaluations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController

@Composable
fun NewEvaluationScreen(
    viewModel: NewEvaluationViewModel = hiltViewModel(),
    navController: NavHostController,
    onNavigate: (String) -> Unit,
) {
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cabecera con filtros
        EvaluationHeader(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
//                .padding(0.dp),
            viewModel = viewModel,
            isChecked = isChecked,
            onCheckedChange = { isChecked = it }
        )

        // Cuerpo scrollable con la lista
        EvaluationBody(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewModel = viewModel
        )
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
    Column(
        modifier = modifier
            .shadow(elevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
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
                        .menuAnchor(),
                    readOnly = true,
                    value = viewModel.selectedCombo1.value?.name ?: "",
                    onValueChange = {},
                    label = { Text("Lote") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isCombo1Expanded.value) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = viewModel.isCombo1Expanded.value,
                    onDismissRequest = { viewModel.isCombo1Expanded.value = false }
                ) {
                    viewModel.combo1Items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                viewModel.onCombo1Selected(item)
                                viewModel.isCombo1Expanded.value = false
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
                    value = viewModel.selectedCombo2.value?.name ?: "",
                    onValueChange = {},
                    label = { Text("Valvula") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isCombo2Expanded.value) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = viewModel.isCombo2Expanded.value,
                    onDismissRequest = { viewModel.isCombo2Expanded.value = false }
                ) {
                    viewModel.combo2Items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                viewModel.onCombo2Selected(item)
                                viewModel.isCombo2Expanded.value = false
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
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF43BD28)
                    )
                )
                Text(
                    text = "GPS Automatico",
                    modifier = Modifier.padding(start = 8.dp)
                )

            }
            // Botón con ícono
            IconButton(
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .size(48.dp)  // Tamaño pequeño del botón
                    .padding(end = 8.dp,)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle, // Cambia por otro ícono si es necesario
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
    viewModel: NewEvaluationViewModel
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(viewModel.evaluationList) { item ->
            EvaluationItem(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EvaluationItem(
    item: EvaluationItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Clases de datos necesarias para el ViewModel
data class ComboItem(
    val id: Int,
    val name: String
)

data class EvaluationItem(
    val id: Int,
    val title: String,
    val description: String
)