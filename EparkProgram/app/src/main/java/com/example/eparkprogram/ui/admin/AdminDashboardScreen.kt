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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.remote.AdminReportSummary
import com.example.eparkprogram.data.remote.RetrofitClient
import com.example.eparkprogram.navigation.Routes

data class StatCard(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {

    var summary by remember { mutableStateOf<AdminReportSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            summary = RetrofitClient.api.getAdminReportSummary()
        } catch (e: Exception) {
            // Si falla mostramos ceros
        } finally {
            isLoading = false
        }
    }

    val stats = listOf(
        StatCard(
            "Sesiones activas",
            if (isLoading) "..." else "${summary?.activeSessions ?: 0}",
            "en este momento",
            Icons.Filled.LocalParking,
            Color(0xFF1565C0)
        ),
        StatCard(
            "Ingresos totales",
            if (isLoading) "..." else "₡${String.format("%.0f", summary?.revenue ?: 0.0)}",
            "acumulado",
            Icons.Filled.AttachMoney,
            Color(0xFF2E7D32)
        ),
        StatCard(
            "Zonas activas",
            if (isLoading) "..." else "${summary?.totalZones ?: 0}",
            "habilitadas",
            Icons.Filled.Map,
            Color(0xFFE65100)
        ),
        StatCard(
            "Multas pendientes",
            "—",
            "próximamente",
            Icons.Filled.Receipt,
            Color(0xFFE53935)
        )
    )

    val recentActivity = listOf(
        Triple(Icons.Filled.PlayArrow, "Sesión iniciada — Zona A", "Hace 2 min"),
        Triple(Icons.Filled.AttachMoney, "Pago recibido — ₡850", "Hace 5 min"),
        Triple(Icons.Filled.Warning, "Espacio 0042 vencido", "Hace 8 min"),
        Triple(Icons.Filled.PlayArrow, "Sesión iniciada — Zona C", "Hace 12 min"),
        Triple(Icons.Filled.Receipt, "Nueva multa registrada", "Hace 15 min")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel administrativo", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
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

            Text("Resumen del día", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.take(2).forEach { stat ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                stat.icon,
                                contentDescription = null,
                                tint = stat.color,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stat.value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(stat.title, fontSize = 12.sp, color = Color.Gray)
                            Text(stat.subtitle, fontSize = 11.sp, color = stat.color)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.drop(2).forEach { stat ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                stat.icon,
                                contentDescription = null,
                                tint = stat.color,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stat.value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(stat.title, fontSize = 12.sp, color = Color.Gray)
                            Text(stat.subtitle, fontSize = 11.sp, color = stat.color)
                        }
                    }
                }
            }

            Text("Gestión", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = { navController.navigate(Routes.ZONE_MANAGEMENT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Map,
                            contentDescription = null,
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Zonas", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }
                Card(
                    onClick = { navController.navigate(Routes.RATE_MANAGEMENT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tarifas", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }
                Card(
                    onClick = { navController.navigate(Routes.REPORTS) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Assessment,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Reportes", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }
            }

            Text("Actividad reciente", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    recentActivity.forEachIndexed { index, (icon, description, time) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE3F2FD),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = Color(0xFF1565C0),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(description, fontSize = 13.sp)
                            }
                            Text(time, fontSize = 11.sp, color = Color.Gray)
                        }
                        if (index < recentActivity.size - 1)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}