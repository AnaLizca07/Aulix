package com.example.aulix.ui.estudiante.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.ui.estudiante.EstudianteViewModel
import com.example.aulix.ui.theme.*

// ── Confirmación de asistencia con comprobante ─────────────────────────────────
@Composable
fun ConfirmacionAsistenciaScreen(
    comprobante: EstudianteViewModel.Comprobante,
    onGuardar: () -> Unit,
    onVolverInicio: () -> Unit,
) {
    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Row(
                modifier = Modifier.background(Lienzo).padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onGuardar,
                    shape = RoundedCornerShape(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f).height(56.dp),
                ) {
                    Icon(Icons.Default.Download, null, tint = Tinta, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar", style = MaterialTheme.typography.titleMedium, color = Tinta)
                }
                Button(
                    onClick = onVolverInicio,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cobalto, contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(56.dp),
                ) {
                    Text("Volver al inicio", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))
            Box(
                modifier = Modifier.size(88.dp).clip(CircleShape).background(StatusGreen),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text("Asistencia\nregistrada", style = MaterialTheme.typography.headlineLarge, color = Tinta, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "Tu presencia quedó confirmada en el sistema. Puedes guardar este comprobante.",
                style = MaterialTheme.typography.bodyMedium,
                color = Tinta.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            // Comprobante
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("COMPROBANTE", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp, modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(StatusGreen))
                    Spacer(Modifier.width(5.dp))
                    Text("VÁLIDO", style = MaterialTheme.typography.labelSmall, color = StatusGreen, letterSpacing = 0.5.sp)
                }
                HorizontalDivider(color = Tinta.copy(alpha = 0.07f))
                ComprobanteRow("SESIÓN", comprobante.sesion)
                ComprobanteRow("ASIGNATURA", comprobante.asignatura)
                ComprobanteRow("LABORATORIO", comprobante.laboratorio)
                ComprobanteRow("HORA REGISTRO", comprobante.horaRegistro)
                ComprobanteRow("ID", comprobante.id)
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Arena).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.NotificationsActive, null, tint = Tinta.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Notificamos al docente · ${comprobante.confirmados} confirmados",
                    style = MaterialTheme.typography.bodySmall,
                    color = Tinta.copy(alpha = 0.7f),
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ComprobanteRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp, modifier = Modifier.width(120.dp))
        Spacer(Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, color = Tinta, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}
