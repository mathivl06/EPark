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
import retrofit2.HttpException
import org.json.JSONObject

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

    // FIX: igual que en LoginScreen, extrae el mensaje real del servidor
    fun parseError(e: Exception): String {
        return try {
            if (e is HttpException) {
                val body = e.response()?.errorBody()?.string()
                if (!body.isNullOrBlank()) {
                    JSONObject(body).optString("message", "").ifBlank { null }
                } else null
            } else null
        } catch (ex: Exception) {
            null
        } ?: when {
            e.message?.contains("401") == true ->
                "Sin autorización — hacé login real con el admin"
            e.message?.contains("400") == true ->
                "El admin no tiene municipalidad asignada en el sistema"
            e.message?.contains("Unable to resolve host") == true ->
                "Sin conexión al servidor"
            else -> e.message ?: "Error desconocido"
        }
    }

    LaunchedEffect(Unit) {
        try {
            zones = zoneRepository.getAdminZones()
        } catch (e: Exception) {
            // FIX: muestra el error real del servidor en vez de mensaje genérico
            errorMsg = "No se pudieron cargar las zonas: ${parseError(e)}"
        } finally {
            isLoading = false
        }
    }

    // FIX: LaunchedEffect fuera del bloque if, con guard al inicio
    // Esto evita que Compose lo ignore cuando isSaving cambia a true
    LaunchedEffect(isSaving) {
        if (!isSaving) return@LaunchedEffect
        val zone = selectedZone ?: run { isSaving = false; return@LaunchedEffect }
        try {
            val rate = newRate.toDouble()
            zoneRepository.updateZoneTariff(zone.zoneId, rate)
            // Recarga la lista para reflejar la nueva tarifa en pantalla
            zones = zoneRepository.getAdminZones()
            showDialog = false
            showSuccessDialog = true
        } catch (e: Exception) {
            // FIX: muestra el error real del servidor
            rateError = "Error: ${parseError(e)}"
        } finally {
            isSaving = false
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        ),
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
                        rateError = ""
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

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Tarifa actualizada") },
            text = {
                Text("El cambio se registró correctamente y aplica a nuevas sesiones.")
            },
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }

            errorMsg.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    // FIX: error en Card roja igual que el resto de pantallas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Text(
                                errorMsg,
                                color = Color(0xFFB71C1C),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            zones.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.AttachMoney,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No hay zonas disponibles", color = Color.Gray)
                    }
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "₡${String.format("%.0f", zone.hourlyRate)}/hora",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1565C0)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        // Muestra el estado de la zona junto a la tarifa
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = if (zone.status == "ACTIVE")
                                                Color(0xFFE8F5E9) else Color(0xFFFAFAFA)
                                        ) {
                                            Text(
                                                zone.status,
                                                modifier = Modifier.padding(
                                                    horizontal = 6.dp,
                                                    vertical = 2.dp
                                                ),
                                                fontSize = 10.sp,
                                                color = if (zone.status == "ACTIVE")
                                                    Color(0xFF2E7D32) else Color.Gray
                                            )
                                        }
                                    }
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
                                        contentDescription = "Editar tarifa",
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