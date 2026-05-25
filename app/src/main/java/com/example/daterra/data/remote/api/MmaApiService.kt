package com.example.daterra.data.remote.api
import com.example.daterra.data.remote.dto.MmaApiPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MmaApiService {
    @GET("api/points")
    suspend fun getPuntosLimpios(): List<MmaApiPoint> // Descarga la lista de forma asíncrona
}

// Configuración del cliente Retrofit
object RetrofitClient {
    private const val BASE_URL = "https://puntoslimpios.mma.gob.cl/"

    val instance: MmaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MmaApiService::class.java)
    }
}