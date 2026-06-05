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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.model.ParkingZone
import com.example.eparkprogram.data.repository.ZoneRepository
import com.example.eparkprogram.data.session.ParkingSelection
import com.example.eparkprogram.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyZonesScreen(
    navController: NavController,
    municipalityId: Int? = null   // FIX: recibe el id de la municipalidad seleccionada
) {
    val zoneRepository = remember { ZoneRepository() }
    var zones by remember { mutableStateOf<List<ParkingZone>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    // FIX: se usa municipalityId como key para que recargue si cambia,
    // y se pasa al API para filtrar zonas por municipalidad
    LaunchedEffect(municipalityId) {
        isLoading = true
        error = ""
        runCatching { zoneRepository.getZones(municipalityId) }
            .onSuccess {
                zones = it
                isLoading = false
            }
            .onFailure {
                error = it.message ?: "No se pudieron cargar las zonas"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zonas cercanas") },
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
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }

            error.isNotBlank() -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                )
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
                            "${zones.count { it.availableSpaces > 0 }} zonas con espacios disponibles",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(zones) { zone ->
                        ZoneCard(zone = zone) {
                            ParkingSelection.selectedZone = zone
                            navController.navigate(Routes.START_PARKING)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ZoneCard(zone: ParkingZone, onStartParking: () -> Unit) {
    val isAvailable = zone.availableSpaces > 0

    Card(
        onClick = { if (isAvailable) onStartParking() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) Color.White else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    zone.zoneName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isAvailable) Color.Black else Color.Gray
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFE53935)
                ) {
                    Text(
                        if (isAvailable) "${zone.availableSpaces} disponibles" else "Sin espacios",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                zone.description ?: zone.municipalityName,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.DirectionsWalk,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Disponible", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.AttachMoney,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${zone.currencyCode} ${"%.0f".format(zone.hourlyRate)}/h", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${zone.operationStartTime}-${zone.operationEndTime}",
                        fontSize = 12.sp
                    )
                }
            }

            if (isAvailable) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onStartParking,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Parquear aqui")
                }
            }
        }
    }
}