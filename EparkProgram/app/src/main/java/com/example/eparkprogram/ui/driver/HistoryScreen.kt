package com.example.eparkprogram.ui.driver

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
import androidx.navigation.compose.currentBackStackEntryAsState  // FIX: import faltante
import com.example.eparkprogram.data.remote.SessionHistoryDto
import com.example.eparkprogram.data.repository.ParkingRepository
import com.example.eparkprogram.ui.shared.BottomNavBar
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {

    val parkingRepository = remember { ParkingRepository() }
    var sessions by remember { mutableStateOf<List<SessionHistoryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    suspend fun loadHistory() {
        isLoading = true
        errorMsg = ""
        try {
            sessions = parkingRepository.getSessionHistory()
        } catch (e: Exception) {
            errorMsg = "No se pudo cargar el historial"
        } finally {
            isLoading = false
        }
    }

    // FIX: import correcto para currentBackStackEntryAsState
    val currentEntry = navController.currentBackStackEntryAsState()
    LaunchedEffect(currentEntry.value) {
        loadHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                actions = {
                    IconButton(
                        onClick = { },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Recargar",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMsg, color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { }) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }

            sessions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.History,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sin sesiones registradas", color = Color.Gray)
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
                            "${sessions.size} sesiones registradas",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(sessions) { session ->
                        val elapsedMin = session.elapsedMinutes ?: 0
                        val hours = elapsedMin / 60
                        val mins = elapsedMin % 60
                        val duration = "${hours}h ${mins}m"
                        val amount = "₡${String.format(Locale.getDefault(), "%.0f", session.totalAmount ?: 0.0)}"
                        val isPaid = session.paymentStatus == "APPROVED"

                        // FIX: usa SimpleDateFormat en vez de java.time (compatible con API 24+)
                        val formattedDate = remember(session.startedAt) {
                            try {
                                // El servidor envía formato ISO 8601: "2026-05-29T14:32:00Z" o con offset
                                val inputFormats = listOf(
                                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                                )
                                val outputFormat = SimpleDateFormat(
                                    "d MMM yyyy · HH:mm",
                                    Locale("es")
                                )
                                var parsed = false
                                var result = session.startedAt
                                for (format in inputFormats) {
                                    if (parsed) break
                                    try {
                                        val sdf = SimpleDateFormat(format, Locale.US)
                                        sdf.isLenient = false
                                        val date = sdf.parse(session.startedAt)
                                        if (date != null) {
                                            result = outputFormat.format(date)
                                            parsed = true
                                        }
                                    } catch (ex: Exception) {
                                        // intenta el siguiente formato
                                    }
                                }
                                result
                            } catch (e: Exception) {
                                session.startedAt
                            }
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
                                    Text(
                                        session.zoneName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (isPaid) Color(0xFF4CAF50) else Color(0xFFE53935)
                                    ) {
                                        Text(
                                            if (isPaid) "PAGADO" else "PENDIENTE",
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            ),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.LocalParking,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Espacio ${session.spaceCode}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.DirectionsCar,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            session.plateNumber,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            formattedDate,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Text(duration, fontSize = 13.sp)
                                    }
                                    Text(
                                        amount,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1565C0)
                                    )
                                }

                                session.receiptNumber?.let { receipt ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Comprobante: $receipt",
                                        fontSize = 11.sp,
                                        color = Color.Gray
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