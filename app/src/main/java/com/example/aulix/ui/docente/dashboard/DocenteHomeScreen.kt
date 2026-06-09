package com.example.aulix.ui.docente.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.docente.DocenteViewModel
import com.example.aulix.ui.docente.components.DocenteBottomBar
import com.example.aulix.ui.docente.components.DocenteTab
import com.example.aulix.ui.theme.*

@Composable
fun DocenteHomeScreen(
    user: User,
    viewModel: DocenteViewModel,
    onAbrirSesion: () -> Unit,
    onGenerarQr: () -> Unit,
    onCodigoTiempo: () -> Unit,
    onReportarIncidencia: () -> Unit,
    onEvidencias: () -> Unit,
    onAgenda: () -> Unit,
    onIndicadores: () -> Unit,
    onPerfil: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val sesion = state.sesion

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            DocenteBottomBar(
                selected = DocenteTab.HOY,
                onHoy = {},
                onAgenda = onAgenda,
                onIndicadores = onIndicadores,
                onPerfil = onPerfil,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(initials = user.initials, role = UserRole.DOCENTE, size = 36)
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "JUEVES 22 · MAY 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = Tinta.copy(alpha = 0.45f),
                        letterSpacing = 1.sp,
                    )
                    Text("Mi día", style = MaterialTheme.typography.titleLarge, color = Tinta)
                }
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Arena),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, "Notificaciones", tint = Tinta.copy(alpha = 0.6f))
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Text(
                    text = "Buenos días, ${user.fullName.split(" ").first()} —",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Tinta.copy(alpha = 0.55f),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tienes 1 práctica\nprogramada hoy.",
                    style = MaterialTheme.typography.displayLarge,
                    color = Tinta,
                    lineHeight = 40.sp,
                )

                Spacer(Modifier.height(20.dp))

                // ── Card de próxima práctica ──────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Cobalto)
                        .padding(20.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PRÓXIMA · EN ${sesion.minutosRestantes} MIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.sp,
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = sesion.rangoHorario,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(sesion.titulo, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text(
                        sesion.asignaturaGrupo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(sesion.laboratorio, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Default.People, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${sesion.totalEstudiantes} estudiantes", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = onAbrirSesion,
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                                Text("Abrir sesión  →", style = MaterialTheme.typography.titleMedium, color = Cobalto, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.MoreHoriz, "Más", tint = Color.White)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "ACCESOS RÁPIDOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(12.dp))

                // ── Grid 2x2 de accesos rápidos ───────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAccess("Generar QR", Icons.Default.QrCode2, Cobalto, Modifier.weight(1f), onGenerarQr)
                    QuickAccess("Código tiempo", Icons.Default.Schedule, Cobalto, Modifier.weight(1f), onCodigoTiempo)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAccess("Reportar incidencia", Icons.Default.WarningAmber, Cobre, Modifier.weight(1f), onReportarIncidencia)
                    QuickAccess("Evidencias", Icons.Default.CameraAlt, StatusGreen, Modifier.weight(1f), onEvidencias)
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun QuickAccess(
    label: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(28.dp))
        Text(label, style = MaterialTheme.typography.titleSmall, color = Tinta)
    }
}
