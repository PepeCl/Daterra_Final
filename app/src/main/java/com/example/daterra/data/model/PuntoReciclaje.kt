package com.example.daterra.data.model

// El modelo limpio y en español que usará tu pantalla
data class PuntoReciclaje(
    val id: String,
    val name: String,
    val address: String,
    val isOpen: Boolean,
    val materials: List<String>,
    val latitude: Double,
    val longitude: Double,

    // Datos simulados para el MVP (ya que la API no los provee)
    val closingTime: String = "S/I",
    val occupancyProgress: Float = 0.5f
)