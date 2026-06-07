package com.example.eparkprogram.ui.driver

import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eparkprogram.data.model.ParkingZone
import com.example.eparkprogram.data.repository.ZoneRepository
import com.example.eparkprogram.data.session.ParkingSelection
import com.example.eparkprogram.navigation.Routes
import com.example.eparkprogram.utils.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyZonesScreen(
    navController: NavController,
    municipalityId: Int? = null,
) {
    val context = LocalContext.current
    val zoneRepository = remember { ZoneRepository() }
    var zones by remember { mutableStateOf<List<ParkingZone>>(emptyList()) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            LocationHelper.getCurrentLocation(context) { location ->
                userLocation = location
            }
        }
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermissions(context)) {
            LocationHelper.getCurrentLocation(context) { location ->
                userLocation = location
            }
        } else {
            permissionLauncher.launch(LocationHelper.locationPermissions)
        }
    }

    LaunchedEffect(municipalityId, userLocation) {
        isLoading = true
        error = ""
        runCatching { zoneRepository.getZones(municipalityId) }
            .onSuccess { fetchedZones ->
                zones = if (userLocation != null) {
                    LocationHelper.sortZonesByDistance(
                        fetchedZones,
                        userLocation!!.latitude,
                        userLocation!!.longitude
                    )
                } else {
                    fetchedZones
                }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
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
                        ZoneCard(zone = zone, userLocation = userLocation) {
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
private fun ZoneCard(zone: ParkingZone, userLocation: Location?, onStartParking: () -> Unit) {
    val isAvailable = zone.availableSpaces > 0
    val distance = userLocation?.let {
        LocationHelper.calculateDistance(it.latitude, it.longitude, zone.latitude, zone.longitude)
    }

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

            Spacer(modifier = Modifier.height(12.dp))
            
            // Metadatos organizados en dos columnas para mejor alineación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    distance?.let {
                        InfoItem(
                            icon = Icons.Default.LocationOn,
                            text = if (it < 1000) "${it.toInt()}m" else "%.1fkm".format(it / 1000f)
                        )
                    }
                    InfoItem(
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        text = "Disponible"
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    InfoItem(
                        icon = Icons.Default.AttachMoney,
                        text = "${zone.currencyCode} ${"%.0f".format(zone.hourlyRate)}/h"
                    )
                    InfoItem(
                        icon = Icons.Default.Schedule,
                        text = "${zone.operationStartTime}-${zone.operationEndTime}"
                    )
                }
            }

            if (isAvailable) {
                Spacer(modifier = Modifier.height(16.dp))
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

@Composable
private fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF1565C0),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}