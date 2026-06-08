package com.example.aulix.ui.docente.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
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
import com.example.aulix.domain.model.Asistente
import com.example.aulix.domain.model.Sesion
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.docente.components.DetailHeader
import com.example.aulix.ui.theme.*

// ── HU 04 · Asistencia por QR (pantalla siempre oscura) ────────────────────────
@Composable
fun AsistenciaQrScreen(
    sesion: Sesion,
    asistentes: List<Asistente>,
    onBack: () -> Unit,
    onUsarCodigo: () -> Unit,
    onCerrarAsistencia: () -> Unit,
) {
    Scaffold(
        containerColor = TintaDark,
        bottomBar = {
            Box(modifier = Modifier.background(TintaDark).padding(20.dp)) {
                Button(
                    onClick = onCerrarAsistencia,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVarDark, contentColor = TextOnDark),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Text("Cerrar asistencia", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            // Header oscuro
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceVarDark),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Volver", tint = TextOnDark)
                    }
                }
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ASISTENCIA POR QR", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, letterSpacing = 1.sp)
                    Text("Sesión activa", style = MaterialTheme.typography.titleLarge, color = TextOnDark)
                }
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(StatusGreen.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(StatusGreen))
                    Spacer(Modifier.width(5.dp))
                    Text("EN VIVO", style = MaterialTheme.typography.labelSmall, color = StatusGreen)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // QR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .padding(28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.size(180.dp).clip(RoundedCornerShape(12.dp)).background(TextOnDark),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.QrCode2, "Código QR", tint = TintaDark, modifier = Modifier.size(160.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("VENCE EN", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, letterSpacing = 1.sp)
                Text("04:32", style = MaterialTheme.typography.displayLarge, color = Cobre)

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DarkPillButton("Renovar", Icons.Default.Refresh) {}
                    DarkPillButton("Usar código", Icons.Default.Schedule, onUsarCodigo)
                }

                Spacer(Modifier.height(16.dp))
                // Contador de asistentes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CieloDark)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        buildString { append("${sesion.asistentesConfirmados}") },
                        style = MaterialTheme.typography.displayLarge,
                        color = TextOnDark,
                    )
                    Text("/${sesion.totalEstudiantes}", style = MaterialTheme.typography.titleLarge, color = TextMutedDark, modifier = Modifier.padding(top = 8.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Asistentes confirmados", style = MaterialTheme.typography.titleSmall, color = TextOnDark)
                        Text("actualizado hace 2s", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("ÚLTIMOS EN ESCANEAR", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                asistentes.take(3).forEach { a -> AsistenteRowDark(a) }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DarkPillButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(50.dp))
            .background(SurfaceDark)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = TextMutedDark, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextOnDark)
    }
}

@Composable
private fun AsistenteRowDark(a: Asistente) {
    val initials = a.nombre.split(",").firstOrNull()?.take(2)?.uppercase() ?: "?"
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(CieloDark),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, style = MaterialTheme.typography.labelSmall, color = TextOnDark)
        }
        Spacer(Modifier.width(10.dp))
        Text(a.nombre, style = MaterialTheme.typography.bodyMedium, color = TextOnDark, modifier = Modifier.weight(1f))
        Text(a.hora, style = MaterialTheme.typography.labelMedium, color = TextMutedDark)
        if (a.esNuevo) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(StatusGreen).padding(horizontal = 7.dp, vertical = 3.dp),
            ) {
                Text("NUEVO", style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}

// ── HU 05 · Código de tiempo (alternativa al QR) ───────────────────────────────
@Composable
fun CodigoTiempoScreen(
    sesion: Sesion,
    asistentes: List<Asistente>,
    onBack: () -> Unit,
    onVolverQr: () -> Unit,
) {
    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Box(modifier = Modifier.background(Lienzo).padding(20.dp)) {
                OutlinedButton(
                    onClick = onVolverQr,
                    shape = RoundedCornerShape(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Icon(Icons.Default.QrCode2, null, tint = Tinta, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Volver al QR", style = MaterialTheme.typography.titleMedium, color = Tinta)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(eyebrow = "ALTERNATIVA AL QR", title = "Código de tiempo", onBack = onBack)

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Si los estudiantes no pueden escanear el QR, comparte este código verbalmente o en el tablero.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Tinta.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(16.dp))

                // Tarjeta del código
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Cobalto)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("CÓDIGO PARA ASISTENCIA", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), letterSpacing = 1.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sesion.codigoAsistencia.forEach { d ->
                            Box(
                                modifier = Modifier.size(40.dp, 50.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(d.toString(), style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("VENCE EN 04:32", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.55f },
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    )
                }

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    LightPillButton("Regenerar", Icons.Default.Refresh, Modifier.weight(1f)) {}
                    LightPillButton("Compartir", Icons.Default.IosShare, Modifier.weight(1f)) {}
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    "CONFIRMACIONES · ${sesion.asistentesConfirmados}/${sesion.totalEstudiantes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(horizontal = 14.dp),
                ) {
                    ConfirmacionRow("Pérez, L.", "hace 12s", valida = true)
                    HorizontalDivider(color = Tinta.copy(alpha = 0.07f))
                    ConfirmacionRow("Vargas, J.", "hace 40s", valida = true)
                    HorizontalDivider(color = Tinta.copy(alpha = 0.07f))
                    ConfirmacionRow("Código inválido · A1234", "hace 1m", valida = false)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LightPillButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(50.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = Tinta, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = Tinta)
    }
}

@Composable
private fun ConfirmacionRow(nombre: String, hora: String, valida: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape)
                .background(if (valida) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (valida) Icons.Default.Check else Icons.Default.Close,
                null,
                tint = if (valida) StatusGreen else StatusRed,
                modifier = Modifier.size(14.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(nombre, style = MaterialTheme.typography.bodyMedium, color = if (valida) Tinta else Tinta.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
        Text(hora, style = MaterialTheme.typography.labelMedium, color = Tinta.copy(alpha = 0.45f))
    }
}
