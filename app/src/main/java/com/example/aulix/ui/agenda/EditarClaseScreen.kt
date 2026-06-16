package com.example.aulix.ui.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aulix.domain.model.AsignaturaCatalog
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.LaboratorioCatalog
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixDropdown
import com.example.aulix.ui.components.AulixTextField
import com.example.aulix.ui.components.InfoBox
import com.example.aulix.ui.docente.components.DetailHeader
import com.example.aulix.ui.theme.BorderLight
import com.example.aulix.ui.theme.Cobalto
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.Tinta
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DURACIONES = listOf("1h", "1.5h", "2h", "3h")
private val LOCALE_ES = Locale.forLanguageTag("es-ES")

private fun millisToDatePair(millis: Long): Pair<String, String> {
    val fecha = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    val label = fecha.format(DateTimeFormatter.ofPattern("EEEE d", LOCALE_ES)).uppercase(LOCALE_ES)
    val iso = fecha.toString()
    return (if (fecha == LocalDate.now()) "$label · HOY" else label) to iso
}

// ── Formulario compartido para crear o editar una clase de la agenda ───────────
@Composable
fun EditarClaseScreen(
    evento: EventoAgenda?,
    asignaturas: List<AsignaturaCatalog>,
    laboratorios: List<LaboratorioCatalog>,
    defaultColorHex: Long,
    onBack: () -> Unit,
    onGuardar: (EventoAgenda) -> Unit,
) {
    val esNueva = evento == null

    var titulo by remember { mutableStateOf(evento?.titulo ?: "") }
    var asignaturaId by remember { mutableStateOf(evento?.asignaturaId ?: "") }
    var laboratorioId by remember { mutableStateOf(evento?.laboratorioId ?: laboratorios.firstOrNull()?.id ?: "") }
    var grupo by remember { mutableStateOf(evento?.grupo ?: "") }
    var diaLabel by remember { mutableStateOf(evento?.dia ?: "") }
    var diaIso by remember { mutableStateOf(evento?.fechaIso ?: "") }
    var hora by remember { mutableStateOf(evento?.hora ?: "") }
    var duracion by remember { mutableStateOf(evento?.duracion ?: "2h") }

    val asignaturaSeleccionada = asignaturas.find { it.id == asignaturaId }
    val laboratorioSeleccionado = laboratorios.find { it.id == laboratorioId }

    val valido = titulo.isNotBlank() && hora.isNotBlank() && asignaturaId.isNotBlank() && laboratorioId.isNotBlank()

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                AulixButton(
                    text = if (esNueva) "Agregar clase" else "Guardar cambios",
                    onClick = {
                        val labNombre = laboratorioSeleccionado?.nombre ?: ""
                        val detalle = if (grupo.isBlank()) labNombre else "$labNombre · Grupo $grupo"
                        onGuardar(
                            EventoAgenda(
                                id = evento?.id ?: "",
                                hora = hora,
                                duracion = duracion,
                                titulo = titulo.trim(),
                                detalle = detalle,
                                dia = diaLabel,
                                laboratorio = labNombre,
                                grupo = grupo.trim(),
                                enCurso = evento?.enCurso ?: false,
                                colorHex = evento?.colorHex ?: defaultColorHex,
                                asignaturaId = asignaturaId,
                                laboratorioId = laboratorioId,
                                fechaIso = diaIso,
                            )
                        )
                    },
                    enabled = valido,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(
                eyebrow = if (esNueva) "AGENDA" else "EDITAR",
                title = if (esNueva) "Nueva clase" else "Editar clase",
                onBack = onBack,
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AulixTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = "TÍTULO DE LA CLASE",
                    placeholder = "Ej. Configuración VLAN",
                )

                // Asignatura dropdown
                AulixDropdown(
                    value = asignaturaSeleccionada?.let { "${it.nombre} (${it.codigo})" } ?: "",
                    onValueChange = { selected ->
                        asignaturaId = asignaturas.find { "${it.nombre} (${it.codigo})" == selected }?.id ?: ""
                    },
                    label = "ASIGNATURA",
                    options = asignaturas.map { "${it.nombre} (${it.codigo})" },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Laboratorio dropdown
                AulixDropdown(
                    value = laboratorioSeleccionado?.nombre ?: "",
                    onValueChange = { selected ->
                        laboratorioId = laboratorios.find { it.nombre == selected }?.id ?: ""
                    },
                    label = "LABORATORIO",
                    options = laboratorios.map { it.nombre },
                    modifier = Modifier.fillMaxWidth(),
                )

                AulixTextField(
                    value = grupo,
                    onValueChange = { grupo = it },
                    label = "GRUPO",
                    placeholder = "Ej. 21A",
                )
                DiaSelector(
                    value = diaLabel,
                    onDiaSeleccionado = { label, iso ->
                        diaLabel = label
                        diaIso = iso
                    },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AulixTextField(
                        value = hora,
                        onValueChange = { hora = it },
                        label = "HORA DE INICIO",
                        placeholder = "10:00",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    AulixDropdown(
                        value = duracion,
                        onValueChange = { duracion = it },
                        label = "DURACIÓN",
                        options = DURACIONES,
                        modifier = Modifier.weight(1f),
                    )
                }
                InfoBox(text = "Los estudiantes matriculados verán esta clase en su agenda.")
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// Campo de día que abre un calendario (Material 3 DatePicker).
// Devuelve label de display (ej. "JUEVES 22 · HOY") e ISO date ("2026-06-17")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaSelector(
    value: String,
    onDiaSeleccionado: (label: String, isoDate: String) -> Unit,
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "DÍA",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                .clickable { mostrarDialogo = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value.ifBlank { "Selecciona una fecha" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isBlank()) Tinta.copy(alpha = 0.4f) else Tinta,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Default.CalendarMonth, contentDescription = "Abrir calendario", tint = Cobalto)
        }
    }

    if (mostrarDialogo) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val (label, iso) = millisToDatePair(millis)
                        onDiaSeleccionado(label, iso)
                    }
                    mostrarDialogo = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
