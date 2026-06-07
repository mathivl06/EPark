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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.session.ParkingSelection
import com.example.eparkprogram.navigation.Routes
import kotlinx.coroutines.launch
import com.example.eparkprogram.data.repository.ParkingRepository

data class SavedCard(val id: Int, val last4: String, val brand: String, val expiry: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, fineId: Long? = null) {

    var currentStep by remember { mutableStateOf(0) }
    val steps = listOf("Método", "Confirmar", "Completado")
    var selectedCardId by remember { mutableStateOf(1) }
    val scope = rememberCoroutineScope()
    val parkingRepository = remember { ParkingRepository() }

    val savedCards = listOf(
        SavedCard(1, "4242", "Visa", "12/25"),
        SavedCard(2, "5555", "Mastercard", "08/26")
    )

    // FIX: datos reales de la sesión terminada en vez de valores hardcodeados
    val finished = ParkingSelection.lastFinishedSession
    val activeSession = ParkingSelection.lastActiveSession
    val elapsedMin = finished?.elapsedMinutes ?: 0
    val durationHours = elapsedMin / 60
    val durationMins = elapsedMin % 60
    val totalAmount = finished?.totalAmount ?: 0.0
    val sessionSummary = mapOf(
        "zone"     to (activeSession?.zoneName ?: "—"),
        "spot"     to (activeSession?.spaceCode ?: "—"),
        "duration" to "${durationHours}h ${durationMins}m",
        "rate"     to "₡${"%.0f".format(activeSession?.hourlyRateApplied ?: 0.0)}/hora",
        "total"    to "₡${"%.0f".format(totalAmount)}"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago") },
                navigationIcon = {
                    if (currentStep < 2) {
                        IconButton(onClick = {
                            if (currentStep == 0) navController.popBackStack()
                            else currentStep--
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                        }
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
                .padding(24.dp)
        ) {
            // Indicador de pasos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, label ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (index <= currentStep) Color(0xFF1565C0)
                            else Color(0xFFE0E0E0),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (index < currentStep) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        "${index + 1}",
                                        color = if (index <= currentStep) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            label,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = if (index <= currentStep) Color(0xFF1565C0) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Paso 0 — Seleccionar método
            if (currentStep == 0) {
                Text("Seleccioná tu método de pago", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                savedCards.forEach { card ->
                    Card(
                        onClick = { selectedCardId = card.id },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedCardId == card.id)
                                Color(0xFFE3F2FD) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CreditCard,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0)
                                )
                                Column {
                                    Text(
                                        "${card.brand} •••• ${card.last4}",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Vence ${card.expiry}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            if (selectedCardId == card.id) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }
            }

            // Paso 1 — Confirmar
            if (currentStep == 1) {
                Text("Resumen de pago", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        listOf(
                            "Zona"     to sessionSummary["zone"],
                            "Espacio"  to sessionSummary["spot"],
                            "Duración" to sessionSummary["duration"],
                            "Tarifa"   to sessionSummary["rate"]
                        ).forEach { (label, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, color = Color.Gray, fontSize = 13.sp)
                                Text(value ?: "", fontWeight = FontWeight.Medium)
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                sessionSummary["total"] ?: "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val selectedCard = savedCards.find { it.id == selectedCardId }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.CreditCard,
                            tint = Color(0xFF1565C0),
                            contentDescription = null
                        )
                        Text(
                            "${selectedCard?.brand} •••• ${selectedCard?.last4}",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Paso 2 — Completado
            if (currentStep == 2) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("¡Pago exitoso!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tu comprobante está disponible en tu historial.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total pagado", color = Color.Gray, fontSize = 13.sp)
                                Text(
                                    sessionSummary["total"] ?: "",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 18.sp
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("N° comprobante", color = Color.Gray, fontSize = 13.sp)
                                // FIX: usa el sessionId real como referencia del comprobante
                                Text(
                                    "REC-${finished?.sessionId ?: "—"}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (currentStep < 2) {
                Button(
                    onClick = {
                        if (currentStep == 1) {
                            scope.launch {
                                try {
                                    if (fineId != null) {
                                        parkingRepository.payFine(fineId)
                                    } else {
                                        val sessionId = finished?.sessionId
                                        if (sessionId != null) {
                                            parkingRepository.paySession(sessionId)
                                        }
                                    }
                                } catch (_: Exception) { }
                                currentStep++
                            }
                        } else {
                            currentStep++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text(
                        if (currentStep == 1) "Confirmar pago" else "Continuar",
                        fontSize = 16.sp
                    )
                }
            } else {
                Button(
                    onClick = {
                        ParkingSelection.lastFinishedSession = null
                        ParkingSelection.lastActiveSession = null
                        if (fineId != null) {
                            navController.navigate(Routes.FINES) {
                                popUpTo(Routes.DRIVER_HOME) { inclusive = false }
                            }
                        } else {
                            navController.navigate(Routes.HISTORY) {
                                popUpTo(Routes.DRIVER_HOME) { inclusive = false }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text(if (fineId != null) "Ver mis multas" else "Ver mi historial", fontSize = 16.sp)
                }
            }
        }
    }
}