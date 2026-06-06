package com.example.aulix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.auth.AuthViewModel
import com.example.aulix.ui.auth.LoginScreen
import com.example.aulix.ui.auth.RegisterScreen
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable object Login : Route
    @Serializable object Register : Route

    // Dashboards por rol
    @Serializable object DocenteDashboard : Route
    @Serializable object EstudianteDashboard : Route
    @Serializable object AuxiliarDashboard : Route
    @Serializable object SoporteDashboard : Route

    // Docente — flujos
    @Serializable object DocenteSesion : Route
    @Serializable object DocenteAsistencia : Route
    @Serializable object DocenteIncidencias : Route
    @Serializable object DocenteEvidencias : Route
    @Serializable object DocenteAgenda : Route

    // Estudiante
    @Serializable object EstudianteAsistencia : Route

    // Auxiliar
    @Serializable object AuxiliarPrestamos : Route

    // Soporte
    @Serializable object SoporteIncidencias : Route
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Route.Login) {

        composable<Route.Login> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    val destination: Route = when (user.role) {
                        UserRole.DOCENTE         -> Route.DocenteDashboard
                        UserRole.ESTUDIANTE      -> Route.EstudianteDashboard
                        UserRole.AUXILIAR        -> Route.AuxiliarDashboard
                        UserRole.SOPORTE_TECNICO -> Route.SoporteDashboard
                    }
                    navController.navigate(destination) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Route.Register) },
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { user ->
                    val destination: Route = when (user.role) {
                        UserRole.DOCENTE         -> Route.DocenteDashboard
                        UserRole.ESTUDIANTE      -> Route.EstudianteDashboard
                        UserRole.AUXILIAR        -> Route.AuxiliarDashboard
                        UserRole.SOPORTE_TECNICO -> Route.SoporteDashboard
                    }
                    navController.navigate(destination) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        // Dashboards — placeholder hasta implementar cada módulo
        composable<Route.DocenteDashboard> {
            RolePlaceholderScreen(role = "Docente", initials = "CG", onLogout = {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
            })
        }
        composable<Route.EstudianteDashboard> {
            RolePlaceholderScreen(role = "Estudiante", initials = "MV", onLogout = {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
            })
        }
        composable<Route.AuxiliarDashboard> {
            RolePlaceholderScreen(role = "Auxiliar", initials = "DM", onLogout = {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
            })
        }
        composable<Route.SoporteDashboard> {
            RolePlaceholderScreen(role = "Soporte Técnico", initials = "JR", onLogout = {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
            })
        }
    }
}
