package com.example.daterra.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

import com.example.daterra.R // Importante para cargar la imagen
import com.example.daterra.ui.theme.* // Tus colores

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onNavigateToMain: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Variable que controla la animación inicial
    var showForm by remember { mutableStateOf(false) }

    // Efecto que espera 1.5 segundos (Splash) y luego activa el formulario
    LaunchedEffect(Unit) {
        delay(1500)
        showForm = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DaterraBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Mantiene todo en el centro de la pantalla
    ) {

        // 1. LOGO DE LA APLICACIÓN
        Image(
            painter = painterResource(id = R.drawable.logo_daterra),
            contentDescription = "Logo Daterra",
            modifier = Modifier.size(if (showForm) 120.dp else 180.dp) // Achica el logo cuando sube
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. TEXTO DATERRA CON DOBLE COLOR
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF1B2E4B))) { // Azul oscuro
                    append("Da")
                }
                withStyle(style = SpanStyle(color = DaterraPrimary)) { // Tu verde primario
                    append("terra")
                }
            },
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        // 3. FORMULARIO ANIMADO (Aparece desplazando el logo hacia arriba)
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
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onNavigateToMain() },
                    colors = ButtonDefaults.buttonColors(containerColor = DaterraPrimary),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Iniciar Sesión", fontSize = 16.sp, color = Color.White)
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