package com.example.aulix.ui.docente.incidencias

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aulix.domain.model.Sesion
import com.example.aulix.domain.model.TipoIncidencia
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.docente.components.CircleIconButton
import com.example.aulix.ui.theme.*

// ── HU 06 · Reportar incidencia ────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportarIncidenciaScreen(
    sesion: Sesion,
    onClose: () -> Unit,
    onEnviar: () -> Unit,
    viewModel: ReportarIncidenciaViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var tipo by remember { mutableStateOf(TipoIncidencia.EQUIPO) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        viewModel.onFotoTomada(exito)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.onImagenSeleccionada(uri)
    }

    LaunchedEffect(state.enviado) {
        if (state.enviado) onEnviar()
    }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Column(modifier = Modifier.background(Lienzo).padding(horizontal = 20.dp, vertical = 16.dp)) {
                if (state.error != null) {
                    Text(state.error ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }
                val requiereEquipo = tipo == TipoIncidencia.EQUIPO && state.equipoSeleccionado == null
                val sinDescripcion = state.descripcion.isBlank()
                if (requiereEquipo || sinDescripcion) {
                    Text(
                        text = when {
                            requiereEquipo -> "Selecciona el equipo afectado"
                            else -> "Escribe una descripción"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Cobre,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                AulixButton(
                    text = if (state.isLoading) "Enviando..." else "Enviar a soporte técnico",
                    onClick = { viewModel.enviar(tipo, sesion.id.ifBlank { null }) },
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
                        Text("${sesion.titulo} · ${sesion.laboratorio}", style = MaterialTheme.typography.titleSmall, color = Tinta)
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

                if (tipo == TipoIncidencia.EQUIPO) {
                Spacer(Modifier.height(20.dp))
                Text("EQUIPO AFECTADO", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                ) {
                    Row(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, if (state.equipoSeleccionado != null) Cobalto else BorderLight, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Science, null, tint = if (state.equipoSeleccionado != null) Cobalto else Tinta.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            if (state.equipoSeleccionado != null) {
                                Text(state.equipoSeleccionado!!.nombre, style = MaterialTheme.typography.titleSmall, color = Tinta)
                                Text("SN: ${state.equipoSeleccionado!!.codigo}", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f))
                            } else {
                                Text("Seleccionar equipo...", style = MaterialTheme.typography.bodyMedium, color = Tinta.copy(alpha = 0.4f))
                            }
                        }
                        Icon(Icons.Default.ArrowDropDown, null, tint = Tinta.copy(alpha = 0.4f))
                    }
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        if (state.equipos.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Cargando equipos...", style = MaterialTheme.typography.bodySmall) },
                                onClick = {},
                            )
                        }
                        state.equipos.forEach { equipo ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(equipo.nombre, style = MaterialTheme.typography.bodyMedium)
                                        Text("SN: ${equipo.codigo}", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.5f))
                                    }
                                },
                                onClick = {
                                    viewModel.onEquipoSeleccionado(equipo)
                                    dropdownExpanded = false
                                },
                            )
                        }
                    }
                }
                } // end if EQUIPO

                Spacer(Modifier.height(20.dp))
                Text("DESCRIPCIÓN", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    placeholder = { Text("Describe el problema en detalle…", style = MaterialTheme.typography.bodySmall, color = Tinta.copy(alpha = 0.4f)) },
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
                val imagenes = state.imagenesUri
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    imagenes.forEach { uri ->
                        Box(modifier = Modifier.weight(1f).height(70.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .clickable { viewModel.eliminarImagen(uri) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                    val remaining = 3 - imagenes.size
                    if (remaining >= 2) {
                        AddThumbClickable(Icons.Default.CameraAlt, Modifier.weight(1f)) {
                            cameraLauncher.launch(viewModel.crearUriCamara())
                        }
                        AddThumbClickable(Icons.Default.Image, Modifier.weight(1f)) {
                            galleryLauncher.launch("image/*")
                        }
                        Box(modifier = Modifier.weight(1f).height(70.dp).clip(RoundedCornerShape(10.dp)).background(Arena))
                    } else if (remaining == 1) {
                        AddThumbClickable(Icons.Default.CameraAlt, Modifier.weight(1f)) {
                            cameraLauncher.launch(viewModel.crearUriCamara())
                        }
                    }
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
private fun AddThumbClickable(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.height(70.dp).clip(RoundedCornerShape(10.dp))
            .border(1.dp, Cobalto.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = Cobalto, modifier = Modifier.size(22.dp))
    }
}
