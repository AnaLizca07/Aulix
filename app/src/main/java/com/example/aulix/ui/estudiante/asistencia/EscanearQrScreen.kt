package com.example.aulix.ui.estudiante.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.ui.theme.*

// ── HU 10 · Escanear el QR del docente (siempre pantalla oscura) ───────────────
@Composable
fun EscanearQrScreen(
    onClose: () -> Unit,
    onUsarCodigo: () -> Unit,
    onDetectado: () -> Unit,
) {
    Scaffold(containerColor = TintaDark) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DarkCircle(Icons.Default.Close, "Cerrar", onClose)
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceVarDark).padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text("ESCANEAR QR", style = MaterialTheme.typography.labelSmall, color = TextOnDark, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    DarkCircle(Icons.Default.FlashOn, "Flash") {}
                }

                Spacer(Modifier.weight(1f))

                // Visor
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally).size(240.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // Marco con esquinas
                    Box(
                        modifier = Modifier.fillMaxSize().border(2.dp, Cobre, RoundedCornerShape(20.dp)),
                    )
                    Box(modifier = Modifier.fillMaxWidth(0.6f).height(2.dp).background(Cobre.copy(alpha = 0.8f)))
                }

                Spacer(Modifier.height(32.dp))
                Text(
                    "Encuadra el QR del docente",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextOnDark,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Mantén el código dentro del marco. Se reconoce automáticamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMutedDark,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.weight(1f))

                // Controles inferiores
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DarkPill("Usar código", Icons.Default.Schedule, onUsarCodigo)
                    // Botón de captura (simula detección del QR)
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Cobre)
                            .border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable(onClick = onDetectado),
                    )
                    DarkPill("Foto", Icons.Default.Image) {}
                }
            }
        }
    }
}

@Composable
private fun DarkCircle(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceVarDark),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick) { Icon(icon, desc, tint = TextOnDark) }
    }
}

@Composable
private fun DarkPill(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(SurfaceVarDark).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = TextOnDark, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextOnDark)
    }
}
