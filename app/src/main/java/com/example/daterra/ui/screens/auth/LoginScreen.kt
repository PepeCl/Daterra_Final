package com.example.daterra.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

import com.example.daterra.R
import com.example.daterra.ui.theme.*

// IMPORTACIONES PARA EL VIEWMODEL
import com.example.daterra.ui.viewmodel.AuthViewModel
import com.example.daterra.ui.viewmodel.AuthState

// IMPORTANTE: Ajusta esta línea según el paquete donde creaste el TokenManager
import com.example.daterra.data.local.TokenManager

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    // 1. EL CONTEXTO Y TOKEN MANAGER DEBEN IR DENTRO DE LA FUNCIÓN COMPOSABLE
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // 2. OBSERVAMOS EL TOKEN GUARDADO PARA EL AUTO-LOGIN
    val tokenGuardado by tokenManager.getToken.collectAsState(initial = null)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estados para manejar los mensajes de error locales (campos vacíos)
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Variable que controla la animación inicial
    var showForm by remember { mutableStateOf(false) }

    // Observamos el estado del backend (Cargando, Éxito, Error)
    val authState by authViewModel.authState.collectAsState()

    // AUTO-LOGIN: Efecto que reacciona si el token ya existe al abrir la app
    LaunchedEffect(tokenGuardado) {
        if (!tokenGuardado.isNullOrBlank()) {
            onNavigateToMain()
        }
    }

    // Efecto que espera 1.5 segundos (Splash) y luego activa el formulario
    LaunchedEffect(Unit) {
        delay(1500)
        showForm = true
    }

    // Efecto que reacciona cuando el login manual es exitoso en el backend
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onNavigateToMain()
        }
    }

    // Función de validación para evitar que envíen campos vacíos a la red
    fun validateLogin(): Boolean {
        var isValid = true
        if (email.isBlank()) {
            emailError = "Ingresa tu correo"
            isValid = false
        }
        if (password.isBlank()) {
            passwordError = "Ingresa tu contraseña"
            isValid = false
        }
        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DaterraBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // LOGO DE LA APLICACIÓN
        Image(
            painter = painterResource(id = R.drawable.logo_daterra),
            contentDescription = "Logo Daterra",
            modifier = Modifier.size(if (showForm) 120.dp else 180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TEXTO CON DOBLE COLOR
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF1B2E4B))) {
                    append("Da")
                }
                withStyle(style = SpanStyle(color = DaterraPrimary)) {
                    append("terra")
                }
            },
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        // FORMULARIO ANIMADO
        AnimatedVisibility(
            visible = showForm,
            enter = fadeIn(animationSpec = tween(1000)) + expandVertically(animationSpec = tween(1000))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Datos claros para una tierra limpia",
                    fontSize = 14.sp,
                    color = DaterraText.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { input ->
                        email = input.filter { !it.isWhitespace() }.take(100)
                        emailError = null
                        if (authState is AuthState.Error) authViewModel.resetState() // Limpiar error de red al escribir
                    },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    isError = emailError != null,
                    supportingText = { if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { input ->
                        password = input.filter { !it.isWhitespace() }.take(50)
                        passwordError = null
                        if (authState is AuthState.Error) authViewModel.resetState() // Limpiar error de red al escribir
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    isError = passwordError != null,
                    supportingText = { if (passwordError != null) Text(passwordError!!, color = MaterialTheme.colorScheme.error) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÓN DE LOGIN CONECTADO AL VIEWMODEL
                Button(
                    onClick = {
                        if (validateLogin()) {
                            // PASAMOS EL TOKEN MANAGER AL VIEWMODEL
                            authViewModel.loginUser(email, password, tokenManager)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DaterraPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = authState !is AuthState.Loading // Se deshabilita para evitar doble clic
                ) {
                    Text(
                        text = if (authState is AuthState.Loading) "Conectando..." else "Iniciar Sesión",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                // MENSAJE DE ERROR DESDE EL BACKEND
                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (authState as AuthState.Error).error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = DaterraSecundary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}