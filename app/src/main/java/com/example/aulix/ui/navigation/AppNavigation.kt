package com.example.aulix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

// Rutas tipadas con Kotlin Serialization
sealed interface Route {
    @Serializable object Auth : Route

    // Docente
    @Serializable object DocenteDashboard : Route
    @Serializable object DocenteSesion : Route
    @Serializable object DocenteAsistencia : Route
    @Serializable object DocenteIncidencias : Route
    @Serializable object DocenteEvidencias : Route
    @Serializable object DocenteAgenda : Route

    // Estudiante
    @Serializable object EstudianteDashboard : Route
    @Serializable object EstudianteAsistencia : Route

    // Auxiliar
    @Serializable object AuxiliarDashboard : Route
    @Serializable object AuxiliarPrestamos : Route

    // Soporte
    @Serializable object SoporteDashboard : Route
    @Serializable object SoporteIncidencias : Route
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Route.Auth
    ) {
        // Las rutas se añadirán aquí por módulo
    }
}
