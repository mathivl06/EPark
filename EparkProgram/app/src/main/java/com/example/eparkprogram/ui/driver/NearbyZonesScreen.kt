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
import com.example.eparkprogram.navigation.Routes

data class ParkingZoneItem(
    val id: Int,
    val name: String,
    val address: String,
    val distance: String,
    val available: Int,
    val total: Int,
    val rate: String,
    val hours: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyZonesScreen(navController: NavController) {

    val zones = listOf(
        ParkingZoneItem(1, "Zona A - Centro", "Av. Central, San José", "120 m", 8, 20, "₡500/hora", "6:00 AM - 10:00 PM"),
        ParkingZoneItem(2, "Zona B - Plaza", "Calle 2, San José", "250 m", 3, 15, "₡400/hora", "7:00 AM - 9:00 PM"),
        ParkingZoneItem(3, "Zona C - Mercado", "Av. 2, San José", "400 m", 12, 30, "₡350/hora", "6:00 AM - 8:00 PM"),
        ParkingZoneItem(4, "Zona D - Parque", "Calle 9, San José", "550 m", 0, 10, "₡300/hora", "8:00 AM - 6:00 PM"),
        ParkingZoneItem(5, "Zona E - Hospital", "Av. 8, San José", "700 m", 5, 25, "₡450/hora", "6:00 AM - 11:00 PM")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zonas cercanas") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "${zones.count { it.available > 0 }} zonas con espacios disponibles",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(zones) { zone ->
                val isAvailable = zone.available > 0

                Card(
                    onClick = {
                        if (isAvailable) navController.navigate(Routes.START_PARKING)
                    },
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
                                zone.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isAvailable) Color.Black else Color.Gray
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFE53935)
                            ) {
                                Text(
                                    if (isAvailable) "${zone.available} disponibles"
                                    else "Sin espacios",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(zone.address, fontSize = 13.sp, color = Color.Gray)

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
                                Text(zone.distance, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.AttachMoney,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(zone.rate, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(zone.hours, fontSize = 12.sp)
                            }
                        }

                        if (isAvailable) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { navController.navigate(Routes.START_PARKING) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1565C0)
                                )
                            ) {
                                Text("Parquear aquí")
                            }
                        }
                    }
                }
            }
        }
    }
}