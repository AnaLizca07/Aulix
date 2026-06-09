package com.example.aulix.ui.docente.incidencias

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Science
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
import com.example.aulix.domain.model.TipoIncidencia
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.theme.*
import androidx.compose.material.icons.filled.Close

// ── HU 06 · Reportar incidencia ────────────────────────────────────────────────
@Composable
fun ReportarIncidenciaScreen(
    sesion: Sesion,
    onClose: () -> Unit,
    onEnviar: () -> Unit,
) {
    var tipo by remember { mutableStateOf(TipoIncidencia.EQUIPO) }
    var descripcion by remember { mutableStateOf("Pierde conexión intermitente al configurar VLAN 20. Se reinicia y vuelve a fallar.") }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Box(modifier = Modifier.background(Lienzo).padding(20.dp)) {
                AulixButton(
                    text = "Enviar a soporte técnico",
                    onClick = onEnviar,
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp)) },
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            // Header con X de cierre
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircleIconButton(Icons.Default.Close, onClick = onClose, contentDescription = "Cerrar")
                Text(
                    "Reportar incidencia",
                    style = MaterialTheme.typography.titleLarge,
                    color = Tinta,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(Modifier.size(40.dp))
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Asociada a
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Arena).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Science, null, tint = Tinta.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("ASOCIADA A", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp)
                        Text("Sesión ${sesion.id} · ${sesion.laboratorio}", style = MaterialTheme.typography.titleSmall, color = Tinta)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("TIPO DE INCIDENCIA", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TipoCard("Equipo", "Falla técnica", TipoIncidencia.EQUIPO, tipo, Modifier.weight(1f)) { tipo = it }
                    TipoCard("Seguridad", "Riesgo o robo", TipoIncidencia.SEGURIDAD, tipo, Modifier.weight(1f)) { tipo = it }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TipoCard("Infraestructura", "Eléctrica, red", TipoIncidencia.INFRAESTRUCTURA, tipo, Modifier.weight(1f)) { tipo = it }
                    TipoCard("Otra", "Especificar", TipoIncidencia.OTRA, tipo, Modifier.weight(1f)) { tipo = it }
                }

                Spacer(Modifier.height(20.dp))
                Text("EQUIPO AFECTADO", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
                        .border(1.dp, Cobalto, RoundedCornerShape(12.dp)).clickable {}.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Science, null, tint = Cobalto, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Switch Cisco 03", style = MaterialTheme.typography.titleSmall, color = Tinta)
                        Text("SN: CIS-2960-X · #03", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f))
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Tinta.copy(alpha = 0.4f))
                }

                Spacer(Modifier.height(20.dp))
                Text("DESCRIPCIÓN", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cobalto,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
                )

                Spacer(Modifier.height(20.dp))
                Row {
                    Text("EVIDENCIA", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                    Text("Opcional · máx 3", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PhotoThumb("Foto 1", Modifier.weight(1f))
                    AddThumb(Icons.Default.CameraAlt, Modifier.weight(1f))
                    AddThumb(Icons.Default.Image, Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TipoCard(
    titulo: String,
    descripcion: String,
    value: TipoIncidencia,
    selected: TipoIncidencia,
    modifier: Modifier = Modifier,
    onSelect: (TipoIncidencia) -> Unit,
) {
    val isSelected = value == selected
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Cobalto else Color.White)
            .border(1.dp, if (isSelected) Cobalto else BorderLight.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { onSelect(value) }
            .padding(14.dp),
    ) {
        Text(titulo, style = MaterialTheme.typography.titleSmall, color = if (isSelected) Color.White else Tinta, fontWeight = FontWeight.SemiBold)
        Text(descripcion, style = MaterialTheme.typography.bodySmall, color = if (isSelected) Color.White.copy(alpha = 0.8f) else Tinta.copy(alpha = 0.5f))
    }
}

@Composable
private fun PhotoThumb(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(70.dp).clip(RoundedCornerShape(10.dp)).background(Arena),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.5f))
    }
}

@Composable
private fun AddThumb(icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(70.dp).clip(RoundedCornerShape(10.dp))
            .border(1.dp, Cobalto.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).clickable {},
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = Cobalto, modifier = Modifier.size(22.dp))
    }
}
