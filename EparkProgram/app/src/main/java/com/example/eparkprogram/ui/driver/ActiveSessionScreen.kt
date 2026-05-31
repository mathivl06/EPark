package com.example.eparkprogram.ui.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.model.ParkingSession
import com.example.eparkprogram.data.repository.ParkingRepository
import com.example.eparkprogram.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(navController: NavController) {
    val parkingRepository = remember { ParkingRepository() }
    val scope = rememberCoroutineScope()
    var session by remember { mutableStateOf<ParkingSession?>(null) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var isFinishing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var showEndDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        runCatching { parkingRepository.getActiveSession() }
            .onSuccess {
                session = it
                isLoading = false
            }
            .onFailure {
                error = it.message ?: "No se pudo cargar la sesion"
                isLoading = false
            }
    }

    LaunchedEffect(session?.sessionId) {
        while (session != null) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val currentSession = session
    val hourlyRate = currentSession?.hourlyRateApplied ?: 0.0
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = "%02d:%02d:%02d".format(hours, minutes, seconds)
    val currentCost = (elapsedSeconds.coerceAtLeast(60) / 3600.0) * hourlyRate
    val costFormatted = "CRC ${"%,.0f".format(currentCost)}"

    if (showEndDialog && currentSession != null) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text("Finalizar sesion") },
            text = {
                Column {
                    Text("Confirmas que queres finalizar tu sesion de parqueo?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total aproximado: $costFormatted", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isFinishing = true
                        showEndDialog = false
                        scope.launch {
                            runCatching { parkingRepository.finishSession(currentSession.sessionId) }
                                .onSuccess {
                                    isFinishing = false
                                    navController.navigate(Routes.PAYMENT) {
                                        popUpTo(Routes.ACTIVE_SESSION) { inclusive = true }
                                    }
                                }
                                .onFailure {
                                    isFinishing = false
                                    error = it.message ?: "No se pudo finalizar la sesion"
                                }
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
                title = { Text("Sesion activa") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atras")
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
            when {
                isLoading -> CircularProgressIndicator(color = Color(0xFF1565C0))
                error.isNotBlank() -> Text(error, color = MaterialTheme.colorScheme.error)
                currentSession == null -> Text("No hay una sesion activa.")
                else -> {
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFF4CAF50)) {
                        Text(
                            "ACTIVA",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

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
                            Icon(Icons.Filled.AccessTime, contentDescription = null, tint = Color(0xFFBBDEFB), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(timeFormatted, fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Tiempo transcurrido", color = Color(0xFFBBDEFB), fontSize = 13.sp)
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Costo aproximado", color = Color.Gray, fontSize = 13.sp)
                                Text(costFormatted, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            }
                            Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(36.dp))
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailRow("Zona", currentSession.zoneName)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow("Espacio", currentSession.spaceCode)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow("Vehiculo", currentSession.plateNumber ?: "-")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow("Tarifa", "CRC ${"%.0f".format(hourlyRate)}/hora")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { showEndDialog = true },
                        enabled = !isFinishing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        if (isFinishing) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finalizar sesion", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
