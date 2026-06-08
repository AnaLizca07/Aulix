package com.example.aulix.ui.auxiliar.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.*

@Composable
fun PerfilScreen(
    user: User?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    if (user == null) return

    Scaffold(containerColor = Lienzo) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AUXILIAR · LAB-B-204",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoleAuxiliar,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Tinta
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AulixCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            initials = user.initials,
                            role = user.role,
                            size = 80
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Tinta
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = Tinta.copy(alpha = 0.55f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val (rolTexto, rolColor) = when (user.role) {
                                UserRole.AUXILIAR        -> "AUXILIAR" to RoleAuxiliar
                                UserRole.DOCENTE         -> "DOCENTE" to RoleDocente
                                UserRole.ESTUDIANTE      -> "ESTUDIANTE" to RoleEstudiante
                                UserRole.SOPORTE_TECNICO -> "SOPORTE" to RoleSoporte
                            }
                            StatusChip(
                                label = rolTexto,
                                color = rolColor,
                                backgroundColor = rolColor.copy(alpha = 0.12f)
                            )
                        }
                    }
                }

                AulixCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoRow(label = "Laboratorio", value = "LAB-B-204")
                        HorizontalDivider(color = BorderLight, thickness = 1.dp)
                        InfoRow(label = "Programa", value = user.program)
                        HorizontalDivider(color = BorderLight, thickness = 1.dp)
                        InfoRow(label = "Documento", value = user.document)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onLogout,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Cerrar sesión",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Tinta.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Tinta
        )
    }
}
