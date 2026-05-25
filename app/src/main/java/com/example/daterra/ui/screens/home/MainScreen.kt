package com.example.daterra.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.daterra.R
import com.example.daterra.navigation.Screen
import com.example.daterra.ui.theme.*
import com.example.daterra.data.model.ProductoPrioritario
import com.example.daterra.data.model.PuntoReciclaje
import com.example.daterra.ui.viewmodel.MapViewModel
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng

@Composable
fun MainScreen(
    navController: NavController,
    onLogout: () -> Unit,
    mapViewModel: MapViewModel,
    nombreUsuario: String = "Giuseppe"
) {
    var isMapExpanded by remember { mutableStateOf(false) }

    // Escuchamos los puntos desde el ViewModel
    val puntos by mapViewModel.puntosFiltrados.collectAsState()

    if (isMapExpanded) {
        // Le pasamos el ViewModel a la pantalla expandida
        ExpandedMapScreen(mapViewModel = mapViewModel, onBack = { isMapExpanded = false })
    } else {
        HomeScreenContainer(
            navController = navController,
            onExpandMap = { isMapExpanded = true },
            onLogout = onLogout,
            nombreUsuario = nombreUsuario,
            puntos = puntos // Le pasamos los puntos al contenedor
        )
    }
}

@Composable
fun HomeScreenContainer(
    navController: NavController,
    onExpandMap: () -> Unit,
    onLogout: () -> Unit,
    nombreUsuario: String,
    puntos: List<PuntoReciclaje> // Recibe los puntos
) {
    Scaffold(
        topBar = { HeaderSection() },
        bottomBar = { DaterraBottomNavigation(navController = navController) },
        containerColor = DaterraBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "¡Bienvenido, $nombreUsuario!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaterraText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prepárate para cambiar el mundo \uD83C\uDF3F",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            // Llamamos al mapa real y le pasamos los puntos y la acción de expandir
            MapaRealSection(puntos = puntos, onExpandMap = onExpandMap)

            ElegantDivider()
            EstadoTerritorioSection()

            ElegantDivider()
            QueReciclarHoySection()

            ElegantDivider()
            PuntoDestacadoSection()

            ElegantDivider()
            TendenciasSection()

            ElegantDivider()
            ProductosPrioritariosSection()

            ElegantDivider()
            IndiceDaterraSection()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ElegantDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
        thickness = 1.dp,
        color = Color.LightGray.copy(alpha = 0.6f)
    )
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF68DDBD),
                        Color(0xFF4CB89B)
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo_daterra),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Daterra",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MapaRealSection(puntos: List<PuntoReciclaje>, onExpandMap: () -> Unit) {
    val santiagoCentro = LatLng(-33.4489, -70.6693)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(santiagoCentro, 11f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            puntos.forEach { punto ->
                if(punto.latitude != 0.0 && punto.longitude != 0.0) {
                    Marker(
                        state = MarkerState(position = LatLng(punto.latitude, punto.longitude)),
                        title = punto.name,
                        snippet = punto.address
                    )
                }
            }
        }

        // Botón para expandir el mapa
        IconButton(
            onClick = onExpandMap,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(Color.White, CircleShape)
                .size(36.dp)
                .shadow(2.dp, CircleShape)
        ) {
            Icon(Icons.Default.OpenInFull, contentDescription = "Expandir", modifier = Modifier.size(20.dp), tint = Color.DarkGray)
        }
    }
}

@Composable
fun EstadoTerritorioSection() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Estado del territorio", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DaterraText)
            Text("Santiago Centro", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    CircularProgressIndicator(progress = 0.78f, modifier = Modifier.size(80.dp), color = DaterraPrimary, strokeWidth = 8.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("78%", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    EstadoItem(Icons.Default.TrendingUp, "+12 ton", "Valorizadas este mes")
                    Spacer(modifier = Modifier.height(8.dp))
                    EstadoItem(Icons.Default.LocationOn, "92", "Puntos registrados")
                    Spacer(modifier = Modifier.height(8.dp))
                    EstadoItem(Icons.Default.Home, "5", "Comunas mejorando")
                }
            }
        }
    }
}

@Composable
fun EstadoItem(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = DaterraPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QueReciclarHoySection() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Qué reciclar hoy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DaterraText)
            Text("Ver más", fontSize = 14.sp, color = DaterraPrimary, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RecycleCard("Botellas PET", "Limpias y sin tapa", Icons.Default.WaterDrop, Color(0xFFE8F5E9), Modifier.weight(1f))
            RecycleCard("Vidrio", "Frascos y botellas", Icons.Default.Inventory2, Color(0xFFE8F5E9), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RecycleCard("Cartón", "Seco y doblado", Icons.Default.Drafts, Color(0xFFFFF3E0), Modifier.weight(1f))
            RecycleCard("No reciclar", "Cerámicas, espejo...", Icons.Default.Close, Color(0xFFFFEBEE), Modifier.weight(1f), isError = true)
        }
    }
}

@Composable
fun RecycleCard(title: String, subtitle: String, icon: ImageVector, bgColor: Color, modifier: Modifier = Modifier, isError: Boolean = false) {
    Card(colors = CardDefaults.cardColors(containerColor = bgColor), modifier = modifier) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (isError) Color.Red else DaterraPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isError) Color.Red else DaterraText)
                Text(subtitle, fontSize = 11.sp, color = if (isError) Color.Red.copy(alpha = 0.7f) else Color.Gray)
            }
        }
    }
}

@Composable
fun PuntoDestacadoSection() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Punto limpio destacado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DaterraText)
            Text("Ver todos", fontSize = 14.sp, color = DaterraPrimary, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Punto Verde Providencia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Abierto hasta 19:00", color = DaterraPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Recibe: Plástico, Vidrio, Cartón...", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(progress = 0.92f, color = DaterraPrimary, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun TendenciasSection() {
    Column {
        Text("Tendencias del mes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DaterraText)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TendenciaCard("+18%", "Plástico", true, Modifier.weight(1f))
            TendenciaCard("-7%", "Vidrio", false, Modifier.weight(1f))
            TendenciaCard("+23%", "Electrónicos", true, Modifier.weight(1f))
        }
    }
}

@Composable
fun TendenciaCard(valor: String, material: String, positivo: Boolean, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(if (positivo) Icons.Default.TrendingUp else Icons.Default.TrendingDown, contentDescription = null, tint = if (positivo) DaterraPrimary else Color.Red)
            Text(valor, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (positivo) DaterraPrimary else Color.Red)
            Text(material, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProductosPrioritariosSection() {
    val context = LocalContext.current

    val productos = listOf(
        ProductoPrioritario("Neumáticos", Icons.Outlined.DonutLarge, "https://economiacircular.mma.gob.cl/neumaticos/"),
        ProductoPrioritario("Envases y Embalajes", Icons.Outlined.Inventory2, "http://economiacircular.mma.gob.cl/envases-y-embalajes/"),
        ProductoPrioritario("Aceite Lubricante", Icons.Outlined.WaterDrop, "https://economiacircular.mma.gob.cl/aceites-lubricantes/"),
        ProductoPrioritario("Textiles", Icons.Outlined.Checkroom, "https://economiacircular.mma.gob.cl/textiles/estrategia/"),
        ProductoPrioritario("Aparatos eléctricos y electrónicos", Icons.Outlined.Devices, "https://economiacircular.mma.gob.cl/aparatos-electricos-y-electronicos/"),
        ProductoPrioritario("Baterías", Icons.Outlined.BatteryStd, "https://economiacircular.mma.gob.cl/baterias/")
    )

    Column {
        Text(
            text = "Productos Prioritarios",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DaterraText
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productos) { producto ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(90.dp)
                        .clickable {
                            val uri = Uri.parse(producto.url)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = producto.icono,
                            contentDescription = producto.nombre,
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF1B365D)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = producto.nombre,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DaterraText,
                        textAlign = TextAlign.Center,
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
fun IndiceDaterraSection() {
    Column {
        Text("Índice Daterra™", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DaterraText)
        Spacer(modifier = Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = DaterraPrimary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("82/100", fontWeight = FontWeight.Bold, fontSize = 32.sp)
                    Text("Índice de gestión del reciclaje", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DaterraBottomNavigation(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == Screen.Inicio.route,
            onClick = {
                if (currentRoute != Screen.Inicio.route) {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.Inicio.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navegar a Mapa */ },
            icon = { Icon(Icons.Outlined.Map, contentDescription = "Mapa") },
            label = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navegar a Reportes */ },
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Reportes") },
            label = { Text("Reportes") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Aprender.route,
            onClick = {
                if (currentRoute != Screen.Aprender.route) {
                    navController.navigate(Screen.Aprender.route) {
                        popUpTo(Screen.Inicio.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Outlined.Book, contentDescription = "Aprender") },
            label = { Text("Aprender") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Perfil.route,
            onClick = {
                if (currentRoute != Screen.Perfil.route) {
                    navController.navigate(Screen.Perfil.route) {
                        popUpTo(Screen.Inicio.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}

// =========================================================================
// PANTALLA DE MAPA EXPANDIDO (CONECTADA AL VIEWMODEL Y GOOGLE MAPS)
// =========================================================================

@Composable
fun ExpandedMapScreen(mapViewModel: MapViewModel, onBack: () -> Unit) {
    val puntos by mapViewModel.puntosFiltrados.collectAsState()
    val filtroSeleccionado by mapViewModel.filtroSeleccionado.collectAsState()
    var puntoSeleccionado by remember(puntos) { mutableStateOf(puntos.firstOrNull()) }
    val categoriasFiltro = listOf("Todos", "Plástico", "Vidrio", "Cartón", "Electrónicos")

    val santiagoCentro = LatLng(-33.4489, -70.6693)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(santiagoCentro, 11f)
    }

    Column(modifier = Modifier.fillMaxSize().background(DaterraBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text("Mapa del territorio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { /* Filtros avanzados */ }) {
                Icon(Icons.Outlined.Tune, contentDescription = "Filtros")
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoriasFiltro) { categoria ->
                FilterChip(
                    selected = filtroSeleccionado == categoria,
                    onClick = { mapViewModel.cambiarFiltro(categoria) },
                    label = { Text(categoria) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DaterraPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFFE0E0E0))) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                puntos.forEach { punto ->
                    if (punto.latitude != 0.0 && punto.longitude != 0.0) {
                        Marker(
                            state = MarkerState(position = LatLng(punto.latitude, punto.longitude)),
                            title = punto.name,
                            snippet = punto.address,
                            onClick = {
                                puntoSeleccionado = punto
                                false // Devuelve false para que la cámara y el título sigan funcionando por defecto
                            }
                        )
                    }
                }
            }

            puntoSeleccionado?.let { punto ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.LightGray).align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(punto.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DaterraText)
                        Text(
                            text = if (punto.isOpen) "Abierto hasta ${punto.closingTime}" else "Cerrado",
                            color = if (punto.isOpen) DaterraPrimary else Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Dirección: ${punto.address}", fontSize = 12.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Recibe: ${punto.materials.joinToString(", ")}", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(onClick = { /* Navegar a Google Maps app */ }, colors = ButtonDefaults.buttonColors(containerColor = DaterraPrimary)) {
                                Text("Cómo llegar")
                            }
                        }
                    }
                }
            }
        }
    }
}