package com.example.eparkprogram.ui.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartParkingScreen(navController: NavController) {

    var currentStep by remember { mutableStateOf(0) }
    val steps = listOf("Vehículo", "Código", "Confirmar")

    var selectedPlate by remember { mutableStateOf("ABC-1234") }
    var spotCode by remember { mutableStateOf("") }
    var spotCodeError by remember { mutableStateOf("") }

    // Datos hardcodeados
    val vehicles = listOf("ABC-1234", "XYZ-5678")
    val confirmedSpot = mapOf(
        "zone" to "Zona A - Centro",
        "address" to "Av. Central, San José",
        "rate" to "₡500/hora"
    )

    fun validateSpotCode(): Boolean {
        return if (spotCode.length != 4 || !spotCode.all { it.isDigit() }) {
            spotCodeError = "El código debe tener exactamente 4 dígitos"
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
                        if (currentStep == 0) navController.popBackStack()
                        else currentStep--
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
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
            // Indicador de pasos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, label ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (index <= currentStep) Color(0xFF1565C0)
                            else Color(0xFFE0E0E0),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${index + 1}",
                                    color = if (index <= currentStep) Color.White
                                    else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            label,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = if (index <= currentStep) Color(0xFF1565C0) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Paso 0 — Seleccionar vehículo
            if (currentStep == 0) {
                Text("Seleccioná tu vehículo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                vehicles.forEach { plate ->
                    Card(
                        onClick = { selectedPlate = plate },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPlate == plate)
                                Color(0xFFE3F2FD) else Color.White
                        ),
                        border = if (selectedPlate == plate)
                            CardDefaults.outlinedCardBorder() else null,
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DirectionsCar,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0)
                                )
                                Text(plate, fontWeight = FontWeight.Medium)
                            }
                            if (selectedPlate == plate) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }
            }

            // Paso 1 — Código del espacio
            if (currentStep == 1) {
                Text("Código del espacio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Ingresá el número de 4 dígitos visible junto al espacio donde estacionaste.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = spotCode,
                    onValueChange = {
                        if (it.length <= 4) spotCode = it
                        spotCodeError = ""
                    },
                    label = { Text("Código del espacio") },
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

            // Paso 2 — Confirmación
            if (currentStep == 2) {
                Text("Confirmá tu sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Zona", color = Color.Gray, fontSize = 13.sp)
                            Text(confirmedSpot["zone"] ?: "", fontWeight = FontWeight.Medium)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Espacio", color = Color.Gray, fontSize = 13.sp)
                            Text(spotCode, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Vehículo", color = Color.Gray, fontSize = 13.sp)
                            Text(selectedPlate, fontWeight = FontWeight.Medium)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tarifa", color = Color.Gray, fontSize = 13.sp)
                            Text(confirmedSpot["rate"] ?: "", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    when (currentStep) {
                        0 -> currentStep = 1
                        1 -> { if (validateSpotCode()) currentStep = 2 }
                        2 -> {
                            navController.navigate(Routes.ACTIVE_SESSION) {
                                popUpTo(Routes.START_PARKING) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text(
                    if (currentStep == 2) "Confirmar e iniciar" else "Continuar",
                    fontSize = 16.sp
                )
            }
        }
    }
}