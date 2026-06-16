package com.example.aulix.ui.docente.sesion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.EstadoCierre
import com.example.aulix.domain.model.Sesion
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.docente.components.DetailHeader
import com.example.aulix.ui.theme.*

// ── HU 03 · Cerrar sesión con estado final ─────────────────────────────────────
@Composable
fun CerrarSesionScreen(
    sesion: Sesion,
    onBack: () -> Unit,
    onCerrar: () -> Unit,
) {
    var estado by remember { mutableStateOf(EstadoCierre.NORMAL) }
    var observaciones by remember { mutableStateOf("") }
    val rangoHorario = remember(sesion.horaApertura) {
        val apertura = sesion.horaApertura?.take(5) ?: "—"
        val ahora = java.time.LocalTime.now().let { t -> "%02d:%02d".format(t.hour, t.minute) }
        "$apertura → $ahora"
    }
    val duracion = remember(sesion.horaApertura) {
        val apertura = sesion.horaApertura ?: return@remember "—"
        runCatching {
            val hA = apertura.substring(0, 2).toInt()
            val mA = apertura.substring(3, 5).toInt()
            val ahora = java.time.LocalTime.now()
            val diffMin = (ahora.hour * 60 + ahora.minute) - (hA * 60 + mA)
            if (diffMin < 0) "—" else "%02d:%02d".format(diffMin / 60, diffMin % 60)
        }.getOrDefault("—")
    }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Box(modifier = Modifier.background(Lienzo).padding(20.dp)) {
                AulixButton(text = "✓  Cerrar sesión", onClick = onCerrar)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(eyebrow = "", title = "Cerrar sesión", onBack = onBack)

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Resumen de duración + métricas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("DURACIÓN", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                        Text(rangoHorario, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(duracion, style = MaterialTheme.typography.displayLarge, color = Tinta)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CierreStat("${sesion.asistentesConfirmados}/${sesion.totalEstudiantes}", "Asistencia", StatusGreen, Modifier.weight(1f))
                        CierreStat("—", "Incidencias", StatusAmber, Modifier.weight(1f))
                        CierreStat("—", "Evidencias", Tinta, Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("ESTADO FINAL", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                EstadoOption("Normal", "Práctica completada sin novedad", EstadoCierre.NORMAL, estado, StatusGreen) { estado = it }
                Spacer(Modifier.height(8.dp))
                EstadoOption("Con incidencia", "Hubo un evento reportado", EstadoCierre.CON_INCIDENCIA, estado, Cobre) { estado = it }
                Spacer(Modifier.height(8.dp))
                EstadoOption("Cancelada", "No se pudo completar", EstadoCierre.CANCELADA, estado, StatusRed) { estado = it }

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("OBSERVACIONES DE CIERRE", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                    Text("Requerido", style = MaterialTheme.typography.labelSmall, color = Cobre)
                }
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cobre,
                        unfocusedBorderColor = Cobre.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp),
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CierreStat(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.5f))
    }
}

@Composable
private fun EstadoOption(
    titulo: String,
    descripcion: String,
    value: EstadoCierre,
    selected: EstadoCierre,
    accent: Color,
    onSelect: (EstadoCierre) -> Unit,
) {
    val isSelected = value == selected
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accent.copy(alpha = 0.08f) else Color.White)
            .border(1.dp, if (isSelected) accent else BorderLight.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { onSelect(value) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape)
                .border(2.dp, if (isSelected) accent else BorderLight, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(accent))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = if (isSelected) accent else Tinta, fontWeight = FontWeight.SemiBold)
            Text(descripcion, style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.55f))
        }
    }
}
