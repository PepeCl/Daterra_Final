package com.example.daterra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Importa tu cliente Retrofit y LoginRequest según tu estructura de paquetes
import com.example.daterra.data.remote.api.RetrofitClient
import com.example.daterra.data.remote.api.LoginRequest
// IMPORTANTE: Importamos tu TokenManager
import com.example.daterra.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definimos los posibles estados de la pantalla de autenticación
sealed class AuthState {
    object Idle : AuthState() // Estado inicial, esperando acción
    object Loading : AuthState() // Cargando (se está comunicando con Render)
    data class Success(val message: String) : AuthState() // Registro exitoso
    data class Authenticated(val token: String) : AuthState() // Login exitoso (recibe el Token JWT)
    data class Error(val error: String) : AuthState() // Falló la conexión o la validación
}

// 2. Definimos el objeto exacto que enviaremos a la API para el Registro
data class UserRegisterRequest(
    val email: String,
    val runUsuario: Int, // Cambió a Int
    val dvrunUsuario: String,
    val primerNombre: String,
    val segundoNombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val direccion: String,
    val telefono: String,
    val password: String, // Cambió de contrasena a password
    val idTipoUsu: Int, // Nuevo campo
    val idComuna: Int // Cambió de texto a número
)

class AuthViewModel : ViewModel() {

    // Estado reactivo que la UI estará observando
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // =========================================================
    // FUNCIÓN DE REGISTRO
    // =========================================================
    fun registerUser(userRequest: UserRegisterRequest) {
        // 1. Cambiamos el estado a "Cargando"
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                // 2. Llamada real al backend en Render
                val response = RetrofitClient.authApi.registerUser(userRequest)

                // 3. Evaluamos la respuesta HTTP
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success("¡Usuario creado con éxito!")
                } else {
                    // Si el backend rechaza el registro (ej. HTTP 400 por correo duplicado)
                    _authState.value = AuthState.Error("Error al registrar: Código HTTP ${response.code()}")
                }

            } catch (e: Exception) {
                // 4. Capturamos errores de red (sin internet, servidor caído)
                _authState.value = AuthState.Error("Error de conexión: ${e.message ?: "Desconocido"}")
            }
        }
    }

    // =========================================================
    // FUNCIÓN DE LOGIN
    // =========================================================
    fun loginUser(email: String, contrasena: String, tokenManager: TokenManager) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email = email, password = contrasena)
                val response = RetrofitClient.authApi.loginUser(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val tokenRecibido = response.body()!!.token

                    // AQUÍ ESTÁ LA CORRECCIÓN: Guardamos el token y los datos del perfil
                    tokenManager.saveTokenAndData(
                        token = tokenRecibido,
                        nombre = "Giuseppe Saavedra Contreras", // Si luego actualizas LoginResponse, puedes poner response.body()!!.nombre
                        correo = email,
                        comuna = "Ñuñoa"
                    )

                    _authState.value = AuthState.Authenticated(tokenRecibido)
                } else {
                    _authState.value = AuthState.Error("Credenciales incorrectas: Código HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // Función para reiniciar el estado (útil si el usuario falla y quiere volver a intentar)
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}