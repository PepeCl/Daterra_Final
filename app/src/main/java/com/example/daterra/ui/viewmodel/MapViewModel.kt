package com.example.daterra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daterra.data.model.PuntoReciclaje
import com.example.daterra.data.remote.api.RetrofitClient // Asegúrate de que esta ruta coincida con tu proyecto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    // Lista completa de puntos (Ahora se llenará con la API)
    private val _puntos = MutableStateFlow<List<PuntoReciclaje>>(emptyList())

    // Filtro de material seleccionado ("Todos", "Plástico", "Vidrio", etc.)
    private val _filtroSeleccionado = MutableStateFlow("Todos")
    val filtroSeleccionado: StateFlow<String> = _filtroSeleccionado.asStateFlow()

    // Flujo combinado: Filtra automáticamente los puntos cada vez que cambia el filtro o la lista.
    // Usamos stateIn para convertir el Flow combinado en un StateFlow que la vista pueda observar.
    val puntosFiltrados: StateFlow<List<PuntoReciclaje>> = combine(_puntos, _filtroSeleccionado) { listaPuntos, filtro ->
        if (filtro == "Todos") {
            listaPuntos
        } else {
            listaPuntos.filter { it.materials.contains(filtro) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        cargarPuntosDesdeApi()
    }

    private fun cargarPuntosDesdeApi() {
        // viewModelScope.launch crea un hilo secundario para no congelar la pantalla
        viewModelScope.launch {
            try {
                // 1. Descargamos la data cruda del gobierno
                val puntosDelGobierno = RetrofitClient.instance.getPuntosLimpios()

                // 2. Diccionario de traducción de materiales
                val traductorMateriales = mapOf(
                    "paper" to "Papel",
                    "paperboard" to "Cartón",
                    "cardboard_drink" to "Tetra Pak",
                    "plastic" to "Plástico",
                    "metal" to "Metal",
                    "glass" to "Vidrio",
                    "power_bank" to "Baterías",
                    "phone" to "Celulares"
                )

                // 3. Transformación (Mapeo)
                val latitudCentro = -33.4569
                val longitudCentro = -70.5982
                val radioMaximoKm = 5.0 // Límite de 5 kilómetros a la redonda

                // mapNotNull permite transformar el objeto y al mismo tiempo descartar (filtrar) los nulos
                val puntosTraducidos = puntosDelGobierno.mapNotNull { apiPoint ->

                    val lat = apiPoint.latitud?.toDoubleOrNull() ?: return@mapNotNull null
                    val lng = apiPoint.longitud?.toDoubleOrNull() ?: return@mapNotNull null

                    // Evaluamos la distancia. Si supera los 5km, devolvemos null para que no entre a la lista final
                    if (calcularDistanciaKm(latitudCentro, longitudCentro, lat, lng) > radioMaximoKm) {
                        return@mapNotNull null
                    }

                    val calle = apiPoint.nombreDireccion ?: "Dirección desconocida"
                    val numero = apiPoint.numeroDireccion ?: "S/N"
                    val comuna = apiPoint.comuna?.nombre ?: ""
                    val direccionCompleta = "$calle $numero, $comuna"

                    val materialesEnEspanol = apiPoint.materialesApi?.mapNotNull { materialIngles ->
                        traductorMateriales[materialIngles]
                    } ?: emptyList()

                    // Solo los puntos cercanos llegan hasta aquí y se construyen
                    PuntoReciclaje(
                        id = java.util.UUID.randomUUID().toString(),
                        name = apiPoint.propietario ?: "Punto de Reciclaje",
                        address = direccionCompleta,
                        isOpen = apiPoint.estado == "open",
                        materials = materialesEnEspanol,
                        latitude = lat,
                        longitude = lng,
                        closingTime = "S/I",
                        occupancyProgress = 0.5f
                    )
                }

                // 4. Se los pasamos a la Vista
                _puntos.value = puntosTraducidos

            } catch (e: Exception) {
                // Aquí se atrapan los errores (ej: falta de internet)
                println("Error al cargar la API del MMA: ${e.message}")
            }
        }
    }

    fun cambiarFiltro(nuevoFiltro: String) {
        _filtroSeleccionado.value = nuevoFiltro
    }

    private fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radioTierra * c
    }
}