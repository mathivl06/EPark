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
import com.example.eparkprogram.ui.shared.BottomNavBar

data class SessionHistory(
    val id: Int,
    val zone: String,
    val spot: String,
    val plate: String,
    val date: String,
    val duration: String,
    val amount: String,
    val isOffline: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {

    val sessions = listOf(
        SessionHistory(1, "Zona A - Centro", "0042", "ABC-1234", "29 may 2026", "1h 42m", "₡850"),
        SessionHistory(2, "Zona B - Plaza", "0015", "ABC-1234", "28 may 2026", "0h 30m", "₡200"),
        SessionHistory(3, "Zona C - Mercado", "0087", "XYZ-5678", "27 may 2026", "2h 15m", "₡1.125", true),
        SessionHistory(4, "Zona A - Centro", "0033", "ABC-1234", "25 may 2026", "0h 45m", "₡375"),
        SessionHistory(5, "Zona E - Hospital", "0061", "ABC-1234", "20 may 2026", "3h 00m", "₡1.500")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
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
                    "${sessions.size} sesiones registradas",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(sessions) { session ->
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
                            Text(session.zone, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (session.isOffline) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFFF6F00)
                                ) {
                                    Text(
                                        "OFFLINE",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                                Text("Espacio ${session.spot}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.DirectionsCar,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(session.plate, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(session.date, fontSize = 12.sp, color = Color.Gray)
                                Text(session.duration, fontSize = 13.sp)
                            }
                            Text(
                                session.amount,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }
            }
        }
    }
}