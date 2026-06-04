package com.example.eparkprogram.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.remote.AdminZoneDto
import com.example.eparkprogram.data.repository.ZoneRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateManagementScreen(navController: NavController) {

    val zoneRepository = remember { ZoneRepository() }
    var zones by remember { mutableStateOf<List<AdminZoneDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf<AdminZoneDto?>(null) }
    var newRate by remember { mutableStateOf("") }
    var rateError by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            zones = zoneRepository.getAdminZones()
        } catch (e: Exception) {
            errorMsg = "No se pudieron cargar las zonas"
        } finally {
            isLoading = false
        }
    }

    if (showDialog && selectedZone != null) {
        AlertDialog(
            onDismissRequest = { if (!isSaving) showDialog = false },
            title = { Text("Actualizar tarifa") },
            text = {
                Column {
                    Text(selectedZone!!.zoneName, fontWeight = FontWeight.Bold)
                    Text(
                        "Tarifa actual: ₡${String.format("%.0f", selectedZone!!.hourlyRate)}/hora",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newRate,
                        onValueChange = { newRate = it; rateError = "" },
                        label = { Text("Nueva tarifa ₡/hora") },
                        isError = rateError.isNotEmpty(),
                        supportingText = { if (rateError.isNotEmpty()) Text(rateError) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isSaving
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "⚠ El cambio aplica solo a sesiones nuevas, no afecta sesiones activas.",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rate = newRate.toDoubleOrNull()
                        if (rate == null || rate < 0) {
                            rateError = "Ingresá una tarifa válida"
                            return@Button
                        }
                        isSaving = true
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Confirmar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    enabled = !isSaving
                ) { Text("Cancelar") }
            }
        )
    }

    if (isSaving && selectedZone != null) {
        LaunchedEffect(isSaving) {
            try {
                val rate = newRate.toDouble()
                zoneRepository.updateZoneTariff(selectedZone!!.zoneId, rate)
                zones = zoneRepository.getAdminZones()
                showDialog = false
                showSuccessDialog = true
            } catch (e: Exception) {
                rateError = "No se pudo actualizar la tarifa"
            } finally {
                isSaving = false
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Tarifa actualizada") },
            text = { Text("El cambio se registró correctamente y aplica a nuevas sesiones.") },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) { Text("Aceptar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de tarifas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }
            errorMsg.isNotEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMsg, color = Color.Red)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        ) {
                            Text(
                                "Los cambios de tarifa aplican únicamente a sesiones nuevas y no afectan sesiones activas en curso.",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                color = Color(0xFF1565C0)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(zones) { zone ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        zone.zoneName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        zone.description ?: zone.municipalityName,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "₡${String.format("%.0f", zone.hourlyRate)}/hora",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedZone = zone
                                        newRate = String.format("%.0f", zone.hourlyRate)
                                        rateError = ""
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Editar",
                                        tint = Color(0xFF1565C0)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}