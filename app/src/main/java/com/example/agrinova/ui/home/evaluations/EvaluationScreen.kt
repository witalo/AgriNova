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
import java.text.SimpleDateFormat
import java.util.Locale
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
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.agrinova.R
import com.example.agrinova.data.dto.DatoValvulaDto
import com.example.agrinova.di.models.DatoDomainModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluationScreen(
    viewModel: EvaluationViewModel = hiltViewModel(),
    navController: NavHostController,
//    onNavigate: (String) -> Unit,
) {
    val context = LocalContext.current
    val cartillas by viewModel.cartillas.collectAsState() // Lista de fundos
    var selectedCartilla by remember { mutableStateOf<CartillaEvaluacionDomainModel?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val datos by viewModel.filteredDatos.collectAsState() // Lista de datos filtrados

    val uploadStatus by viewModel.uploadStatus.collectAsState()
    var uploadMessage by remember { mutableStateOf("Subiendo datos al servidor...") }

    // Estado para controlar mensajes de error/éxito
    LaunchedEffect(uploadStatus) {
        when (uploadStatus) {
            is UploadState.Loading -> {
                uploadMessage = "Subiendo datos al servidor..."
            }

            is UploadState.Success -> {
                delay(1000) // Pequeña demora antes de mostrar el mensaje de éxito
                Toast.makeText(
                    context,
                    (uploadStatus as UploadState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is UploadState.Error -> {
                Toast.makeText(
                    context,
                    (uploadStatus as UploadState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Llama a la función EvaluationCard y pásale la lista de datos
        EvaluationCard(
            cartillas = cartillas,
            selectedCartilla = selectedCartilla,
            selectedDate = selectedDate,
            onCartillaSelected = { selectedCartilla = it },
            onDateSelected = { selectedDate = it }, // Esta función actualiza la fecha seleccionada
            datos = datos,
            onUploadClick = {
                if (selectedDate == null || selectedCartilla == null) {
                    Toast.makeText(
                        context,
                        "Seleccione una fecha y una cartilla",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@EvaluationCard
                }
                if (datos.isEmpty()) {
                    Toast.makeText(context, "No hay datos para subir", Toast.LENGTH_SHORT).show()
                    return@EvaluationCard
                }
                viewModel.uploadDataToServer(selectedDate.toString(), selectedCartilla!!.id)
            }
        )
        // Llamada a cargar los datos cuando cartilla y fecha están seleccionados
        selectedDate?.let { date ->
            selectedCartilla?.let { cartilla ->
                viewModel.loadDatosByDateAndCartilla(date.toString(), cartilla.id)
            }
        }
        // Botón flotante en la esquina inferior derecha
        FloatingActionButton(
            onClick = {
                if (selectedCartilla != null) {
                    navController.navigate("newEvaluation/${selectedCartilla?.id}")
                } else {
                    Toast.makeText(context, "Seleccione una cartilla", Toast.LENGTH_SHORT).show()
                }
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
        // Loading Overlay
        LoadingOverlay(
            isLoading = uploadStatus is UploadState.Loading,
            message = uploadMessage
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluationCard(
    cartillas: List<CartillaEvaluacionDomainModel>,
    selectedCartilla: CartillaEvaluacionDomainModel?,
    selectedDate: LocalDate?,
    onCartillaSelected: (CartillaEvaluacionDomainModel?) -> Unit,
    onDateSelected: (LocalDate?) -> Unit,
    datos: List<DatoValvulaDto>,
    onUploadClick: () -> Unit
) {
    val context = LocalContext.current // Obtener el contexto dentro del composable

    // Mostrar diálogo de progreso
//    if (showProgressDialog) {
//        AlertDialog(
//            onDismissRequest = { },
//            title = { Text("Subiendo datos") },
//            text = {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    CircularProgressIndicator()
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text("Por favor espere...")
//                }
//            },
//            confirmButton = { }
//        )
//    }
    // Observar el estado de la subida
//    LaunchedEffect(uploadStatus) {
//        when (uploadStatus) {
//            is UploadState.Loading -> {
//                showProgressDialog = true
//            }
//            is UploadState.Success -> {
//                showProgressDialog = false
//                Toast.makeText(context, (uploadStatus as UploadState.Success).message, Toast.LENGTH_LONG).show()
//            }
//            is UploadState.Error -> {
//                showProgressDialog = false
//                Toast.makeText(context, (uploadStatus as UploadState.Error).message, Toast.LENGTH_LONG).show()
//            }
//            else -> {
//                showProgressDialog = false
//            }
//        }
//    }
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
                                        val selectedLocalDate = calendar?.time?.toInstant()
                                            ?.atZone(ZoneId.systemDefault())?.toLocalDate()
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
                        // Actualización del botón de subida
                        Button(
                            onClick = onUploadClick,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(8.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFCC014),
                                contentColor = Color.Transparent
                            ),
                            enabled = selectedDate != null && selectedCartilla != null && datos.isNotEmpty()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.cloud_upload),
                                contentDescription = "Subir",
                                modifier = Modifier.size(30.dp),
                                tint = Color.Unspecified
                            )
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
                items(datos) { dato ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp), // Espaciado adicional
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Dato ID y Lote Código
                        Text(
                            text = "(${dato.datoId}) ${dato.loteCodigo}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Negrita
                            color = Color(0xFF1B87DE), // Color personalizado
                            modifier = Modifier.padding(end = 8.dp) // Espacio entre las columnas
                        )

                        // Código de la válvula
                        Text(
                            text = dato.valvulaCodigo,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Negrita
                            color = Color(0xFF29C418), // Otro color personalizado para distinción
                            modifier = Modifier.padding(horizontal = 8.dp) // Espacio entre columnas
                        )
                        val originalDate = dato.datoFecha
                        // Extraer solo la hora, minutos y segundos
                        val timeOnly = try {
                            val inputFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val date = inputFormat.parse(originalDate)
                            val outputFormat = SimpleDateFormat(
                                "hh:mm:ss a",
                                Locale.getDefault()
                            ) // 'a' agrega AM/PM
                            if (date != null) {
                                outputFormat.format(date)
                            } else {
                                originalDate
                            }
                        } catch (e: Exception) {
                            originalDate // Si falla, usar el texto original
                        }

                        // Fecha del dato
                        Text(
                            text = timeOnly.toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Negrita
                            color = Color(0xFF0D5708), // Color personalizado
                            modifier = Modifier.padding(start = 8.dp) // Espacio al lado izquierdo
                        )
                    }

                    // Divider con opacidad para separar cada fila
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
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Total registros = ") // Este texto estará en negrita
                        }
                        append(datos.size.toString()) // Este texto tendrá el estilo predeterminado
                    },
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

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    message: String = "Subiendo datos al servidor..."
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) { /* Previene clicks */ },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = Color(0xFF1B87DE),
                        strokeWidth = 6.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    // Barra de progreso lineal animada
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = Color(0xFF29C418)
                    )

                    // Texto del estado actual
                    Text(
                        text = "Por favor espere...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}