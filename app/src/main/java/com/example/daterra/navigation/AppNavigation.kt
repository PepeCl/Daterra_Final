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
// IMPORTA EL NUEVO VIEWMODEL
import com.example.daterra.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mapViewModel: MapViewModel = viewModel()

    // 1. INSTANCIA EL AUTHVIEWMODEL AQUÍ
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // --- RUTAS DE AUTENTICACIÓN ---

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel, // 2. PÁSALO AL LOGIN
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
                authViewModel = authViewModel, // 3. PÁSALO TAMBIÉN AL REGISTRO (si ya lo actualizaste para recibirlo)
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