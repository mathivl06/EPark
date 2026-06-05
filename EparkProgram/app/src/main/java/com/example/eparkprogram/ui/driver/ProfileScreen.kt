package com.example.eparkprogram.ui.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.eparkprogram.data.model.Vehicle
import com.example.eparkprogram.data.remote.AuthSession
import com.example.eparkprogram.data.repository.ParkingRepository
import com.example.eparkprogram.navigation.Routes
import com.example.eparkprogram.ui.shared.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val parkingRepository = remember { ParkingRepository() }
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Lee los datos reales del usuario desde la sesión activa
    val driverName = AuthSession.fullName.orEmpty().ifBlank { "Usuario" }
    val driverEmail = AuthSession.userEmail.orEmpty()

    LaunchedEffect(Unit) {
        runCatching { parkingRepository.getVehicles() }
            .onSuccess { vehicles = it }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE3F2FD),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(driverName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if (driverEmail.isNotBlank()) {
                        Text(driverEmail, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // Datos personales
                Text(
                    "Datos personales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF1565C0))
                            Text(driverName.ifBlank { "—" }, fontSize = 14.sp)
                        }
                        if (driverEmail.isNotBlank()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFF1565C0))
                                Text(driverEmail, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vehículos
                Text(
                    "Mis vehículos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        when {
                            isLoading -> CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = Color(0xFF1565C0),
                                strokeWidth = 2.dp
                            )
                            vehicles.isEmpty() -> Text(
                                "Sin vehículos registrados",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            else -> vehicles.forEachIndexed { index, vehicle ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color(0xFF1565C0)
                                    )
                                    Text(vehicle.plateNumber, fontSize = 14.sp)
                                    vehicle.alias?.let {
                                        Text(" — $it", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                                if (index < vehicles.size - 1)
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Cerrar sesión
                OutlinedButton(
                    onClick = {
                        AuthSession.token = null
                        AuthSession.userRole = null
                        AuthSession.fullName = null
                        AuthSession.userEmail = null
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}