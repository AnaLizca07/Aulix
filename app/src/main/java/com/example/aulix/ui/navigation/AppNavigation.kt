package com.example.aulix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.aulix.domain.model.Sesion
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
import com.example.aulix.ui.agenda.EditarClaseScreen
import com.example.aulix.ui.docente.DocenteViewModel
import com.example.aulix.ui.docente.agenda.AgendaScreen
import com.example.aulix.ui.docente.asistencia.AsistenciaQrScreen
import com.example.aulix.ui.docente.asistencia.CodigoTiempoScreen
import com.example.aulix.ui.docente.dashboard.DocenteHomeScreen
import com.example.aulix.ui.docente.evidencias.EvidenciasScreen
import com.example.aulix.ui.docente.incidencias.ReportarIncidenciaScreen
import com.example.aulix.ui.docente.indicadores.IndicadoresDocenteScreen
import com.example.aulix.ui.docente.sesion.CerrarSesionScreen
import com.example.aulix.ui.docente.sesion.ConfirmarAperturaScreen
import com.example.aulix.ui.docente.sesion.DetalleSesionScreen
import com.example.aulix.ui.estudiante.EstudianteViewModel
import com.example.aulix.ui.estudiante.agenda.EstudianteAgendaScreen
import com.example.aulix.ui.estudiante.asistencia.ConfirmacionAsistenciaScreen
import com.example.aulix.ui.estudiante.asistencia.EscanearQrScreen
import com.example.aulix.ui.estudiante.asistencia.IngresarCodigoScreen
import com.example.aulix.ui.estudiante.dashboard.EstudianteHomeScreen
import com.example.aulix.ui.estudiante.historial.EstudianteHistorialScreen
import com.example.aulix.ui.soporte.dashboard.SoporteHomeScreen
import com.example.aulix.ui.soporte.incidencias.EquipoHistorialScreen
import com.example.aulix.ui.soporte.incidencias.IncidenciaDetailScreen
import com.example.aulix.ui.soporte.incidencias.RegistrarIncidenciaScreen
import com.example.aulix.ui.soporte.mantenimiento.ProgramarMantenimientoScreen
import com.example.aulix.ui.soporte.metricas.SoporteMetricasScreen
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
    @Serializable object DocenteSesion : Route            // HU 02 · detalle de sesión
    @Serializable object DocenteConfirmarApertura : Route // HU 02 · confirmar apertura
    @Serializable object DocenteAsistencia : Route        // HU 04 · asistencia QR
    @Serializable object DocenteCodigoTiempo : Route      // HU 05 · código de tiempo
    @Serializable object DocenteIncidencias : Route       // HU 06 · reportar incidencia
    @Serializable object DocenteEvidencias : Route        // HU 07 · evidencias
    @Serializable object DocenteCerrarSesion : Route      // HU 03 · cerrar sesión
    @Serializable object DocenteAgenda : Route            // HU 08 · agenda
    @Serializable data class DocenteEditarClase(val eventoId: String? = null) : Route
    @Serializable object DocenteIndicadores : Route       // indicadores
    @Serializable object DocentePerfil : Route

    // Estudiante
    @Serializable object EstudianteEscanearQr : Route     // HU 10
    @Serializable object EstudianteConfirmacion : Route
    @Serializable object EstudianteIngresarCodigo : Route // HU 11
    @Serializable object EstudianteAgenda : Route
    @Serializable object EstudianteHistorial : Route
    @Serializable object EstudiantePerfil : Route

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
    @Serializable object SoporteMetricas : Route
    @Serializable data class SoporteProgramarMantenimiento(val equipoId: String) : Route
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    // ViewModels compartidos por rol: una sola instancia para que los cambios
    // (abrir/cerrar sesión, editar agenda) se reflejen entre pantallas.
    val docenteVm: DocenteViewModel = hiltViewModel()
    val estudianteVm: EstudianteViewModel = hiltViewModel()

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

        // ════════════════════════════ DOCENTE ════════════════════════════════
        composable<Route.DocenteDashboard> {
            val user = UserSession.currentUser ?: return@composable redirectLogin(navController)
            DocenteHomeScreen(
                user = user,
                viewModel = docenteVm,
                onAbrirSesion = { navController.navigate(Route.DocenteSesion) },
                onGenerarQr = { navController.navigate(Route.DocenteAsistencia) },
                onCodigoTiempo = { navController.navigate(Route.DocenteCodigoTiempo) },
                onReportarIncidencia = { navController.navigate(Route.DocenteIncidencias) },
                onEvidencias = { navController.navigate(Route.DocenteEvidencias) },
                onAgenda = { navController.navigate(Route.DocenteAgenda) },
                onIndicadores = { navController.navigate(Route.DocenteIndicadores) },
                onPerfil = { navController.navigate(Route.DocentePerfil) },
            )
        }

        composable<Route.DocenteSesion> {
            val state by docenteVm.uiState.collectAsState()
            DetalleSesionScreen(
                sesion = state.sesion ?: Sesion(),
                onBack = { navController.popBackStack() },
                onAbrir = { navController.navigate(Route.DocenteConfirmarApertura) },
            )
        }

        composable<Route.DocenteConfirmarApertura> {
            val state by docenteVm.uiState.collectAsState()
            ConfirmarAperturaScreen(
                sesion = state.sesion ?: Sesion(),
                onBack = { navController.popBackStack() },
                onConfirmar = {
                    docenteVm.abrirSesion()
                    navController.navigate(Route.DocenteAsistencia) {
                        popUpTo(Route.DocenteDashboard)
                    }
                },
            )
        }

        composable<Route.DocenteAsistencia> {
            val state by docenteVm.uiState.collectAsState()
            AsistenciaQrScreen(
                sesion = state.sesion ?: Sesion(),
                asistentes = state.asistentes,
                timerSegundos = state.qrTimerSegundos,
                onBack = { navController.popBackStack() },
                onUsarCodigo = { navController.navigate(Route.DocenteCodigoTiempo) },
                onRenovar = { docenteVm.renovarQr() },
                onCerrarAsistencia = { navController.navigate(Route.DocenteCerrarSesion) },
            )
        }

        composable<Route.DocenteCodigoTiempo> {
            val state by docenteVm.uiState.collectAsState()
            CodigoTiempoScreen(
                sesion = state.sesion ?: Sesion(),
                asistentes = state.asistentes,
                timerSegundos = state.qrTimerSegundos,
                onBack = { navController.popBackStack() },
                onRenovar = { docenteVm.renovarQr() },
                onVolverQr = { navController.popBackStack() },
            )
        }

        composable<Route.DocenteIncidencias> {
            val state by docenteVm.uiState.collectAsState()
            ReportarIncidenciaScreen(
                sesion = state.sesion ?: Sesion(),
                onClose = { navController.popBackStack() },
                onEnviar = { navController.popBackStack() },
            )
        }

        composable<Route.DocenteEvidencias> {
            val state by docenteVm.uiState.collectAsState()
            EvidenciasScreen(
                sesion = state.sesion ?: Sesion(),
                evidencias = state.evidencias,
                onBack = { navController.popBackStack() },
                onCapturar = {},
            )
        }

        composable<Route.DocenteCerrarSesion> {
            val state by docenteVm.uiState.collectAsState()
            CerrarSesionScreen(
                sesion = state.sesion ?: Sesion(),
                onBack = { navController.popBackStack() },
                onCerrar = {
                    docenteVm.cerrarSesion()
                    navController.navigate(Route.DocenteDashboard) {
                        popUpTo(Route.DocenteDashboard) { inclusive = true }
                    }
                },
            )
        }

        composable<Route.DocenteAgenda> {
            val state by docenteVm.uiState.collectAsState()
            AgendaScreen(
                eventos = state.agenda,
                laboratorios = state.laboratorios.map { it.nombre },
                onHoy = { navController.navigate(Route.DocenteDashboard) { popUpTo(Route.DocenteDashboard) { inclusive = true } } },
                onIndicadores = { navController.navigate(Route.DocenteIndicadores) },
                onPerfil = { navController.navigate(Route.DocentePerfil) },
                onNuevaClase = { navController.navigate(Route.DocenteEditarClase()) },
                onEditarClase = { id -> navController.navigate(Route.DocenteEditarClase(id)) },
            )
        }

        composable<Route.DocenteEditarClase> {
            val args = it.toRoute<Route.DocenteEditarClase>()
            val state by docenteVm.uiState.collectAsState()
            EditarClaseScreen(
                evento = args.eventoId?.let { id -> state.agenda.find { e -> e.id == id } },
                asignaturas = state.asignaturas,
                laboratorios = state.laboratorios,
                defaultColorHex = 0xFF2C5BA8,
                onBack = { navController.popBackStack() },
                onGuardar = { evento ->
                    docenteVm.guardarEvento(evento)
                    navController.popBackStack()
                },
            )
        }

        composable<Route.DocenteIndicadores> {
            val user = UserSession.currentUser ?: return@composable redirectLogin(navController)
            IndicadoresDocenteScreen(
                user = user,
                onHoy = { navController.navigate(Route.DocenteDashboard) { popUpTo(Route.DocenteDashboard) { inclusive = true } } },
                onAgenda = { navController.navigate(Route.DocenteAgenda) },
                onPerfil = { navController.navigate(Route.DocentePerfil) },
            )
        }

        composable<Route.DocentePerfil> {
            PerfilScreen(
                user = UserSession.currentUser,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.resetLoginState()
                    authViewModel.resetRegisterState()
                    UserSession.logout()
                    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                },
            )
        }

        // ════════════════════════════ ESTUDIANTE ═════════════════════════════
        composable<Route.EstudianteDashboard> {
            val user = UserSession.currentUser ?: return@composable redirectLogin(navController)
            EstudianteHomeScreen(
                user = user,
                viewModel = estudianteVm,
                onEscanearQr = { navController.navigate(Route.EstudianteEscanearQr) },
                onIngresarCodigo = { navController.navigate(Route.EstudianteIngresarCodigo) },
                onAgenda = { navController.navigate(Route.EstudianteAgenda) },
                onHistorial = { navController.navigate(Route.EstudianteHistorial) },
                onPerfil = { navController.navigate(Route.EstudiantePerfil) },
            )
        }

        composable<Route.EstudianteAgenda> {
            val state by estudianteVm.uiState.collectAsState()
            EstudianteAgendaScreen(
                eventos = state.agenda,
                laboratorios = emptyList(),
                onHoy = { navController.navigate(Route.EstudianteDashboard) { popUpTo(Route.EstudianteDashboard) { inclusive = true } } },
                onHistorial = { navController.navigate(Route.EstudianteHistorial) },
                onPerfil = { navController.navigate(Route.EstudiantePerfil) },
            )
        }

        composable<Route.EstudianteHistorial> {
            val state by estudianteVm.uiState.collectAsState()
            EstudianteHistorialScreen(
                historial = state.historial,
                resumen = state.resumen,
                onHoy = { navController.navigate(Route.EstudianteDashboard) { popUpTo(Route.EstudianteDashboard) { inclusive = true } } },
                onAgenda = { navController.navigate(Route.EstudianteAgenda) },
                onPerfil = { navController.navigate(Route.EstudiantePerfil) },
            )
        }

        composable<Route.EstudianteEscanearQr> {
            val state by estudianteVm.uiState.collectAsState()
            EscanearQrScreen(
                expectedCode = state.sesionActiva?.codigoAsistencia ?: "",
                onClose = { navController.popBackStack() },
                onUsarCodigo = { navController.navigate(Route.EstudianteIngresarCodigo) },
                onDetectado = {
                    val sesionId = state.sesionActiva?.id ?: ""
                    estudianteVm.registrarAsistencia(sesionId, "qr")
                    navController.navigate(Route.EstudianteConfirmacion)
                },
            )
        }

        composable<Route.EstudianteIngresarCodigo> {
            val state by estudianteVm.uiState.collectAsState()
            IngresarCodigoScreen(
                sesion = state.sesionActiva ?: Sesion(),
                onBack = { navController.popBackStack() },
                onConfirmar = {
                    val sesionId = state.sesionActiva?.id ?: ""
                    estudianteVm.registrarAsistencia(sesionId, "codigo")
                    navController.navigate(Route.EstudianteConfirmacion)
                },
            )
        }

        composable<Route.EstudianteConfirmacion> {
            val state by estudianteVm.uiState.collectAsState()
            val comprobante = state.comprobanteReciente
            if (comprobante != null) {
                ConfirmacionAsistenciaScreen(
                    comprobante = comprobante,
                    onGuardar = {
                        navController.navigate(Route.EstudianteDashboard) {
                            popUpTo(Route.EstudianteDashboard) { inclusive = true }
                        }
                    },
                    onVolverInicio = {
                        navController.navigate(Route.EstudianteDashboard) {
                            popUpTo(Route.EstudianteDashboard) { inclusive = true }
                        }
                    },
                )
            }
        }

        composable<Route.EstudiantePerfil> {
            PerfilScreen(
                user = UserSession.currentUser,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.resetLoginState()
                    authViewModel.resetRegisterState()
                    UserSession.logout()
                    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                },
            )
        }

        // ════════════════════════════ AUXILIAR ═══════════════════════════════
        composable<Route.AuxiliarDashboard> {
            val user = UserSession.currentUser
            if (user == null) {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                return@composable
            }
            AuxiliarHomeScreen(
                user = user,
                onNuevoPrestamo = { navController.navigate(Route.AuxiliarBuscarEquipo) },
                onVerHistorial = { navController.navigate(Route.AuxiliarHistorial) },
                onVerInventario = { navController.navigate(Route.AuxiliarInventario) },
                onVerPerfil = { navController.navigate(Route.AuxiliarPerfil) },
                onLogout = {
                    authViewModel.resetLoginState()
                    authViewModel.resetRegisterState()
                    UserSession.logout()
                    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
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
            HistorialScreen(onBack = { navController.popBackStack() })
        }
        composable<Route.AuxiliarInventario> {
            InventarioScreen(onBack = { navController.popBackStack() })
        }
        composable<Route.AuxiliarPerfil> {
            PerfilScreen(
                user = UserSession.currentUser,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.resetLoginState()
                    authViewModel.resetRegisterState()
                    UserSession.logout()
                    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ════════════════════════════ SOPORTE ════════════════════════════════
        composable<Route.SoporteDashboard> {
            val user = UserSession.currentUser
            if (user == null) {
                navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                return@composable
            }
            SoporteHomeScreen(
                user = user,
                navController = navController,
                onVerDetalle = { incidenciaId -> navController.navigate(Route.SoporteDetalle(incidenciaId)) },
                onNuevaIncidencia = { navController.navigate(Route.SoporteRegistrarIncidencia) }
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
                    authViewModel.resetLoginState()
                    authViewModel.resetRegisterState()
                    UserSession.logout()
                    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable<Route.SoporteMetricas> {
            SoporteMetricasScreen(navController = navController)
        }
    }
}

// Helper para redirigir al login cuando no hay sesión activa.
private fun redirectLogin(navController: NavHostController) {
    navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } }
}
