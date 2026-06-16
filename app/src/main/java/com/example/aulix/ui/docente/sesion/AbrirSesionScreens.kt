package com.example.aulix.ui.docente.sesion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.Sesion
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.docente.components.DetailHeader
import com.example.aulix.ui.theme.*

// ── HU 02 · Paso 1 de 2 · Detalle de la sesión programada ──────────────────────
@Composable
fun DetalleSesionScreen(
    sesion: Sesion,
    onBack: () -> Unit,
    onAbrir: () -> Unit,
) {
    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Column(modifier = Modifier.background(Lienzo).padding(20.dp)) {
                AulixButton(text = "→  Abrir sesión ahora", onClick = onAbrir)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Se registrará la hora real de apertura",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(
                eyebrow = "PASO 1 DE 2",
                title = "Sesión programada",
                onBack = onBack,
                trailing = { CircleIconButton(Icons.Default.MoreHoriz, onClick = {}, contentDescription = "Más") },
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "HOY · ${sesion.rangoHorario}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Cobre,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(sesion.titulo, style = MaterialTheme.typography.headlineLarge, color = Tinta)
                Text(sesion.asignaturaGrupo, style = MaterialTheme.typography.bodyMedium, color = Tinta.copy(alpha = 0.55f))

                Spacer(Modifier.height(16.dp))

                // Tabla de detalle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DetailRow("LABORATORIO", "${sesion.laboratorio} · ${sesion.edificio}")
                    DetailRow("CAPACIDAD", sesion.capacidad)
                    DetailRow("AUXILIAR", sesion.auxiliar)
                    DetailRow("PRÁCTICA", sesion.practica)
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "ANTES DE ABRIR",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.45f),
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(10.dp))
                ChecklistRow("Equipos disponibles", "OK", ok = true)
                Spacer(Modifier.height(8.dp))
                ChecklistRow("Sin incidencias abiertas", "OK", ok = true)
                Spacer(Modifier.height(8.dp))
                ChecklistRow("Material de práctica", "OPCIONAL", ok = false)

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Tinta.copy(alpha = 0.45f),
            letterSpacing = 0.5.sp,
            modifier = Modifier.width(96.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Tinta, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ChecklistRow(label: String, tag: String, ok: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape)
                .background(if (ok) StatusGreen.copy(alpha = 0.15f) else StatusGray.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (ok) Icons.Default.Check else Icons.Default.Close,
                null,
                tint = if (ok) StatusGreen else StatusGray,
                modifier = Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Tinta, modifier = Modifier.weight(1f))
        Text(
            tag,
            style = MaterialTheme.typography.labelSmall,
            color = if (ok) StatusGreen else Cobre,
            letterSpacing = 0.5.sp,
        )
    }
}

// ── HU 02 · Paso 2 de 2 · Confirmar apertura ───────────────────────────────────
@Composable
fun ConfirmarAperturaScreen(
    sesion: Sesion,
    onBack: () -> Unit,
    onConfirmar: () -> Unit,
) {
    var observaciones by remember { mutableStateOf("") }
    val etiquetas = listOf("Asistencia parcial", "Equipo con falla", "Material listo", "Cambio de aula")
    val horaApertura = remember {
        val t = java.time.LocalTime.now()
        "%02d:%02d".format(t.hour, t.minute)
    }
    val fechaApertura = remember {
        val d = java.time.LocalDate.now()
        val diaNombre = d.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("es", "CO")).uppercase()
        val mesNombre = d.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("es", "CO")).uppercase()
        "$diaNombre · ${d.dayOfMonth} $mesNombre ${d.year}"
    }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Row(
                modifier = Modifier.background(Lienzo).padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f).height(56.dp),
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.titleMedium, color = Tinta)
                }
                AulixButton(text = "Confirmar apertura", onClick = onConfirmar, modifier = Modifier.weight(1.4f))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(eyebrow = "PASO 2 DE 2", title = "Abrir sesión", onBack = onBack)

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Card de contexto con barra lateral
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Arena)
                        .height(IntrinsicSize.Min),
                ) {
                    Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Cobalto))
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(sesion.titulo, style = MaterialTheme.typography.titleMedium, color = Tinta, fontWeight = FontWeight.SemiBold)
                        Text("${sesion.laboratorio} · Grupo ${sesion.grupo}", style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.6f))
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("HORA REAL DE APERTURA", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(horaApertura, style = MaterialTheme.typography.displayLarge, color = Tinta)
                    Spacer(Modifier.width(12.dp))
                    Text(fechaApertura, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), modifier = Modifier.padding(bottom = 6.dp))
                }

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("OBSERVACIONES DE APERTURA", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                    Text("Opcional", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f))
                }
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    shape = RoundedCornerShape(12.dp),
                    supportingText = { Text("${observaciones.length} / 500", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.End, style = MaterialTheme.typography.labelSmall) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cobalto,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp),
                )

                Spacer(Modifier.height(12.dp))
                Text("Agregar etiqueta:", style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.6f))
                Spacer(Modifier.height(8.dp))
                FlowChips(etiquetas)

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowChips(labels: List<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEach { label ->
            var selected by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (selected) Cielo else Color.White)
                    .border(1.dp, if (selected) Cobalto else BorderLight, RoundedCornerShape(50.dp))
                    .clickable { selected = !selected }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text("+ $label", style = MaterialTheme.typography.labelMedium, color = if (selected) Cobalto else Tinta.copy(alpha = 0.7f))
            }
        }
    }
}
