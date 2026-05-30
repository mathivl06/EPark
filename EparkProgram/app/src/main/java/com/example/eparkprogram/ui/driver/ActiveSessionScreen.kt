package com.example.eparkprogram.ui.driver

import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(navController: NavController) {

    var elapsedSeconds by remember { mutableStateOf(6120) } // 1h 42m inicial
    var showEndDialog by remember { mutableStateOf(false) }
    val hourlyRate = 500.0

    // Cronómetro en tiempo real
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    val currentCost = (elapsedSeconds / 3600.0) * hourlyRate
    val costFormatted = "₡${String.format("%,.0f", currentCost)}"

    val sessionStatus = when {
        elapsedSeconds > 10800 -> "VENCIDA"
        elapsedSeconds > 9900 -> "POR VENCER"
        else -> "ACTIVA"
    }
    val statusColor = when (sessionStatus) {
        "VENCIDA" -> Color(0xFFE53935)
        "POR VENCER" -> Color(0xFFFB8C00)
        else -> Color(0xFF4CAF50)
    }

    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text("Finalizar sesión") },
            text = {
                Column {
                    Text("¿Confirmás que querés finalizar tu sesión de parqueo?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total a pagar: $costFormatted",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndDialog = false
                        navController.navigate(Routes.PAYMENT) {
                            popUpTo(Routes.ACTIVE_SESSION) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sesión activa") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Estado
            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor
            ) {
                Text(
                    sessionStatus,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Cronómetro
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFBBDEFB),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        timeFormatted,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Tiempo transcurrido", color = Color(0xFFBBDEFB), fontSize = 13.sp)
                }
            }

            // Costo acumulado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Costo acumulado", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            costFormatted,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }
                    Icon(
                        Icons.Filled.AttachMoney,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Detalles de la sesión
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Zona", color = Color.Gray, fontSize = 13.sp)
                        Text("Zona A - Centro", fontWeight = FontWeight.Medium)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Espacio", color = Color.Gray, fontSize = 13.sp)
                        Text("0042", fontWeight = FontWeight.Medium)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vehículo", color = Color.Gray, fontSize = 13.sp)
                        Text("ABC-1234", fontWeight = FontWeight.Medium)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tarifa", color = Color.Gray, fontSize = 13.sp)
                        Text("₡500/hora", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showEndDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Icon(Icons.Filled.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finalizar sesión", fontSize = 16.sp)
            }
        }
    }
}