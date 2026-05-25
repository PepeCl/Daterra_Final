package com.example.daterra.data.remote.dto
import com.google.gson.annotations.SerializedName

// Molde exacto de lo que responde la API del gobierno
data class MmaApiPoint(
    @SerializedName("lat") val latitud: String?,
    @SerializedName("lng") val longitud: String?,
    @SerializedName("status") val estado: String?, // ej: "open"
    @SerializedName("owner") val propietario: String?,
    @SerializedName("type") val tipo: String?, // ej: "pl" (punto limpio) o "pv" (punto verde)
    @SerializedName("address_name") val nombreDireccion: String?,
    @SerializedName("address_number") val numeroDireccion: String?,
    @SerializedName("commune") val comuna: ComunaDto?,
    @SerializedName("materials") val materialesApi: List<String>?
)

// Sub-objeto que viene dentro del JSON principal
data class ComunaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val nombre: String?
)