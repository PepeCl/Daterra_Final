package com.example.daterra.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daterra.ui.theme.*
import com.example.daterra.ui.viewmodel.AuthState
import com.example.daterra.ui.viewmodel.AuthViewModel
import com.example.daterra.ui.viewmodel.UserRegisterRequest

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    authViewModel: AuthViewModel = viewModel() // Inyectamos el ViewModel
) {
    // Escuchamos el estado del ViewModel (Idle, Loading, Success, Error)
    val authState by authViewModel.authState.collectAsState()

    // 1. Estados para los valores (11 campos)
    var rut by remember { mutableStateOf("") }
    var dv by remember { mutableStateOf("") }
    var primerNombre by remember { mutableStateOf("") }
    var segundoNombre by remember { mutableStateOf("") }
    var primerApellido by remember { mutableStateOf("") }
    var segundoApellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 2. Estados para errores
    var rutError by remember { mutableStateOf<String?>(null) }
    var dvError by remember { mutableStateOf<String?>(null) }
    var pNombreError by remember { mutableStateOf<String?>(null) }
    var pApellidoError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var direccionError by remember { mutableStateOf<String?>(null) }
    var comunaError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Regex para validar solo letras y espacios
    val soloLetrasRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$".toRegex()

    // Reaccionamos automáticamente si el estado cambia a "Success"
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToMain() // Viaja a la pantalla principal
            authViewModel.resetState() // Limpia el estado para futuras visitas
        }
    }

    // 3. Función de validación maestra
    fun validateForm(): Boolean {
        var isValid = true

        if (rut.isBlank() || !rut.all { it.isDigit() }) {
            rutError = "RUT inválido"
            isValid = false
        } else { rutError = null }

        val validDv = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "k", "K")
        if (dv.isBlank() || !validDv.contains(dv)) {
            dvError = "Error"
            isValid = false
        } else { dvError = null }

        if (primerNombre.isBlank() || !primerNombre.matches(soloLetrasRegex)) {
            pNombreError = "Requerido, solo letras"
            isValid = false
        } else { pNombreError = null }

        if (primerApellido.isBlank() || !primerApellido.matches(soloLetrasRegex)) {
            pApellidoError = "Requerido, solo letras"
            isValid = false
        } else { pApellidoError = null }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Ej: correo@ejemplo.com"
            isValid = false
        } else { emailError = null }

        if (direccion.isBlank()) {
            direccionError = "Ingresa calle y número"
            isValid = false
        } else { direccionError = null }

        if (comuna.isBlank()) {
            comunaError = "Ingresa tu comuna"
            isValid = false
        } else { comunaError = null }

        if (telefono.length != 9 || !telefono.all { it.isDigit() }) {
            telefonoError = "Debe tener 9 números (Ej: 987654321)"
            isValid = false
        } else { telefonoError = null }

        if (password.isBlank() || password.length <= 8) {
            passwordError = "Mínimo 9 caracteres"
            isValid = false
        } else if (!password.any { it.isUpperCase() }) {
            passwordError = "Falta una mayúscula"
            isValid = false
        } else if (!password.any { !it.isLetterOrDigit() }) {
            passwordError = "Falta carácter especial (!@#...)"
            isValid = false
        } else { passwordError = null }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DaterraBackground)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DaterraPrimary)
        Spacer(modifier = Modifier.height(24.dp))

        // --- FILA 1: RUT Y DV ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = rut,
                onValueChange = { if (it.all { char -> char.isDigit() }) { rut = it; rutError = null } },
                label = { Text("RUT (Sin puntos)") },
                modifier = Modifier.weight(0.7f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                isError = rutError != null,
                supportingText = { if (rutError != null) Text(rutError!!, color = MaterialTheme.colorScheme.error) }
            )
            OutlinedTextField(
                value = dv,
                onValueChange = { if (it.length <= 1) { dv = it.uppercase(); dvError = null } },
                label = { Text("DV") },
                modifier = Modifier.weight(0.3f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
                isError = dvError != null,
                supportingText = { if (dvError != null) Text(dvError!!, color = MaterialTheme.colorScheme.error) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- FILA 2: NOMBRES ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = primerNombre,
                onValueChange = { primerNombre = it; pNombreError = null },
                label = { Text("1er Nombre") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                isError = pNombreError != null,
                supportingText = { if (pNombreError != null) Text(pNombreError!!, color = MaterialTheme.colorScheme.error) }
            )
            OutlinedTextField(
                value = segundoNombre,
                onValueChange = { segundoNombre = it },
                label = { Text("2do Nombre (Opc)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- FILA 3: APELLIDOS ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = primerApellido,
                onValueChange = { primerApellido = it; pApellidoError = null },
                label = { Text("1er Apellido") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                isError = pApellidoError != null,
                supportingText = { if (pApellidoError != null) Text(pApellidoError!!, color = MaterialTheme.colorScheme.error) }
            )
            OutlinedTextField(
                value = segundoApellido,
                onValueChange = { segundoApellido = it },
                label = { Text("2do Apellido (Opc)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- EMAIL ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            isError = emailError != null,
            supportingText = { if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- FILA 4: UBICACIÓN ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it; direccionError = null },
                label = { Text("Calle y número") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                isError = direccionError != null,
                supportingText = { if (direccionError != null) Text(direccionError!!, color = MaterialTheme.colorScheme.error) }
            )
            OutlinedTextField(
                value = comuna,
                onValueChange = { comuna = it; comunaError = null },
                label = { Text("Comuna") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                isError = comunaError != null,
                supportingText = { if (comunaError != null) Text(comunaError!!, color = MaterialTheme.colorScheme.error) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- TELÉFONO ---
        OutlinedTextField(
            value = telefono,
            onValueChange = { if (it.length <= 9 && it.all { char -> char.isDigit() }) { telefono = it; telefonoError = null } },
            label = { Text("Teléfono (9 dígitos)") },
            prefix = { Text("+56 ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            isError = telefonoError != null,
            supportingText = { if (telefonoError != null) Text(telefonoError!!, color = MaterialTheme.colorScheme.error) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- CONTRASEÑA ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            isError = passwordError != null,
            supportingText = { if (passwordError != null) Text(passwordError!!, color = MaterialTheme.colorScheme.error) }
        )

        // Mostrar mensaje de error general desde el ViewModel (si existe)
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTÓN REGISTRO ---
        Button(
            onClick = {
                if (validateForm()) {
                    val newUser = UserRegisterRequest(
                        rut = rut,
                        dv = dv,
                        primerNombre = primerNombre,
                        segundoNombre = segundoNombre,
                        primerApellido = primerApellido,
                        segundoApellido = segundoApellido,
                        email = email,
                        direccion = direccion,
                        comuna = comuna,
                        telefono = telefono,
                        contrasena = password
                    )
                    authViewModel.registerUser(newUser)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DaterraPrimary),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Registrarse", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Ya tienes cuenta? Inicia Sesión",
            color = DaterraSecundary,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}