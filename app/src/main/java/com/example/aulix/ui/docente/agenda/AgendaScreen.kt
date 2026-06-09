package com.example.aulix.ui.docente.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.docente.components.DocenteBottomBar
import com.example.aulix.ui.docente.components.DocenteTab
import com.example.aulix.ui.theme.*

// ── HU 08 · Agenda personal ────────────────────────────────────────────────────
@Composable
fun AgendaScreen(
    eventos: List<EventoAgenda>,
    laboratorios: List<String>,
    onHoy: () -> Unit,
    onIndicadores: () -> Unit,
    onPerfil: () -> Unit,
    onNuevaClase: () -> Unit,
    onEditarClase: (String) -> Unit,
) {
    var filtro by remember { mutableStateOf("Todos") }
    var diaSel by remember { mutableStateOf(22) }
    val filtrados = if (filtro == "Todos") eventos else eventos.filter { it.laboratorio == filtro }
    val porDia = filtrados.groupBy { it.dia }

    Scaffold(
        containerColor = Lienzo,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaClase,
                containerColor = Cobalto,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva clase") },
            )
        },
        bottomBar = {
            DocenteBottomBar(
                selected = DocenteTab.AGENDA,
                onHoy = onHoy, onAgenda = {}, onIndicadores = onIndicadores, onPerfil = onPerfil,
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
                CircleIconButton(Icons.Default.Menu, onClick = {}, contentDescription = "Menú")
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MAYO 2026", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                    Text("Mi agenda", style = MaterialTheme.typography.titleLarge, color = Tinta)
                }
                CircleIconButton(Icons.Default.FilterList, onClick = {}, contentDescription = "Filtrar")
            }

            // Filtros por laboratorio
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FiltroChip("Todos", filtro == "Todos") { filtro = "Todos" }
                laboratorios.forEach { lab -> FiltroChip(lab, filtro == lab) { filtro = lab } }
            }

            Spacer(Modifier.height(16.dp))

            // Tira de días de la semana
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val dias = listOf("LUN" to 19, "MAR" to 20, "MIÉ" to 21, "JUE" to 22, "VIE" to 23, "SÁB" to 24)
                dias.forEach { (nombre, num) ->
                    val sel = num == diaSel
                    Column(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (sel) Cobalto else Color.Transparent)
                            .clickable { diaSel = num }.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(nombre, style = MaterialTheme.typography.labelSmall, color = if (sel) Color.White.copy(alpha = 0.8f) else Tinta.copy(alpha = 0.45f))
                        Text("$num", style = MaterialTheme.typography.titleMedium, color = if (sel) Color.White else Tinta, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                porDia.forEach { (dia, lista) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                        if (dia.contains("HOY")) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Cobre))
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(dia, style = MaterialTheme.typography.labelSmall, color = if (dia.contains("HOY")) Cobre else Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                    }
                    lista.forEach { ev -> EventoRow(ev, onClick = { onEditarClase(ev.id) }) }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FiltroChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Tinta else Color.White)
            .then(if (selected) Modifier else Modifier.border(1.dp, BorderLight, RoundedCornerShape(50.dp)))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (selected) Color.White else Tinta.copy(alpha = 0.7f))
    }
}

@Composable
private fun EventoRow(ev: EventoAgenda, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.width(56.dp)) {
            Text(ev.hora, style = MaterialTheme.typography.titleSmall, color = Tinta, fontWeight = FontWeight.SemiBold)
            Text(ev.duracion, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f))
        }
        Spacer(Modifier.width(8.dp))
        Row(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.White)
                .clickable(onClick = onClick)
                .height(IntrinsicSize.Min),
        ) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Color(ev.colorHex)))
            Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ev.titulo, style = MaterialTheme.typography.titleSmall, color = Tinta, fontWeight = FontWeight.SemiBold)
                    Text(ev.detalle, style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.5f))
                }
                if (ev.enCurso) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(StatusGreen.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text("EN CURSO", style = MaterialTheme.typography.labelSmall, color = StatusGreen)
                    }
                }
            }
        }
    }
}
