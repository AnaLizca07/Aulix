package com.example.aulix.ui.estudiante.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.aulix.ui.theme.Cielo
import com.example.aulix.ui.theme.RoleEstudiante
import com.example.aulix.ui.theme.Tinta

enum class EstudianteTab { HOY, AGENDA, HISTORIAL, PERFIL }

@Composable
fun EstudianteBottomBar(
    selected: EstudianteTab,
    onHoy: () -> Unit,
    onAgenda: () -> Unit,
    onHistorial: () -> Unit,
    onPerfil: () -> Unit,
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        Item("Hoy", Icons.Default.Home, selected == EstudianteTab.HOY, onHoy)
        Item("Agenda", Icons.Default.CalendarMonth, selected == EstudianteTab.AGENDA, onAgenda)
        Item("Historial", Icons.Default.History, selected == EstudianteTab.HISTORIAL, onHistorial)
        Item("Perfil", Icons.Default.Person, selected == EstudianteTab.PERFIL, onPerfil)
    }
}

@Composable
private fun RowScope.Item(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = RoleEstudiante,
            selectedTextColor = RoleEstudiante,
            unselectedIconColor = Tinta.copy(alpha = 0.4f),
            unselectedTextColor = Tinta.copy(alpha = 0.4f),
            indicatorColor = Cielo,
        ),
    )
}
