package com.example.daterra.data.remote.api

import com.example.daterra.data.remote.dto.MmaApiPoint
import com.example.daterra.ui.viewmodel.UserRegisterRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// =========================================================
// 1. INTERFAZ GOBIERNO (Puntos Limpios)
// =========================================================
interface MmaApiService {
    @GET("api/points")
    suspend fun getPuntosLimpios(): List<MmaApiPoint>
}

// =========================================================
// 2. INTERFAZ RENDER (Backend de Autenticación)
// =========================================================
// Clases temporales para enviar y recibir datos en el Login
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

interface AuthApiService {
    // CAMBIO 1: Agregamos "api/" directamente en la ruta
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: UserRegisterRequest): Response<Unit>

    // CAMBIO 2: Agregamos "api/" directamente en la ruta
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}

// =========================================================
// 3. CONFIGURACIÓN DE LOS CLIENTES RETROFIT
// =========================================================
object RetrofitClient {

    // --- CLIENTE 1: GOBIERNO ---
    private const val MMA_BASE_URL = "https://puntoslimpios.mma.gob.cl/"

    val instance: MmaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MMA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MmaApiService::class.java)
    }

    // --- CLIENTE 2: NUESTRO BACKEND EN RENDER ---
    // CAMBIO 3: Dejamos solo el dominio principal, borramos el "api/" de aquí
    private const val RENDER_BASE_URL = "https://daterra-75j1.onrender.com/"

    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RENDER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}