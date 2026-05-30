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
import com.example.eparkprogram.navigation.Routes

data class Zone(val id: Int, val name: String, val spots: Int, val occupied: Int, val rate: Int, val status: String, val address: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneManagementScreen(navController: NavController) {

    var zones by remember {
        mutableStateOf(listOf(
            Zone(1, "Zona A - Centro", 20, 12, 500, "ACTIVE", "Av. Central"),
            Zone(2, "Zona B - Plaza", 15, 8, 400, "ACTIVE", "Calle 2"),
            Zone(3, "Zona C - Mercado", 30, 30, 350, "FULL", "Av. 2"),
            Zone(4, "Zona D - Parque", 10, 0, 300, "INACTIVE", "Calle 9"),
            Zone(5, "Zona E - Hospital", 25, 20, 450, "ACTIVE", "Av. 8")
        ))
    }

    var showMenuFor by remember { mutableStateOf<Int?>(null) }
    var showDeactivateDialog by remember { mutableStateOf<Zone?>(null) }

    if (showDeactivateDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = null },
            title = { Text("Desactivar zona") },
            text = { Text("¿Confirmás que querés desactivar ${showDeactivateDialog!!.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        zones = zones.map {
                            if (it.id == showDeactivateDialog!!.id) it.copy(status = "INACTIVE") else it
                        }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("${zones.size} zonas registradas", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(zones) { zone ->
                val statusColor = when (zone.status) {
                    "ACTIVE" -> Color(0xFF4CAF50)
                    "FULL" -> Color(0xFFFB8C00)
                    else -> Color(0xFF9E9E9E)
                }
                val statusText = when (zone.status) {
                    "ACTIVE" -> "ACTIVA"
                    "FULL" -> "LLENA"
                    else -> "INACTIVA"
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
                                Text(zone.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(zone.address, fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = statusColor
                                ) {
                                    Text(
                                        statusText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box {
                                    IconButton(onClick = { showMenuFor = zone.id }) {
                                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                                    }
                                    DropdownMenu(
                                        expanded = showMenuFor == zone.id,
                                        onDismissRequest = { showMenuFor = null }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Editar") },
                                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                            onClick = {
                                                showMenuFor = null
                                                navController.navigate(Routes.ZONE_EDITOR)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Desactivar", color = Color(0xFFE53935)) },
                                            leadingIcon = {
                                                Icon(Icons.Filled.PowerSettingsNew, contentDescription = null, tint = Color(0xFFE53935))
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
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Espacios", fontSize = 12.sp, color = Color.Gray)
                                Text("${zone.occupied}/${zone.spots}", fontWeight = FontWeight.Medium)
                            }
                            Column {
                                Text("Tarifa", fontSize = 12.sp, color = Color.Gray)
                                Text("₡${zone.rate}/hora", fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { zone.occupied.toFloat() / zone.spots.toFloat() },
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