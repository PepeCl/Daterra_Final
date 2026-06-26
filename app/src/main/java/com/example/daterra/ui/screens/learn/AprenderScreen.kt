package com.example.daterra.ui.screens.learn

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image // Importación nueva
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.daterra.R
import com.example.daterra.ui.theme.*
import com.example.daterra.ui.screens.home.DaterraBottomNavigation
import com.example.daterra.model.BlogPost


@Composable
fun AprenderScreen(navController: NavController) {
    val blogPosts = listOf(
        BlogPost(
            1, "Guia",
            "Guía de Reciclaje",
            "Guia definitiva sobre reciclaje",
            "7 min",
            "https://santiagorecicla.mma.gob.cl/yo-reciclo/que-reciclo/",
            R.drawable.guiareciclaje
        ),
        BlogPost(
            2, "Vidrio",
            "Proceso del reciclaje de Vidrio",
            "Entérate del proceso que se lleva a cabo para reciclar vidrio. ¿Sabías que no todo el vidrio se recicla?",
            "5 min",
            "https://www.leanpio.com/es/blog/proceso-reciclaje-del-vidrio",
            R.drawable.vidrio
        ),
        BlogPost(
            3, "Guia",
            "Prepara tu reciclaje para que sea retirado",
            "Acá encontrarás lo que necesitas saber para entregar tu reciclaje a los camiones municipales.",
            "5 min",
            "https://www.gob.cl/noticias/guia-pasos-reciclar-tips-reciclaje-camion-ley-rep/",
            R.drawable.quereciclo
        ),
        BlogPost(
            4, "Información",
            "Calculadora de huella de carbono",
            "Calcula tu aporte a la reducción de la huella de carbono al reciclar",
            "3 min",
            "https://www.recyclingtoday.org/es/pages/carbon-emission-reduction-calculator",
            R.drawable.calculadoracarbono
        ),
        BlogPost(
            5, "Información",
            "Desafío Chile sin Basura 2040",
            "Conoce el proyecto Chile sin Basura 2040 y súmate a él !",
            "10 min",
            "https://chilesinbasura.cl/quienes-somos/",
            R.drawable.chilesinbasura
        )
    )

    var selectedCategory by remember { mutableStateOf("Todos") }
    // Actualización de filtros
    val categories = listOf("Todos", "Guia", "Vidrio", "Información")

    Scaffold(
        containerColor = DaterraBackground,
        topBar = { AprenderHeader() },
        bottomBar = { DaterraBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DaterraPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredPosts = if (selectedCategory == "Todos") {
                    blogPosts
                } else {
                    blogPosts.filter { it.category == selectedCategory }
                }

                items(filteredPosts) { post ->
                    BlogCard(post = post)
                }
            }
        }
    }
}

@Composable
fun BlogCard(post: BlogPost) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val uri = Uri.parse(post.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // === IMAGEN NATIVA ===
            Image(
                painter = painterResource(id = post.imageRes),
                contentDescription = "Imagen de ${post.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.category.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaterraSecundary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = post.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaterraText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = post.excerpt,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Fuente",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.readTime,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(20.dp),
                        tint = DaterraPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun AprenderHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF68DDBD), Color(0xFF4CB89B))
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Infórmate",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}