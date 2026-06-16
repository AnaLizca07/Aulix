package com.example.aulix.ui.docente.evidencias

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.Evidencia
import com.example.aulix.domain.model.Sesion
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.docente.components.DetailHeader
import com.example.aulix.ui.theme.*

// ── HU 07 · Evidencias de la sesión ────────────────────────────────────────────
@Composable
fun EvidenciasScreen(
    sesion: Sesion,
    evidencias: List<Evidencia>,
    onBack: () -> Unit,
    onCapturar: () -> Unit,
) {
    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Box(modifier = Modifier.background(Lienzo).padding(20.dp)) {
                AulixButton(
                    text = "Capturar nueva",
                    onClick = onCapturar,
                    leadingIcon = { Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp)) },
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(
                eyebrow = "SESIÓN ${sesion.id}",
                title = "Evidencias",
                onBack = onBack,
                trailing = { CircleIconButton(Icons.Default.FilterList, onClick = {}, contentDescription = "Filtrar") },
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                val fechaEvidencia = remember(sesion.reserva?.fecha) {
                    val raw = sesion.reserva?.fecha ?: return@remember ""
                    runCatching {
                        val d = java.time.LocalDate.parse(raw)
                        val mes = d.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("es", "CO")).uppercase()
                        "${d.dayOfMonth} $mes"
                    }.getOrDefault(raw)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${evidencias.size}", style = MaterialTheme.typography.displayLarge, color = Tinta)
                    Spacer(Modifier.width(6.dp))
                    Text("evidencias", style = MaterialTheme.typography.bodyLarge, color = Tinta.copy(alpha = 0.6f), modifier = Modifier.weight(1f))
                    EvidenciaPill("Tomar", Icons.Default.CameraAlt, filled = true) {}
                    Spacer(Modifier.width(8.dp))
                    EvidenciaPill("Galería", Icons.Default.Image, filled = false) {}
                }
                Text("$fechaEvidencia · ${sesion.laboratorio}", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)

                Spacer(Modifier.height(16.dp))

                // Grid 2 columnas
                val fotos = evidencias.filter { !it.esNotaVoz }
                fotos.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        fila.forEach { ev -> EvidenciaTile(ev, Modifier.weight(1f)) }
                        if (fila.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Fila final: nota de voz + añadir
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.weight(1f).height(110.dp).clip(RoundedCornerShape(12.dp)).background(Arena),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Mic, "Nota de voz", tint = Tinta.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
                    }
                    Box(
                        modifier = Modifier.weight(1f).height(110.dp).clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Cobalto.copy(alpha = 0.4f), RoundedCornerShape(12.dp)).clickable(onClick = onCapturar),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, null, tint = Cobalto, modifier = Modifier.size(24.dp))
                            Text("Añadir", style = MaterialTheme.typography.labelMedium, color = Cobalto)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EvidenciaPill(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, filled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (filled) Cobalto else Color.White)
            .then(if (filled) Modifier else Modifier.border(1.dp, BorderLight, RoundedCornerShape(50.dp)))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (filled) Color.White else Tinta, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(5.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (filled) Color.White else Tinta)
    }
}

@Composable
private fun EvidenciaTile(ev: Evidencia, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(12.dp)).background(Arena),
            contentAlignment = Alignment.Center,
        ) {
            Text(ev.titulo.uppercase(), style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.4f))
        }
        Spacer(Modifier.height(6.dp))
        Text(ev.titulo, style = MaterialTheme.typography.titleSmall, color = Tinta, fontWeight = FontWeight.Medium)
        Text(
            buildString {
                append(ev.hora); append(" · "); append(ev.tipo)
                if (ev.nota.isNotEmpty()) { append("\n"); append(ev.nota) }
            },
            style = MaterialTheme.typography.labelSmall,
            color = Tinta.copy(alpha = 0.45f),
        )
    }
}
