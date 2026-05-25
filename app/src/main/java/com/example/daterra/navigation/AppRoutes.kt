package com.example.daterra.navigation

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Aprender : Screen("aprender")
    object Perfil : Screen("perfil")
    object Login : Screen("login")
}