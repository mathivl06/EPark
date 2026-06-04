package com.example.eparkprogram.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.remote.AdminZoneDto
import com.example.eparkprogram.data.repository.ZoneRepository
import com.example.eparkprogram.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneManagementScreen(navController: NavController) {

    val zoneRepository = remember { ZoneRepository() }
    var zones by remember { mutableStateOf<List<AdminZoneDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    var showMenuFor by remember { mutableStateOf<Int?>(null) }
    var showDeactivateDialog by remember { mutableStateOf<AdminZoneDto?>(null) }
    var isDeactivating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            zones = zoneRepository.getAdminZones()
        } catch (e: Exception) {
            errorMsg = "No se pudieron cargar las zonas"
        } finally {
            isLoading = false
        }
    }

    if (showDeactivateDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = null },
            title = { Text("Desactivar zona") },
            text = { Text("¿Confirmás que querés desactivar ${showDeactivateDialog!!.zoneName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        val zone = showDeactivateDialog!!
                        isDeactivating = true
                        showDeactivateDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Desactivar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de zonas") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ZONE_EDITOR) },
                containerColor = Color(0xFF1565C0)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva zona", tint = Color.White)
            }
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
            zones.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Map,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sin zonas registradas", color = Color.Gray)
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
                        Text(
                            "${zones.size} zonas registradas",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    items(zones) { zone ->
                        val isActive = zone.status == "ACTIVE"
                        val occupancyRatio = if (zone.totalSpaces > 0)
                            zone.occupiedSpaces.toFloat() / zone.totalSpaces.toFloat()
                        else 0f
                        val statusColor = when {
                            !isActive -> Color(0xFF9E9E9E)
                            occupancyRatio >= 1f -> Color(0xFFFB8C00)
                            else -> Color(0xFF4CAF50)
                        }
                        val statusText = when {
                            !isActive -> "INACTIVA"
                            occupancyRatio >= 1f -> "LLENA"
                            else -> "ACTIVA"
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
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
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = statusColor
                                        ) {
                                            Text(
                                                statusText,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                ),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Box {
                                            IconButton(onClick = { showMenuFor = zone.zoneId }) {
                                                Icon(Icons.Filled.MoreVert, contentDescription = null)
                                            }
                                            DropdownMenu(
                                                expanded = showMenuFor == zone.zoneId,
                                                onDismissRequest = { showMenuFor = null }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Editar") },
                                                    leadingIcon = {
                                                        Icon(Icons.Filled.Edit, contentDescription = null)
                                                    },
                                                    onClick = {
                                                        showMenuFor = null
                                                        navController.navigate("${Routes.ZONE_EDITOR}/${zone.zoneId}")
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            if (isActive) "Desactivar" else "Activar",
                                                            color = if (isActive) Color(0xFFE53935) else Color(0xFF4CAF50)
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Filled.PowerSettingsNew,
                                                            contentDescription = null,
                                                            tint = if (isActive) Color(0xFFE53935) else Color(0xFF4CAF50)
                                                        )
                                                    },
                                                    onClick = {
                                                        showMenuFor = null
                                                        showDeactivateDialog = zone
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    Column {
                                        Text("Espacios", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            "${zone.occupiedSpaces}/${zone.totalSpaces}",
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Column {
                                        Text("Tarifa", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            "₡${String.format("%.0f", zone.hourlyRate)}/hora",
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Column {
                                        Text("Horario", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            "${zone.operationStartTime} - ${zone.operationEndTime}",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { occupancyRatio },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = statusColor,
                                    trackColor = Color(0xFFE0E0E0)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}