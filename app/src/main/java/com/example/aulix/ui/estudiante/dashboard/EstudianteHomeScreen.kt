package com.example.aulix.ui.estudiante.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.Asignatura
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.estudiante.EstudianteViewModel
import com.example.aulix.ui.estudiante.components.EstudianteBottomBar
import com.example.aulix.ui.estudiante.components.EstudianteTab
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.*

// ── Dashboard del estudiante · "Mis prácticas" ─────────────────────────────────
@Composable
fun EstudianteHomeScreen(
    user: User,
    viewModel: EstudianteViewModel,
    onEscanearQr: () -> Unit,
    onIngresarCodigo: () -> Unit,
    onAgenda: () -> Unit,
    onHistorial: () -> Unit,
    onPerfil: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val sesionActiva = state.sesionActiva

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            EstudianteBottomBar(
                selected = EstudianteTab.HOY,
                onHoy = {}, onAgenda = onAgenda, onHistorial = onHistorial, onPerfil = onPerfil,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(initials = user.initials, role = UserRole.ESTUDIANTE, size = 36)
                val periodoLabel = remember {
                    val d = java.time.LocalDate.now()
                    val mes = d.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("es", "CO")).uppercase()
                    "ESTUDIANTE · $mes ${d.year}"
                }
                Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(periodoLabel, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                    Text("Mis prácticas", style = MaterialTheme.typography.titleLarge, color = Tinta)
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Arena), contentAlignment = Alignment.Center) {
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, "Notificaciones", tint = Tinta.copy(alpha = 0.6f)) }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Hola, ${user.fullName.split(" ").first()} —", style = MaterialTheme.typography.bodyLarge, color = Tinta.copy(alpha = 0.55f))
                Spacer(Modifier.height(4.dp))
                val countText = when (val n = state.sesionesSemana) {
                    0    -> "Sin sesiones\nprogramadas esta semana."
                    1    -> "Tienes 1 sesión\nprogramada esta semana."
                    else -> "Tienes $n sesiones\nprogramadas esta semana."
                }
                Text(countText, style = MaterialTheme.typography.displayLarge, color = Tinta, lineHeight = 40.sp)

                Spacer(Modifier.height(20.dp))

                if (sesionActiva != null) {
                    // Sesión activa real — el docente ya la abrió
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Cobalto).padding(20.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color.White))
                            Spacer(Modifier.width(6.dp))
                            Text("EN CURSO · ABIERTA POR TU DOCENTE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), letterSpacing = 0.5.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(sesionActiva.titulo, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                        Text("${sesionActiva.asignatura} · ${sesionActiva.laboratorio}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                onClick = onEscanearQr,
                                shape = RoundedCornerShape(50.dp),
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                            ) {
                                Row(modifier = Modifier.padding(vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode2, null, tint = Cobalto, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Escanear QR", style = MaterialTheme.typography.titleMedium, color = Cobalto, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Surface(
                                onClick = onIngresarCodigo,
                                shape = RoundedCornerShape(50.dp),
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f),
                            ) {
                                Row(modifier = Modifier.padding(vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Código", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    // Sin sesión activa — placeholder
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                            .background(Arena).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Sin sesión activa ahora", style = MaterialTheme.typography.titleMedium, color = Tinta)
                        Spacer(Modifier.height(4.dp))
                        Text("Cuando tu docente abra una práctica aparecerá aquí.", style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.55f))
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    "MIS ASIGNATURAS · ${state.asignaturas.size} MATRICULADAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(10.dp))
                state.asignaturas.forEach { a ->
                    AsignaturaCard(a)
                    Spacer(Modifier.height(10.dp))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AsignaturaCard(a: Asignatura) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White)
            .border(1.dp, BorderLight.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Color(a.colorHex)))
        Column(modifier = Modifier.weight(1f).padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(a.codigo, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp)
                if (a.esHoy) {
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(StatusGreen.copy(alpha = 0.15f)).padding(horizontal = 7.dp, vertical = 2.dp)) {
                        Text("HOY", style = MaterialTheme.typography.labelSmall, color = StatusGreen)
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(a.nombre, style = MaterialTheme.typography.titleMedium, color = Tinta, fontWeight = FontWeight.SemiBold)
            Text("Grupo ${a.grupo} · próxima: ${a.proxima}", style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.5f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Tinta.copy(alpha = 0.35f), modifier = Modifier.padding(end = 12.dp))
    }
}
