package com.example.daterra.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.daterra.ui.screens.home.DaterraBottomNavigation
import com.example.daterra.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull

// IMPORTA TU TOKEN MANAGER
import com.example.daterra.data.local.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController, onLogout: () -> Unit) {
    // 1. INICIALIZAMOS EL TOKEN MANAGER
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope() // Para lanzar la función de borrar sesión

    // 2. LEEMOS LOS DATOS GUARDADOS (Si no hay nada, mostramos "Cargando...")
    val nombreGuardado by tokenManager.getNombre.collectAsState(initial = "Cargando...")
    val correoGuardado by tokenManager.getCorreo.collectAsState(initial = "Cargando...")
    val comunaGuardada by tokenManager.getComuna.collectAsState(initial = "Cargando...")

    var isEditing by remember { mutableStateOf(false) }

    // Usamos los datos guardados como valor inicial de los TextFields
    var nombreInput by remember(nombreGuardado) { mutableStateOf(nombreGuardado ?: "") }
    var correoInput by remember(correoGuardado) { mutableStateOf(correoGuardado ?: "") }
    var comunaInput by remember(comunaGuardada) { mutableStateOf(comunaGuardada ?: "") }

    Scaffold(
        containerColor = DaterraBackground,
        topBar = { PerfilHeader() },
        bottomBar = { DaterraBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Datos Personales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DaterraText
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreInput,
                onValueChange = { nombreInput = it },
                label = { Text("Nombre") },
                enabled = isEditing,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = correoInput,
                onValueChange = { correoInput = it },
                label = { Text("Correo Electrónico") },
                enabled = isEditing,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comunaInput,
                onValueChange = { comunaInput = it },
                label = { Text("Comuna") },
                enabled = isEditing,
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.DarkGray,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTONES DE ACCIÓN
            if (isEditing) {
                Button(
                    onClick = {
                        // AQUÍ GUARDAMOS LOS NUEVOS DATOS LOCALMENTE
                        coroutineScope.launch {
                            // CORRECCIÓN: Usamos firstOrNull() en vez de .value
                            val tokenActual = tokenManager.getToken.firstOrNull() ?: ""
                            tokenManager.saveTokenAndData(tokenActual, nombreInput, correoInput, comunaInput)
                        }
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DaterraPrimary)
                ) {
                    Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        // Si cancela, restauramos los valores originales
                        nombreInput = nombreGuardado ?: ""
                        correoInput = correoGuardado ?: ""
                        comunaInput = comunaGuardada ?: ""
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Cancelar", color = DaterraText)
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DaterraSecundary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                // 3. BOTÓN DE CERRAR SESIÓN CON LÓGICA DE BORRADO
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            // Borramos el token y todos los datos del usuario del teléfono
                            tokenManager.clearSession()
                            // Navegamos al Login (llamando al callback que configuraste en AppNavigation)
                            onLogout()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { /* Lógica para eliminar cuenta en BD y luego llamar a onLogout() */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Cuenta", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PerfilHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF68DDBD), Color(0xFF4CB89B))
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi Perfil",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}