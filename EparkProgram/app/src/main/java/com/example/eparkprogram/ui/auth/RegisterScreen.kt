package com.example.eparkprogram.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eparkprogram.data.repository.AuthRepository
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegistered: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    val steps = listOf("Datos personales", "Verificación", "Vehículo")

    var fullName by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // Errores
    var nameError by remember { mutableStateOf("") }
    var idError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }
    var plateError by remember { mutableStateOf("") }

    fun validateStep0(): Boolean {
        var valid = true
        if (fullName.isBlank()) { nameError = "Requerido"; valid = false } else nameError = ""
        if (nationalId.length != 9 || !nationalId.all { it.isDigit() }) {
            idError = "La cédula debe tener 9 dígitos"; valid = false
        } else idError = ""
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Correo inválido"; valid = false
        } else emailError = ""
        val passRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")
        if (!passRegex.matches(password)) {
            passwordError = "Mín. 8 caracteres, una mayúscula, una minúscula y un símbolo"
            valid = false
        } else passwordError = ""
        if (password != confirmPassword) {
            confirmError = "Las contraseñas no coinciden"; valid = false
        } else confirmError = ""
        return valid
    }

    fun validateStep2(): Boolean {
        val plateRegex = Regex("^[A-Z]{3}-\\d{3,4}$")
        return if (!plateRegex.matches(vehiclePlate.uppercase())) {
            plateError = "Formato inválido. Ejemplo: ABC-1234"
            false
        } else {
            plateError = ""
            true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep == 0) onBackToLogin()
                        else currentStep--
                    }) {
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
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
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (index <= currentStep) Color(0xFF1565C0)
                                else Color(0xFFE0E0E0),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        color = if (index <= currentStep) Color.White
                                        else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = if (index <= currentStep) Color(0xFF1565C0) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Paso 0 — Datos personales
            if (currentStep == 0) {
                Text("Información personal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; nameError = "" },
                    label = { Text("Nombre completo") },
                    isError = nameError.isNotEmpty(),
                    supportingText = { if (nameError.isNotEmpty()) Text(nameError) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nationalId,
                    onValueChange = { if (it.length <= 9) nationalId = it; idError = "" },
                    label = { Text("Número de cédula") },
                    isError = idError.isNotEmpty(),
                    supportingText = { if (idError.isNotEmpty()) Text(idError) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = { Text("Correo electrónico") },
                    isError = emailError.isNotEmpty(),
                    supportingText = { if (emailError.isNotEmpty()) Text(emailError) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
                    label = { Text("Contraseña") },
                    isError = passwordError.isNotEmpty(),
                    supportingText = { if (passwordError.isNotEmpty()) Text(passwordError) },
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmError = "" },
                    label = { Text("Confirmar contraseña") },
                    isError = confirmError.isNotEmpty(),
                    supportingText = { if (confirmError.isNotEmpty()) Text(confirmError) },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                if (showConfirmPassword) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Paso 1 — Verificación de correo
            if (currentStep == 1) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Verificá tu correo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Enviamos un enlace de activación a:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        email,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Revisá tu bandeja de entrada y hacé clic en el enlace para activar tu cuenta antes de continuar.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            color = Color(0xFF1565C0),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Paso 2 — Registro de vehículo
            if (currentStep == 2) {
                Text("Registrá tu vehículo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Podés agregar más vehículos desde tu perfil después.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = vehiclePlate,
                    onValueChange = { vehiclePlate = it.uppercase(); plateError = "" },
                    label = { Text("Número de placa") },
                    placeholder = { Text("Ej: ABC-1234") },
                    isError = plateError.isNotEmpty(),
                    supportingText = { if (plateError.isNotEmpty()) Text(plateError) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón siguiente / finalizar
            Button(
                onClick = {
                    when (currentStep) {
                        0 -> { if (validateStep0()) currentStep = 1 }
                        1 -> { currentStep = 2 }
                        2 -> {
                            if (validateStep2()) {
                                isLoading = true
                                generalError = ""
                                scope.launch {
                                    runCatching {
                                        authRepository.registerDriver(
                                            fullName = fullName,
                                            nationalId = nationalId,
                                            email = email,
                                            password = password,
                                            vehiclePlate = vehiclePlate
                                        )
                                    }.onSuccess {
                                        isLoading = false
                                        onRegistered()
                                    }.onFailure { error ->
                                        isLoading = false
                                        generalError = error.message ?: "No se pudo crear la cuenta"
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = when (currentStep) {
                            2 -> "Finalizar registro"
                            else -> "Continuar"
                        },
                        fontSize = 16.sp
                    )
                }
            }

            if (generalError.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(generalError, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
        }
    }
}
