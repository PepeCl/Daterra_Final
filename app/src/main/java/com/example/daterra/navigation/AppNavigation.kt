package com.example.daterra.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daterra.ui.screens.home.MainScreen
import com.example.daterra.ui.screens.learn.AprenderScreen
import com.example.daterra.ui.screens.profile.PerfilScreen
import com.example.daterra.ui.screens.auth.LoginScreen // Asegúrate de importar tus pantallas
import com.example.daterra.ui.screens.auth.RegisterScreen
import com.example.daterra.ui.viewmodel.MapViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mapViewModel: MapViewModel = viewModel()

    NavHost(
        navController = navController,
        // AQUÍ ESTÁ LA MAGIA: Cambiamos el punto de partida al Login
        startDestination = Screen.Login.route
    ) {
        // --- RUTAS DE AUTENTICACIÓN ---

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("registro") // O usa Screen.Registro.route si lo creaste
                },
                onNavigateToMain = {
                    // Al entrar con éxito, borramos el Login del historial para no poder volver atrás con el botón del celular
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Si creaste un "object Registro : Screen("registro")" en tu Screen.kt, úsalo aquí.
        // Si no, puedes usar directamente el String "registro".
        composable("registro") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Inicio.route) {
                        // Borramos todo el historial de registro y login al entrar a la app
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- RUTAS PRINCIPALES DE LA APP ---

        composable(Screen.Inicio.route) {
            MainScreen(
                navController = navController,
                mapViewModel = mapViewModel,
                onLogout = {
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