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
import com.example.eparkprogram.ui.shared.BottomNavBar

data class Fine(
    val id: Int,
    val type: String,
    val zone: String,
    val date: String,
    val amount: Int,
    val dueDate: String,
    val isPaid: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinesScreen(navController: NavController) {

    var selectedTab by remember { mutableStateOf(0) }
    var showPayDialog by remember { mutableStateOf(false) }
    var selectedFine by remember { mutableStateOf<Fine?>(null) }

    val fines = listOf(
        Fine(1, "Tiempo vencido", "Zona A - Centro", "5 may 2026", 5000, "20 may 2026", false),
        Fine(2, "Espacio inválido", "Zona B - Plaza", "1 may 2026", 3500, "16 may 2026", false),
        Fine(3, "Tiempo vencido", "Zona C - Mercado", "15 abr 2026", 5000, "30 abr 2026", true),
        Fine(4, "Sin pago", "Zona A - Centro", "10 abr 2026", 4000, "25 abr 2026", true)
    )

    val pending = fines.filter { !it.isPaid }
    val paid = fines.filter { it.isPaid }

    if (showPayDialog && selectedFine != null) {
        AlertDialog(
            onDismissRequest = { showPayDialog = false },
            title = { Text("Pagar multa") },
            text = {
                Column {
                    Text("¿Confirmás el pago de esta multa?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Monto: ₡${selectedFine!!.amount}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPayDialog = false
                        navController.navigate(Routes.PAYMENT)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) { Text("Pagar") }
            },
            dismissButton = {
                TextButton(onClick = { showPayDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis multas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Pendientes (${pending.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Pagadas (${paid.size})") }
                )
            }

            val currentList = if (selectedTab == 0) pending else paid

            if (currentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sin multas", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList) { fine ->
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Warning,
                                            contentDescription = null,
                                            tint = if (fine.isPaid) Color.Gray
                                            else Color(0xFFE53935),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(fine.type, fontWeight = FontWeight.Bold)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (fine.isPaid) Color(0xFF4CAF50)
                                        else Color(0xFFE53935)
                                    ) {
                                        Text(
                                            if (fine.isPaid) "PAGADA" else "PENDIENTE",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(fine.zone, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Fecha: ${fine.date}", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        "₡${fine.amount}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (fine.isPaid) Color.Gray else Color(0xFFE53935)
                                    )
                                }
                                if (!fine.isPaid) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Vence: ${fine.dueDate}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE53935)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            selectedFine = fine
                                            showPayDialog = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1565C0)
                                        )
                                    ) { Text("Pagar multa") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}