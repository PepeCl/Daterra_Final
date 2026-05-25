package com.example.daterra.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daterra.ui.theme.*

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit, onNavigateToMain: () -> Unit) {
    // 1. Estados para los valores de los campos
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 2. Estados para almacenar los mensajes de error (si son null, no hay error)
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // 3. Función de validación
    fun validateForm(): Boolean {
        var isValid = true

        // Validación del Nombre
        if (name.isBlank()) {
            nameError = "El nombre no puede estar vacío"
            isValid = false
        } else {
            nameError = null
        }

        // Validación del Correo Electrónico
        if (email.isBlank()) {
            emailError = "El correo no puede estar vacío"
            isValid = false
            // Patterns.EMAIL_ADDRESS es una herramienta nativa de Android que valida que tenga "@" y un dominio válido
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Ingresa un correo electrónico válido"
            isValid = false
        } else {
            emailError = null
        }

        // Validación de la Contraseña
        if (password.isBlank()) {
            passwordError = "La contraseña no puede estar vacía"
            isValid = false
        } else if (password.length <= 8) {
            passwordError = "La contraseña debe ser superior a 8 caracteres"
            isValid = false
        } else if (!password.any { it.isUpperCase() }) {
            passwordError = "Debe incluir al menos una letra mayúscula"
            isValid = false
        } else if (!password.any { !it.isLetterOrDigit() }) {
            passwordError = "Debe incluir al menos un carácter especial (ej: !@#$%)"
            isValid = false
        } else {
            passwordError = null
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
        Text(
            text = "Crear Cuenta",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = DaterraPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo: Nombre
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = null // Limpia el error cuando el usuario empieza a escribir
            },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = nameError != null,
            supportingText = {
                if (nameError != null) {
                    Text(text = nameError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo: Correo Electrónico
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo: Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Registro
        Button(
            onClick = {
                // Si la validación es exitosa, navega a la siguiente pantalla
                if (validateForm()) {
                    onNavigateToMain()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DaterraPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Registrarse", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Ya tienes cuenta? Inicia Sesión",
            color = DaterraSecundary,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}