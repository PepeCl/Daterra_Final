package com.example.daterra.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daterra.ui.screens.home.MainScreen
import com.example.daterra.ui.screens.learn.AprenderScreen
import com.example.daterra.ui.screens.profile.PerfilScreen
import com.example.daterra.ui.viewmodel.MapViewModel

@Composable
fun AppNavigation() {
    // El navController es el encargado de gestionar el historial de pantallas
    val navController = rememberNavController()

    // Instanciamos el ViewModel del mapa.
    // Al usar viewModel(), Compose se asegura de que este objeto sobreviva
    // aunque el usuario navegue a otras pantallas y vuelva.
    val mapViewModel: MapViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route // Pantalla inicial del MVP
    ) {
        composable(Screen.Inicio.route) {
            MainScreen(
                navController = navController,
                mapViewModel = mapViewModel, // <-- Le inyectamos el ViewModel a la pantalla
                onLogout = {
                    // Al cerrar sesión, limpiamos el historial para que no puedan volver atrás
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Aprender.route) {
            AprenderScreen(navController = navController)
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }
    }
}