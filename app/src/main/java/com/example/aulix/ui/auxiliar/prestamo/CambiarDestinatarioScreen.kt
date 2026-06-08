package com.example.aulix.ui.auxiliar.prestamo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.Destinatario
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.*

private val destinatariosMock = listOf(
    Destinatario("Pérez, Laura",    "E-2003047", "Programación de Redes"),
    Destinatario("Vargas, José",    "E-2001823", "Electrónica"),
    Destinatario("Salazar, Ana",    "E-2002341", "Redes"),
    Destinatario("García, Juan",    "E-2001100", "Electrónica"),
    Destinatario("Torres, María",   "E-2002200", "Redes"),
    Destinatario("Ramírez, Carlos", "E-2004501", "Ing. Software"),
    Destinatario("López, Diana",    "E-2003892", "Ing. Software"),
    Destinatario("Martínez, Pedro", "E-2002765", "Electrónica"),
    Destinatario("Gómez, Carolina", "E-2001234", "Programación de Redes"),
    Destinatario("Herrera, Luis",   "E-2004123", "Redes")
)

@Composable
fun CambiarDestinatarioScreen(
    onBack: () -> Unit,
    onSeleccionar: (Destinatario) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtrados = remember(query) {
        if (query.isBlank()) destinatariosMock
        else destinatariosMock.filter {
            it.nombre.contains(query, ignoreCase = true) ||
            it.id.contains(query, ignoreCase = true)
        }
    }

    Scaffold(containerColor = Lienzo) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Lienzo)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Tinta
                    )
                }
                Column {
                    Text(
                        text = "HU 12 · PASO 2",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cobre,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Seleccionar destinatario",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Tinta
                    )
                }
            }

            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = {
                    Text(
                        text = "Buscar por nombre o ID...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Tinta.copy(alpha = 0.4f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Tinta.copy(alpha = 0.5f)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderLight,
                    focusedBorderColor = Cobalto,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${filtrados.size} ESTUDIANTES",
                style = MaterialTheme.typography.labelSmall,
                color = Tinta.copy(alpha = 0.5f),
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                items(filtrados, key = { it.id }) { destinatario ->
                    DestinatarioRow(
                        destinatario = destinatario,
                        onClick = { onSeleccionar(destinatario) }
                    )
                    HorizontalDivider(
                        color = Tinta.copy(alpha = 0.07f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinatarioRow(
    destinatario: Destinatario,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            initials = destinatario.nombre
                .split(",", " ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() },
            role = UserRole.ESTUDIANTE
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = destinatario.nombre,
                style = MaterialTheme.typography.titleSmall,
                color = Tinta
            )
            Text(
                text = "${destinatario.id} · ${destinatario.programa}",
                style = MaterialTheme.typography.bodySmall,
                color = Tinta.copy(alpha = 0.5f)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Tinta.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}
