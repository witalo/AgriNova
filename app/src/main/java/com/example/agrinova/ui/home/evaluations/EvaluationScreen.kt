package com.example.agrinova.ui.home.evaluations

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.agrinova.di.models.CartillaEvaluacionDomainModel
import com.example.agrinova.ui.home.screens.ProfileViewModel

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluationScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val cartillas = listOf(
        CartillaEvaluacionDomainModel(1, "001", "Cartilla 1", true, 1),
        CartillaEvaluacionDomainModel(2, "002", "Cartilla 2", true, 1),
        CartillaEvaluacionDomainModel(3, "003","Cartilla 3",true, 1),
        CartillaEvaluacionDomainModel(4, "004", "Cartilla 4",true, 1),
        CartillaEvaluacionDomainModel(5, "005", "Cartilla 5", true, 1)
    )
//    val cartillas by viewModel.cartillas.collectAsState() // Lista de fundos
    var selectedCartilla by remember { mutableStateOf<CartillaEvaluacionDomainModel?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    // Ejemplo de datos para la lista
    val items = List(20) { "Item ${it + 1}" } // Lista de ejemplo con más elementos

    Box(modifier = Modifier.fillMaxSize()) {
        // Llama a la función EvaluationCard y pásale la lista de datos
        EvaluationCard(
            cartillas = cartillas,
            selectedCartilla = selectedCartilla,
            selectedDate = selectedDate,
            onCartillaSelected = { selectedCartilla = it },
            onDateSelected = { selectedDate = it } // Esta función actualiza la fecha seleccionada
        )
        // Botón flotante en la esquina inferior derecha
        FloatingActionButton(
            onClick = {
                // Acción al hacer clic en el botón
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF1B87DE), // Color personalizado de fondo
            contentColor = Color.White // Color personalizado del ícono
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar"
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluationCard(
    cartillas: List<CartillaEvaluacionDomainModel>,
    selectedCartilla: CartillaEvaluacionDomainModel?,
    selectedDate: LocalDate?,
    onCartillaSelected: (CartillaEvaluacionDomainModel?) -> Unit,
    onDateSelected: (LocalDate?) -> Unit
) {
    val context = LocalContext.current // Obtener el contexto dentro del composable

    Card(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el ancho y alto del dispositivo
            .padding(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize() // Ajusta el contenido dentro del Card
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
//                    .background(Color.White)
            ) {
                Column {
                    // Usamos el selector de cartilla
                    GenericSelector(
                        items = cartillas,
                        selectedItem = selectedCartilla,
                        onItemSelected = { onCartillaSelected(it) },
                        getDisplayText = { it.nombre },
                        label = "Selecciona una Cartilla"
                    )

                    // Campo de fecha y botón en la misma fila
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDate?.toString() ?: "Fecha",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha") },
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = {
                                    // Mostrar el selector de fecha
                                    showDatePickerDialog(context) { calendar ->
                                        // Convertir la fecha seleccionada a LocalDate
                                        val selectedLocalDate = calendar?.time?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                                        onDateSelected(selectedLocalDate) // Pasar la fecha seleccionada
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Fecha"
                                    )
                                }
                            }
                        )

                        // Botón en la misma fila
                        Button(
                            onClick = {
                                // Acción del botón
                            },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(8.dp), // Añadido para dar espacio alrededor del botón
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B87DE), // Color personalizado de fondo
                                contentColor = Color.White // Color del texto e icono
                            ),
                            shape = RoundedCornerShape(12.dp) // Forma redondeada
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send, // Icono hacia arriba
                                contentDescription = "Subir",
                                modifier = Modifier.size(20.dp) // Tamaño del icono
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el icono y el texto
                            Text("Subir") // Texto del botón
                        }
                    }
                }
            }

            // Cuerpo - Lista de elementos desplazable
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Limita la altura del cuerpo para que sea desplazable
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                items(List(30) { "Item ${it + 1}" }) { item ->
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    )
                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                }
            }

            // Pie
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF29C418), // Color inicial
                                Color(0xFF0D5708)  // Color final
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

fun showDatePickerDialog(context: Context, onDateSelected: (Calendar?) -> Unit) {
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            // Ajusta el mes (base 0 para Calendar)
            calendar.set(year, month, day)
            onDateSelected(calendar) // Pasa el calendario seleccionado
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
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

//@Composable
//fun EvaluationCard(items: List<String>) {
//    Card(
//        modifier = Modifier
//            .fillMaxSize() // Ocupa todo el ancho y alto del dispositivo
//            .padding(5.dp),
//        elevation = CardDefaults.cardElevation(4.dp),
//        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        )
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize() // Ajusta el contenido dentro del Card
//        ) {
//            // Encabezado
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        brush = Brush.linearGradient(
//                            colors = listOf(
//                                Color(0xFF43BD28), // Color inicial
//                                Color(0xFF148102)  // Color final
//                            )
//                        )
//                    )
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Evaluaciones",
//                    style = MaterialTheme.typography.titleLarge,
//                    color = Color(0xFFF9FAF9)
//                )
//            }
//
//            // Cuerpo - Lista de elementos desplazable
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f) // Limita la altura del cuerpo para que sea desplazable
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.background)
//                    .padding(16.dp)
//            ) {
//                items(items) { item ->
//                    Text(
//                        text = item,
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier
//                            .padding(vertical = 4.dp)
//                            .fillMaxWidth()
//                    )
//                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
//                }
//            }
//
//            // Pie
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        brush = Brush.linearGradient(
//                            colors = listOf(
//                                Color(0xFF29C418), // Color inicial
//                                Color(0xFF0D5708)  // Color final
//                            )
//                        )
//                    )
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Pie de la Card",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSecondary
//                )
//            }
//        }
//    }
//}