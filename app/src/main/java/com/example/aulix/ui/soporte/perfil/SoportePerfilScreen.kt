package com.example.aulix.ui.soporte.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.BorderLight
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.RoleSoporte
import com.example.aulix.ui.theme.StatusRed
import com.example.aulix.ui.theme.Tinta

@Composable
fun SoportePerfilScreen(
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
                        text = "SOPORTE TÉCNICO",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoleSoporte,
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
                            role = UserRole.SOPORTE_TECNICO,
                            size = 80
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Tinta,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = Tinta.copy(alpha = 0.55f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StatusChip(
                                label = "SOPORTE",
                                color = RoleSoporte,
                                backgroundColor = RoleSoporte.copy(alpha = 0.12f)
                            )
                        }
                    }
                }

                AulixCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
