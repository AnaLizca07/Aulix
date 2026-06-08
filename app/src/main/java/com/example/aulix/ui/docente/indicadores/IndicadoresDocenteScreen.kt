package com.example.aulix.ui.docente.indicadores

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.docente.components.DocenteBottomBar
import com.example.aulix.ui.docente.components.DocenteTab
import com.example.aulix.ui.theme.*

private data class BarraAsignatura(val nombre: String, val tasa: Int, val color: Color)

// ── Indicadores del docente ────────────────────────────────────────────────────
@Composable
fun IndicadoresDocenteScreen(
    user: User,
    onHoy: () -> Unit,
    onAgenda: () -> Unit,
    onPerfil: () -> Unit,
) {
    var periodo by remember { mutableStateOf("Mes") }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            DocenteBottomBar(
                selected = DocenteTab.INDICADORES,
                onHoy = onHoy, onAgenda = onAgenda, onIndicadores = {}, onPerfil = onPerfil,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(initials = user.initials, role = UserRole.DOCENTE, size = 36)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DOCENTE · MIS CURSOS", style = MaterialTheme.typography.labelSmall, color = Cobre, letterSpacing = 1.sp)
                    Text("Indicadores", style = MaterialTheme.typography.titleLarge, color = Tinta)
                }
                CircleIconButton(Icons.Default.FilterList, onClick = {}, contentDescription = "Filtrar")
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Toggle de periodo
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)).background(Arena).padding(4.dp),
                ) {
                    listOf("Semana", "Mes", "Semestre").forEach { p ->
                        val sel = p == periodo
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(50.dp))
                                .background(if (sel) Color.White else Color.Transparent)
                                .clickable { periodo = p }.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(p, style = MaterialTheme.typography.labelLarge, color = if (sel) Tinta else Tinta.copy(alpha = 0.5f), fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 3 KPIs
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KpiCard("88%", "Asistencia promedio", StatusGreen, "▲ 3 vs. abril", Modifier.weight(1f))
                    KpiCard("24", "Sesiones dictadas", Tinta, null, Modifier.weight(1f))
                    KpiCard("3", "Inasist. críticas", StatusRed, null, Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                Row {
                    Text("ASISTENCIA POR ASIGNATURA", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp, modifier = Modifier.weight(1f))
                    Text("TASA POR SESIÓN", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.height(10.dp))
                AulixCard {
                    val barras = listOf(
                        BarraAsignatura("Redes y Telecom.", 91, StatusGreen),
                        BarraAsignatura("Bases de Datos II", 84, Cobalto),
                        BarraAsignatura("Programación Móvil", 78, Cobre),
                        BarraAsignatura("Arquitectura", 69, Cobre),
                    )
                    barras.forEachIndexed { i, b ->
                        if (i > 0) Spacer(Modifier.height(12.dp))
                        BarraRow(b)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row {
                    Text("MIS RESERVAS · POR ESTADO", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp, modifier = Modifier.weight(1f))
                    Text("23 SOLICITADAS", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.height(10.dp))
                AulixCard {
                    Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                        Box(modifier = Modifier.weight(18f).fillMaxHeight().background(StatusGreen))
                        Box(modifier = Modifier.weight(3f).fillMaxHeight().background(Cobre))
                        Box(modifier = Modifier.weight(2f).fillMaxHeight().background(StatusRed))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row {
                        LeyendaPunto("Completadas 18", StatusGreen)
                        Spacer(Modifier.width(16.dp))
                        LeyendaPunto("Pendientes 3", Cobre)
                    }
                    Spacer(Modifier.height(6.dp))
                    LeyendaPunto("Canceladas 2", StatusRed)
                }

                Spacer(Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("INASISTENCIAS CRÍTICAS", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 0.5.sp, modifier = Modifier.weight(1f))
                    Text("Ver todo →", style = MaterialTheme.typography.labelMedium, color = Cobalto)
                }
                Spacer(Modifier.height(8.dp))
                AulixCard {
                    InasistenciaRow("Jiménez, Mateo", "Arquitectura · 4 faltas seguidas")
                    HorizontalDivider(color = Tinta.copy(alpha = 0.07f), modifier = Modifier.padding(vertical = 4.dp))
                    InasistenciaRow("Ramírez, Sofía", "Programación Móvil · 3 faltas")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun KpiCard(value: String, label: String, color: Color, delta: String?, modifier: Modifier = Modifier) {
    AulixCard(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineLarge, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.55f))
        if (delta != null) {
            Spacer(Modifier.height(2.dp))
            Text(delta, style = MaterialTheme.typography.labelSmall, color = StatusGreen)
        }
    }
}

@Composable
private fun BarraRow(b: BarraAsignatura) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(b.nombre, style = MaterialTheme.typography.bodySmall, color = Tinta, modifier = Modifier.width(110.dp))
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)).background(Arena),
        ) {
            Box(modifier = Modifier.fillMaxWidth(b.tasa / 100f).fillMaxHeight().clip(RoundedCornerShape(5.dp)).background(b.color))
        }
        Spacer(Modifier.width(8.dp))
        Text("${b.tasa}%", style = MaterialTheme.typography.labelMedium, color = Tinta, modifier = Modifier.width(34.dp))
    }
}

@Composable
private fun LeyendaPunto(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Tinta.copy(alpha = 0.7f))
    }
}

@Composable
private fun InasistenciaRow(nombre: String, detalle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        UserAvatar(initials = nombre.take(2).uppercase(), role = UserRole.ESTUDIANTE, size = 34)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(nombre, style = MaterialTheme.typography.titleSmall, color = Tinta)
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.5f))
        }
    }
}
