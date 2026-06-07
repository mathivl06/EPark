package com.example.eparkprogram.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.eparkprogram.data.remote.AdminZoneRequest
import com.example.eparkprogram.data.repository.ZoneRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneEditorScreen(navController: NavController, zoneId: Int? = null) {

    val isEditing = zoneId != null
    val zoneRepository = remember { ZoneRepository() }

    var zoneName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var spots by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("06:00") }
    var endTime by remember { mutableStateOf("22:00") }
    var isActive by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(isEditing) }
    var isSaving by remember { mutableStateOf(false) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var spotsError by remember { mutableStateOf("") }
    var rateError by remember { mutableStateOf("") }

    // Si estamos editando, carga los datos existentes
    LaunchedEffect(zoneId) {
        if (zoneId != null) {
            try {
                val zones = zoneRepository.getAdminZones()
                val zone = zones.find { it.zoneId == zoneId }
                if (zone != null) {
                    zoneName = zone.zoneName
                    address = zone.description ?: ""
                    spots = zone.totalSpaces.toString()
                    rate = String.format("%.0f", zone.hourlyRate)
                    startTime = zone.operationStartTime
                    endTime = zone.operationEndTime
                    isActive = zone.status == "ACTIVE"
                }
            } catch (e: Exception) {
                // FIX: muestra el error real en vez de mensaje genérico
                errorMsg = "No se pudo cargar la zona: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // FIX: LaunchedEffect de guardado movido aquí arriba, dentro del scope del Composable
    // pero fuera del Scaffold, para que Compose lo detecte correctamente
    LaunchedEffect(isSaving) {
        if (!isSaving) return@LaunchedEffect
        try {
            val request = AdminZoneRequest(
                zoneName = zoneName.trim(),
                description = address.trim().ifBlank { null },
                operationStartTime = startTime,
                operationEndTime = endTime,
                totalSpaces = spots.toInt(),
                status = if (isActive) "ACTIVE" else "INACTIVE",
                hourlyRate = rate.toDouble()
            )
            if (isEditing && zoneId != null) {
                zoneRepository.updateAdminZone(zoneId, request)
                val newRate = rate.toDoubleOrNull()
                if (newRate != null) {
                    zoneRepository.updateZoneTariff(zoneId, newRate)
                }
            } else {
                zoneRepository.createAdminZone(request)
            }
            showSavedDialog = true
        } catch (e: Exception) {
            // FIX: muestra el mensaje exacto del servidor para ayudar a diagnosticar
            // el error más común es "Admin user has no municipality assigned"
            // lo cual significa que el admin debe hacer login real, no usar el botón de dev
            errorMsg = "Error al guardar: ${e.message}"
        } finally {
            isSaving = false
        }
    }

    fun validate(): Boolean {
        var valid = true
        if (zoneName.isBlank()) { nameError = "Requerido"; valid = false } else nameError = ""
        if (spots.toIntOrNull() == null || spots.toInt() <= 0) {
            spotsError = "Ingresá un número válido"; valid = false
        } else spotsError = ""
        if (rate.toDoubleOrNull() == null || rate.toDouble() < 0) {
            rateError = "Ingresá una tarifa válida"; valid = false
        } else rateError = ""
        return valid
    }

    if (showSavedDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(if (isEditing) "Zona actualizada" else "Zona creada") },
            text = { Text("Los cambios se registraron correctamente.") },
            confirmButton = {
                Button(onClick = {
                    showSavedDialog = false
                    navController.popBackStack()
                }) { Text("Aceptar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar zona" else "Nueva zona") },
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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1565C0))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (errorMsg.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        errorMsg,
                        color = Color(0xFFB71C1C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = zoneName,
                onValueChange = { zoneName = it; nameError = "" },
                label = { Text("Nombre de la zona") },
                isError = nameError.isNotEmpty(),
                supportingText = { if (nameError.isNotEmpty()) Text(nameError) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Descripción / Dirección") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = spots,
                    onValueChange = { spots = it; spotsError = "" },
                    label = { Text("Espacios") },
                    isError = spotsError.isNotEmpty(),
                    supportingText = { if (spotsError.isNotEmpty()) Text(spotsError) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it; rateError = "" },
                    label = { Text("Tarifa ₡/hora") },
                    isError = rateError.isNotEmpty(),
                    supportingText = { if (rateError.isNotEmpty()) Text(rateError) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Hora inicio") },
                    placeholder = { Text("06:00") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Hora fin") },
                    placeholder = { Text("22:00") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Zona activa", fontWeight = FontWeight.Medium)
                Switch(checked = isActive, onCheckedChange = { isActive = it })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button
                    errorMsg = ""
                    isSaving = true
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditing) "Guardar cambios" else "Crear zona", fontSize = 16.sp)
                }
            }
        }
    }
}