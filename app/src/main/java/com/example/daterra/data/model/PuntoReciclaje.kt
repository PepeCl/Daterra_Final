package com.example.daterra.data.model

data class PuntoReciclaje(
    val id: String,
    val name: String,
    val address: String,
    val isOpen: Boolean,
    val materials: List<String>,
    val latitude: Double,
    val longitude: Double,
    val closingTime: String = "S/I",
    val occupancyProgress: Float = 0.5f
)