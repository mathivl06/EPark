package com.example.eparkprogram.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.eparkprogram.data.remote.AdminReportSummary
import com.example.eparkprogram.data.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {

    var summary by remember { mutableStateOf<AdminReportSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("01/05/2026") }
    var endDate by remember { mutableStateOf("31/05/2026") }

    LaunchedEffect(Unit) {
        try {
            summary = RetrofitClient.api.getAdminReportSummary()
        } catch (e: Exception) {
            errorMsg = "No se pudo cargar el reporte"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Selector de fechas
            Text("Rango de fechas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Desde") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Hasta") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            // Resumen general desde API
            Text("Resumen general", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1565C0))
                    }
                }
                errorMsg.isNotEmpty() -> {
                    Text(errorMsg, color = Color.Red, fontSize = 13.sp)
                }
                summary != null -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(
                            Triple(
                                "Zonas activas",
                                "${summary!!.totalZones}",
                                Color(0xFF1565C0)
                            ),
                            Triple(
                                "Sesiones activas",
                                "${summary!!.activeSessions}",
                                Color(0xFF2E7D32)
                            ),
                            Triple(
                                "Ingresos totales",
                                "₡${String.format("%.0f", summary!!.revenue)}",
                                Color(0xFF2E7D32)
                            )
                        ).forEach { (label, value, color) ->
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        value,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = color
                                    )
                                    Text(label, fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // Desglose por zona — pendiente de backend
            Text("Ingresos por zona", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.BarChart,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Desglose por zona próximamente",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}