package com.example.aulix.ui.docente.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.ui.theme.Arena
import com.example.aulix.ui.theme.Cielo
import com.example.aulix.ui.theme.Cobalto
import com.example.aulix.ui.theme.Cobre
import com.example.aulix.ui.theme.Tinta
import androidx.compose.ui.graphics.Color

// Pestañas de la barra inferior del Docente.
enum class DocenteTab { HOY, AGENDA, INDICADORES, PERFIL }

@Composable
fun DocenteBottomBar(
    selected: DocenteTab,
    onHoy: () -> Unit,
    onAgenda: () -> Unit,
    onIndicadores: () -> Unit,
    onPerfil: () -> Unit,
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        DocenteNavItem("Hoy", Icons.Default.Home, selected == DocenteTab.HOY, onHoy)
        DocenteNavItem("Agenda", Icons.Default.CalendarMonth, selected == DocenteTab.AGENDA, onAgenda)
        DocenteNavItem("Indicadores", Icons.Default.Speed, selected == DocenteTab.INDICADORES, onIndicadores)
        DocenteNavItem("Perfil", Icons.Default.Person, selected == DocenteTab.PERFIL, onPerfil)
    }
}

@Composable
private fun RowScope.DocenteNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Cobalto,
            selectedTextColor = Cobalto,
            unselectedIconColor = Tinta.copy(alpha = 0.4f),
            unselectedTextColor = Tinta.copy(alpha = 0.4f),
            indicatorColor = Cielo,
        ),
    )
}

// Header de pantalla de detalle: botón atrás circular + eyebrow + título centrado.
@Composable
fun DetailHeader(
    eyebrow: String,
    title: String,
    eyebrowColor: Color = Cobre,
    onBack: () -> Unit,
    leadingIsClose: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(
            icon = if (leadingIsClose) Icons.Default.Close else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            onClick = onBack,
            contentDescription = "Volver",
        )
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (eyebrow.isNotBlank()) {
                Text(
                    text = eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    color = eyebrowColor,
                    letterSpacing = 1.sp,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Tinta,
            )
        }
        if (trailing != null) trailing() else Spacer(Modifier.size(40.dp))
    }
}

@Composable
fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Arena),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = contentDescription, tint = Tinta)
        }
    }
}
