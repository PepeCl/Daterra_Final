package com.example.daterra.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.daterra.ui.screens.home.MainScreen
import com.example.daterra.ui.screens.learn.AprenderScreen
import com.example.daterra.ui.screens.profile.PerfilScreen
import com.example.daterra.ui.screens.auth.LoginScreen
import com.example.daterra.ui.screens.auth.RegisterScreen
import com.example.daterra.ui.viewmodel.MapViewModel
import com.example.daterra.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mapViewModel: MapViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // FUNCIÓN CENTRALIZADA PARA CERRAR SESIÓN
    val performLogout = {
        navController.navigate(Screen.Login.route) {
            // Esto asegura que la pila de navegación se vacíe completamente
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // --- RUTAS DE AUTENTICACIÓN ---

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate("registro")
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Inicio.route) {
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
                // Usamos la función centralizada
                onLogout = performLogout
            )
        }

        composable(Screen.Aprender.route) {
            AprenderScreen(navController = navController)
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                navController = navController,
                // Usamos la función centralizada
                onLogout = performLogout
            )
        }
    }
}