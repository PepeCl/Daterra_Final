package com.example.daterra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daterra.data.model.PuntoReciclaje
import com.example.daterra.data.remote.api.RetrofitClient // Asegúrate de que esta ruta coincida
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    // Lista con TODOS los puntos cargados desde la API (sin filtrar por distancia todavía)
    private val _puntosBase = MutableStateFlow<List<PuntoReciclaje>>(emptyList())

    // NUEVO: Estado reactivo para la ubicación real del usuario.
    // Inicia en Santiago Centro por defecto mientras el GPS consigue la señal real.
    private val _ubicacionUsuario = MutableStateFlow(LatLng(-33.4482, -70.6693))
    val ubicacionUsuario: StateFlow<LatLng> = _ubicacionUsuario.asStateFlow()

    // Filtro de material seleccionado ("Todos", "Plástico", "Vidrio", etc.)
    private val _filtroSeleccionado = MutableStateFlow("Todos")
    val filtroSeleccionado: StateFlow<String> = _filtroSeleccionado.asStateFlow()

    // Estado para almacenar el punto más cercano absoluto (para la tarjeta del Home)
    private val _puntoMasCercano = MutableStateFlow<PuntoReciclaje?>(null)
    val puntoMasCercano: StateFlow<PuntoReciclaje?> = _puntoMasCercano.asStateFlow()

    // ARQUITECTURA REACTIVA: Filtra automáticamente por distancia GPS y material en tiempo real
    val puntosFiltrados: StateFlow<List<PuntoReciclaje>> = combine(
        _puntosBase, _filtroSeleccionado, _ubicacionUsuario
    ) { listaPuntos, filtro, ubicacion ->

        // Radio de 500 metros (0.5 km)
        val radioMaximoKm = 0.5

        // 1. Filtramos primero por cercanía al GPS actual del usuario
        val cercanos = listaPuntos.filter { punto ->
            calcularDistanciaKm(ubicacion.latitude, ubicacion.longitude, punto.latitude, punto.longitude) <= radioMaximoKm
        }

        // 2. Calculamos cuál es el punto más cercano de todos para enviarlo a la pantalla de inicio
        _puntoMasCercano.value = cercanos.minByOrNull { punto ->
            calcularDistanciaKm(ubicacion.latitude, ubicacion.longitude, punto.latitude, punto.longitude)
        }

        // 3. Finalmente aplicamos el filtro de materiales de la vista expandida
        if (filtro == "Todos") {
            cercanos
        } else {
            cercanos.filter { it.materials.contains(filtro) }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        cargarPuntosDesdeApi()
    }

    // NUEVA FUNCIÓN: Permite a la Vista inyectar las coordenadas reales capturadas por el sensor
    fun actualizarUbicacionActual(nuevaUbicacion: LatLng) {
        _ubicacionUsuario.value = nuevaUbicacion
    }

    private fun cargarPuntosDesdeApi() {
        viewModelScope.launch {
            try {
                // 1. Descargamos la data cruda
                val puntosDelGobierno = RetrofitClient.instance.getPuntosLimpios()

                // 2. Diccionario de traducción
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

                // 3. Mapeamos la data conservando TODOS los puntos que tengan coordenadas válidas
                val puntosTraducidos = puntosDelGobierno.mapNotNull { apiPoint ->
                    val lat = apiPoint.latitud?.toDoubleOrNull() ?: return@mapNotNull null
                    val lng = apiPoint.longitud?.toDoubleOrNull() ?: return@mapNotNull null

                    val calle = apiPoint.nombreDireccion ?: "Dirección desconocida"
                    val numero = apiPoint.numeroDireccion ?: "S/N"
                    val comuna = apiPoint.comuna?.nombre ?: ""
                    val direccionCompleta = "$calle $numero, $comuna"

                    val materialesEnEspanol = apiPoint.materialesApi?.mapNotNull { materialIngles ->
                        traductorMateriales[materialIngles]
                    } ?: emptyList()

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

                // 4. Guardamos todo en la base. El 'combine' se encargará de hacer el recorte a 500m.
                _puntosBase.value = puntosTraducidos

            } catch (e: Exception) {
                android.util.Log.e("API_MMA", "Error al cargar la API del MMA: ${e.message}")
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