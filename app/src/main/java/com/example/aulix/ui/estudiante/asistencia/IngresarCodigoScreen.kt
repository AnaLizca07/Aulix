package com.example.aulix.ui.estudiante.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.Sesion
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.docente.components.DetailHeader
import com.example.aulix.ui.theme.*

// ── HU 11 · Ingresar código de tiempo (alternativa al QR) ──────────────────────
@Composable
fun IngresarCodigoScreen(
    sesion: Sesion,
    onBack: () -> Unit,
    onConfirmar: () -> Unit,
) {
    var codigo by remember { mutableStateOf("") }
    val completo = codigo.length == 6
    val codigoValido = completo && codigo == sesion.codigoAsistencia
    val codigoInvalido = completo && !codigoValido

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Box(modifier = Modifier.background(Lienzo).padding(20.dp)) {
                AulixButton(text = "✓  Confirmar asistencia", onClick = onConfirmar, enabled = codigoValido)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(eyebrow = "ALTERNATIVA AL QR", title = "Ingresar código", onBack = onBack)

            Column(modifier = Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Escribe el código de 6 dígitos que el docente compartió.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Tinta.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))

                // Casillas
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(6) { i ->
                        val char = codigo.getOrNull(i)
                        val isCursor = i == codigo.length && !completo
                        val borderColor = when {
                            codigoInvalido -> StatusRed
                            codigoValido   -> StatusGreen
                            isCursor       -> Cobalto
                            else           -> BorderLight
                        }
                        val borderWidth = if (isCursor || codigoInvalido || codigoValido) 2.dp else 1.dp
                        Box(
                            modifier = Modifier.size(46.dp, 56.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)
                                .border(borderWidth, borderColor, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(char?.toString() ?: "", style = MaterialTheme.typography.headlineMedium, color = Tinta, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Estado del código
                when {
                    codigoValido -> Row(
                        modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(StatusGreen.copy(alpha = 0.12f)).padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("CÓDIGO CORRECTO", style = MaterialTheme.typography.labelSmall, color = StatusGreen, letterSpacing = 0.5.sp)
                    }
                    codigoInvalido -> Row(
                        modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(StatusRed.copy(alpha = 0.10f)).padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = StatusRed, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("CÓDIGO INCORRECTO", style = MaterialTheme.typography.labelSmall, color = StatusRed, letterSpacing = 0.5.sp)
                    }
                    else -> Row(
                        modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(StatusGreen.copy(alpha = 0.12f)).padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("CÓDIGO ACTIVO", style = MaterialTheme.typography.labelSmall, color = StatusGreen, letterSpacing = 0.5.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
                        .border(1.dp, BorderLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(14.dp),
                ) {
                    Text("REGISTRANDO PARA", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(sesion.titulo, style = MaterialTheme.typography.titleMedium, color = Tinta, fontWeight = FontWeight.SemiBold)
                    Text("${sesion.asignatura} · ${sesion.laboratorio}", style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.55f))
                }

                Spacer(Modifier.height(20.dp))

                // Numpad
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "<"),
                )
                rows.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { key ->
                            when (key) {
                                "" -> Spacer(Modifier.weight(1f))
                                "<" -> NumKey(Modifier.weight(1f), isBackspace = true) {
                                    if (codigo.isNotEmpty()) codigo = codigo.dropLast(1)
                                }
                                else -> NumKey(Modifier.weight(1f), label = key) {
                                    if (codigo.length < 6) codigo += key
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun NumKey(
    modifier: Modifier = Modifier,
    label: String = "",
    isBackspace: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.height(56.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)
            .border(1.dp, BorderLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isBackspace) {
            Icon(Icons.AutoMirrored.Filled.Backspace, "Borrar", tint = Tinta)
        } else {
            Text(label, style = MaterialTheme.typography.headlineMedium, color = Tinta, fontWeight = FontWeight.Medium)
        }
    }
}
