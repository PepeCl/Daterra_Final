package com.example.daterra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definimos los posibles estados de la pantalla de autenticación
sealed class AuthState {
    object Idle : AuthState() // Estado inicial, esperando acción
    object Loading : AuthState() // Cargando (se está comunicando con AWS)
    data class Success(val message: String) : AuthState() // Registro exitoso
    data class Error(val error: String) : AuthState() // Falló la conexión o la validación
}

// 2. Definimos el objeto exacto que enviaremos a la API (Backend)
data class UserRegisterRequest(
    val rut: String,
    val dv: String,
    val primerNombre: String,
    val segundoNombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val email: String,
    val direccion: String,
    val comuna: String,
    val telefono: String,
    val contrasena: String
)

class AuthViewModel : ViewModel() {

    // Estado reactivo que la UI (RegisterScreen) estará observando
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun registerUser(userRequest: UserRegisterRequest) {
        // 1. Cambiamos el estado a "Cargando" para mostrar un spinner en la pantalla
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                // TODO: Aquí conectaremos con Retrofit en el siguiente paso.
                // Quedará algo así: val response = RetrofitClient.instance.register(userRequest)

                // SIMULACIÓN: Simulamos una demora de red de 2 segundos
                delay(2000)

                // 2. Si todo sale bien, avisamos que fue un éxito
                _authState.value = AuthState.Success("¡Usuario creado con éxito!")

            } catch (e: Exception) {
                // 3. Si se cae el internet o falla AWS, atrapamos el error
                _authState.value = AuthState.Error(e.message ?: "Error desconocido al contactar al servidor.")
            }
        }
    }

    // Función para reiniciar el estado (útil si el usuario falla y quiere volver a intentar)
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}