package com.example.aulix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.aulix.domain.model.UserRole
import com.example.aulix.domain.session.UserSession
import com.example.aulix.ui.auth.AuthViewModel
import com.example.aulix.ui.auth.LoginScreen
import com.example.aulix.ui.auth.RegisterScreen
import com.example.aulix.ui.auxiliar.historial.HistorialScreen
import com.example.aulix.ui.auxiliar.home.AuxiliarHomeScreen
import com.example.aulix.ui.auxiliar.inventario.InventarioScreen
import com.example.aulix.ui.auxiliar.perfil.PerfilScreen
import com.example.aulix.ui.auxiliar.prestamo.CambiarDestinatarioScreen
import com.example.aulix.ui.auxiliar.prestamo.RegistrarPrestamoScreen
import com.example.aulix.ui.auxiliar.search.SearchEquipoScreen
import com.example.aulix.ui.soporte.dashboard.SoporteHomeScreen
import com.example.aulix.ui.soporte.incidencias.EquipoHistorialScreen
import com.example.aulix.ui.soporte.incidencias.IncidenciaDetailScreen
import com.example.aulix.ui.soporte.incidencias.RegistrarIncidenciaScreen
import com.example.aulix.ui.soporte.mantenimiento.ProgramarMantenimientoScreen
import com.example.aulix.ui.soporte.perfil.SoportePerfilScreen
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
    @Serializable object AuxiliarBuscarEquipo : Route
    @Serializable data class AuxiliarRegistrarPrestamo(val equipoId: String) : Route
    @Serializable object AuxiliarHistorial : Route
    @Serializable object AuxiliarCambiarDestinatario : Route
    @Serializable object AuxiliarInventario : Route
    @Serializable object AuxiliarPerfil : Route

    // Soporte
    @Serializable object SoporteIncidencias : Route
    @Serializable data class SoporteDetalle(val incidenciaId: String) : Route
    @Serializable data class SoporteHistorialEquipo(val equipoId: String) : Route
    @Serializable object SoporteRegistrarIncidencia : Route
    @Serializable object SoportePerfil : Route
    @Serializable object SoporteEquipos : Route
    @Serializable object SoporteMetricas : Route
    @Serializable data class SoporteProgramarMantenimiento(val equipoId: String) : Route
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
                    UserSession.login(user)
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
                    UserSession.login(user)
                    navController.navigate(destination) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        // Dashboards — placeholder hasta implementar cada módulo
        composable<Route.DocenteDashboard> {
            if (UserSession.currentUser == null) {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                return@composable
            }
            RolePlaceholderScreen(role = "Docente", initials = "CG", onLogout = {
                UserSession.logout()
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
            })
        }
        composable<Route.EstudianteDashboard> {
            if (UserSession.currentUser == null) {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                return@composable
            }
            RolePlaceholderScreen(role = "Estudiante", initials = "MV", onLogout = {
                UserSession.logout()
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
            })
        }
        composable<Route.AuxiliarDashboard> {
            val user = UserSession.currentUser
            if (user == null) {
                navController.navigate(Route.Login) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }
            AuxiliarHomeScreen(
                user = user,
                onNuevoPrestamo = { navController.navigate(Route.AuxiliarBuscarEquipo) },
                onVerHistorial = { navController.navigate(Route.AuxiliarHistorial) },
                onVerInventario = { navController.navigate(Route.AuxiliarInventario) },
                onVerPerfil = { navController.navigate(Route.AuxiliarPerfil) },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.AuxiliarBuscarEquipo> {
            SearchEquipoScreen(
                onBack = { navController.popBackStack() },
                onContinuar = { equipoId ->
                    navController.navigate(Route.AuxiliarRegistrarPrestamo(equipoId))
                }
            )
        }
        composable<Route.AuxiliarRegistrarPrestamo> {
            val args = it.toRoute<Route.AuxiliarRegistrarPrestamo>()
            val user = UserSession.currentUser ?: return@composable
            RegistrarPrestamoScreen(
                equipoId = args.equipoId,
                user = user,
                navController = navController,
                onBack = { navController.popBackStack() },
                onCambiarDestinatario = { navController.navigate(Route.AuxiliarCambiarDestinatario) },
                onConfirmado = {
                    navController.navigate(Route.AuxiliarDashboard) {
                        popUpTo(Route.AuxiliarDashboard) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.AuxiliarCambiarDestinatario> {
            CambiarDestinatarioScreen(
                onBack = { navController.popBackStack() },
                onSeleccionar = { destinatario ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("destinatario_nombre", destinatario.nombre)
                    navController.previousBackStackEntry?.savedStateHandle?.set("destinatario_id", destinatario.id)
                    navController.previousBackStackEntry?.savedStateHandle?.set("destinatario_programa", destinatario.programa)
                    navController.popBackStack()
                }
            )
        }
        composable<Route.AuxiliarHistorial> {
            HistorialScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<Route.AuxiliarInventario> {
            InventarioScreen(onBack = { navController.popBackStack() })
        }
        composable<Route.AuxiliarPerfil> {
            PerfilScreen(
                user = UserSession.currentUser,
                onBack = { navController.popBackStack() },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.SoporteDashboard> {
            val user = UserSession.currentUser
            if (user == null) {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                return@composable
            }
            SoporteHomeScreen(
                user = user,
                onVerDetalle = { incidenciaId -> navController.navigate(Route.SoporteDetalle(incidenciaId)) },
                onNuevaIncidencia = { navController.navigate(Route.SoporteRegistrarIncidencia) },
                onVerEquipos = { navController.navigate(Route.SoporteEquipos) },
                onVerMetricas = { navController.navigate(Route.SoporteMetricas) },
                onVerPerfil = { navController.navigate(Route.SoportePerfil) }
            )
        }
        composable<Route.SoporteDetalle> {
            val args = it.toRoute<Route.SoporteDetalle>()
            IncidenciaDetailScreen(
                incidenciaId = args.incidenciaId,
                onBack = { navController.popBackStack() },
                onVerHistorialEquipo = { equipoId -> navController.navigate(Route.SoporteHistorialEquipo(equipoId)) }
            )
        }
        composable<Route.SoporteHistorialEquipo> {
            val args = it.toRoute<Route.SoporteHistorialEquipo>()
            EquipoHistorialScreen(
                equipoId = args.equipoId,
                onBack = { navController.popBackStack() },
                onProgramarMantenimiento = {
                    navController.navigate(Route.SoporteProgramarMantenimiento(args.equipoId))
                }
            )
        }
        composable<Route.SoporteProgramarMantenimiento> {
            val args = it.toRoute<Route.SoporteProgramarMantenimiento>()
            val user = UserSession.currentUser ?: return@composable
            ProgramarMantenimientoScreen(
                equipoId = args.equipoId,
                user = user,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Route.SoporteRegistrarIncidencia> {
            val user = UserSession.currentUser ?: return@composable
            RegistrarIncidenciaScreen(
                user = user,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Route.SoportePerfil> {
            SoportePerfilScreen(
                user = UserSession.currentUser,
                onBack = { navController.popBackStack() },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable<Route.SoporteEquipos> {
            RolePlaceholderScreen(role = "Equipos", initials = "JR", onLogout = {})
        }
        composable<Route.SoporteMetricas> {
            RolePlaceholderScreen(role = "Métricas", initials = "JR", onLogout = {})
        }
    }
}
