package com.example.eparkprogram.ui.driver

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.model.Vehicle
import com.example.eparkprogram.data.repository.ParkingRepository
import com.example.eparkprogram.data.session.ParkingSelection
import com.example.eparkprogram.navigation.Routes
import com.example.eparkprogram.notifications.NotificationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartParkingScreen(navController: NavController) {
    val context = LocalContext.current
    val parkingRepository = remember { ParkingRepository() }
    val scope = rememberCoroutineScope()
    val selectedZone = ParkingSelection.selectedZone

    var currentStep by remember { mutableIntStateOf(0) }
    val steps = listOf("Vehiculo", "Codigo", "Confirmar")
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var spotCode by remember { mutableStateOf("") }
    var spotCodeError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        runCatching { parkingRepository.getVehicles() }
            .onSuccess {
                vehicles = it
                selectedVehicle = it.firstOrNull()
                isLoading = false
            }
            .onFailure {
                generalError = it.message ?: "No se pudieron cargar los vehiculos"
                isLoading = false
            }
    }

    fun validateSpotCode(): Boolean {
        return if (spotCode.length != 4 || !spotCode.all { it.isDigit() }) {
            spotCodeError = "El codigo debe tener exactamente 4 digitos"
            false
        } else {
            spotCodeError = ""
            true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar parqueo") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep == 0) navController.popBackStack() else currentStep--
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atras")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, label ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (index <= currentStep) Color(0xFF1565C0) else Color(0xFFE0E0E0),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${index + 1}",
                                    color = if (index <= currentStep) Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            } else if (selectedZone == null) {
                Text("Selecciona una zona antes de iniciar parqueo.", color = MaterialTheme.colorScheme.error)
            } else {
                when (currentStep) {
                    0 -> VehicleStep(vehicles, selectedVehicle) { selectedVehicle = it }
                    1 -> SpaceCodeStep(spotCode, spotCodeError) {
                        if (it.length <= 4) spotCode = it
                        spotCodeError = ""
                    }
                    2 -> ConfirmStep(
                        zoneName = selectedZone.zoneName,
                        zoneDescription = selectedZone.description ?: selectedZone.municipalityName,
                        rate = "${selectedZone.currencyCode} ${"%.0f".format(selectedZone.hourlyRate)}/hora",
                        plate = selectedVehicle?.plateNumber.orEmpty(),
                        spotCode = spotCode
                    )
                }
            }

            if (generalError.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(generalError, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    when (currentStep) {
                        0 -> {
                            if (selectedVehicle == null) generalError = "No tenes vehiculos registrados"
                            else {
                                generalError = ""
                                currentStep = 1
                            }
                        }
                        1 -> if (validateSpotCode()) currentStep = 2
                        2 -> {
                            val vehicle = selectedVehicle ?: return@Button
                            val zone = selectedZone ?: return@Button
                            isSubmitting = true
                            generalError = ""
                            scope.launch {
                                runCatching {
                                    parkingRepository.startSession(vehicle.vehicleId, zone.zoneId, spotCode)
                                }.onSuccess { sessionId ->
                                    isSubmitting = false
                                    // 🔔 Iniciar servicio de monitoreo y programar notificación
                                    val startTime = System.currentTimeMillis()
                                    NotificationHelper.createNotificationChannel(context)
                                    com.example.eparkprogram.notifications.NotificationService.startService(
                                        context = context,
                                        sessionId = sessionId,
                                        startTimeMillis = startTime,
                                        durationMinutes = 180
                                    )
                                    navController.navigate(Routes.ACTIVE_SESSION) {
                                        popUpTo(Routes.START_PARKING) { inclusive = true }
                                    }
                                }.onFailure { error ->
                                    isSubmitting = false
                                    generalError = error.message ?: "No se pudo iniciar la sesion"
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading && !isSubmitting && selectedZone != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(if (currentStep == 2) "Confirmar e iniciar" else "Continuar", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun VehicleStep(
    vehicles: List<Vehicle>,
    selectedVehicle: Vehicle?,
    onSelect: (Vehicle) -> Unit
) {
    Text("Selecciona tu vehiculo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    vehicles.forEach { vehicle ->
        Card(
            onClick = { onSelect(vehicle) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedVehicle?.vehicleId == vehicle.vehicleId) Color(0xFFE3F2FD) else Color.White
            ),
            border = if (selectedVehicle?.vehicleId == vehicle.vehicleId) CardDefaults.outlinedCardBorder() else null,
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.DirectionsCar, contentDescription = null, tint = Color(0xFF1565C0))
                    Text(vehicle.plateNumber, fontWeight = FontWeight.Medium)
                }
                if (selectedVehicle?.vehicleId == vehicle.vehicleId) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF1565C0))
                }
            }
        }
    }
}

@Composable
private fun SpaceCodeStep(
    spotCode: String,
    spotCodeError: String,
    onSpotCodeChange: (String) -> Unit
) {
    Text("Codigo del espacio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Text("Ingresa el numero de 4 digitos visible junto al espacio.", fontSize = 13.sp, color = Color.Gray)
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = spotCode,
        onValueChange = onSpotCodeChange,
        label = { Text("Codigo del espacio") },
        placeholder = { Text("Ej: 0042") },
        isError = spotCodeError.isNotEmpty(),
        supportingText = { if (spotCodeError.isNotEmpty()) Text(spotCodeError) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun ConfirmStep(
    zoneName: String,
    zoneDescription: String,
    rate: String,
    plate: String,
    spotCode: String
) {
    Text("Confirma tu sesion", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailRow("Zona", zoneName)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailRow("Ubicacion", zoneDescription)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailRow("Espacio", spotCode)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailRow("Vehiculo", plate)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailRow("Tarifa", rate)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Medium)
    }
}