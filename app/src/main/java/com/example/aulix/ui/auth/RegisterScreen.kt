package com.example.aulix.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import com.example.aulix.domain.model.AcademicPrograms
import com.example.aulix.domain.model.DocumentTypes
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.*
import com.example.aulix.ui.theme.StatusGreen

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.registerState.collectAsState()
    var step by remember { mutableIntStateOf(1) }

    LaunchedEffect(state.registeredUser) {
        state.registeredUser?.let { onRegisterSuccess(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(52.dp))

            // Botón atrás + encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    IconButton(onClick = if (step == 1) onNavigateToLogin else ({ step = 1 })) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Atrás",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "AULIX · NUEVO USUARIO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(16.dp))

            // Progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "PASO $step DE 2 · ${if (step == 1) "TUS DATOS" else "ROL Y SEGURIDAD"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = if (step == 1) "50%" else "100%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(8.dp))
            StepProgressBar(currentStep = step, totalSteps = 2)

            Spacer(Modifier.height(28.dp))

            if (step == 1) {
                RegisterStep1(state = state, viewModel = viewModel)
            } else {
                RegisterStep2(state = state, viewModel = viewModel)
            }

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.error!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(120.dp)) // espacio para el botón fijo
        }

        // Botón y enlace fijo al fondo
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (step == 1) {
                AulixButton(
                    text = "Continuar",
                    onClick = { if (viewModel.validateStep1()) step = 2 },
                    enabled = viewModel.validateStep1(),
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) { append("¿Ya tienes cuenta? ") }
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) { append("Inicia sesión") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                AulixButton(
                    text = "Crear cuenta",
                    onClick = viewModel::register,
                    isLoading = state.isLoading,
                    enabled = state.selectedRole != null && state.termsAccepted
                            && state.password.isNotBlank() && state.confirmPassword.isNotBlank(),
                    leadingIcon = {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { step = 1 }) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) { append("← Volver") }
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) { append(" al paso anterior") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterStep1(state: RegisterUiState, viewModel: AuthViewModel) {
    AulixTextField(
        value = state.fullName,
        onValueChange = viewModel::onRegFullNameChange,
        label = "NOMBRE COMPLETO",
        placeholder = "Carolina Gómez Restrepo",
    )
    Spacer(Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AulixTextField(
            value = state.document,
            onValueChange = viewModel::onRegDocumentChange,
            label = "DOCUMENTO",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f),
        )
        AulixDropdown(
            value = state.documentType,
            onValueChange = viewModel::onRegDocumentTypeChange,
            label = "TIPO",
            options = DocumentTypes.options,
            modifier = Modifier.width(110.dp),
        )
    }

    Spacer(Modifier.height(16.dp))

    // Correo con badge VÁLIDO
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "CORREO INSTITUCIONAL",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onRegEmailChange,
            singleLine = true,
            isError = state.emailValid == false,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = { Text("c.gomez@cue.edu.co", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = if (state.emailValid == true) ({
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StatusGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("VÁLIDO", style = MaterialTheme.typography.labelSmall, color = StatusGreen)
                }
            }) else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Spacer(Modifier.height(16.dp))

    AulixDropdown(
        value = state.program,
        onValueChange = viewModel::onRegProgramChange,
        label = "PROGRAMA / DEPENDENCIA",
        options = AcademicPrograms.options,
    )

    Spacer(Modifier.height(16.dp))

    InfoBox(text = "Solo aceptamos correos institucionales @cue.edu.co. El siguiente paso define tu rol.")
}

@Composable
private fun RegisterStep2(state: RegisterUiState, viewModel: AuthViewModel) {
    // Grid 2x2 de roles
    Text("TU ROL EN LA UNIVERSIDAD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))

    val roles = listOf(UserRole.DOCENTE, UserRole.ESTUDIANTE, UserRole.AUXILIAR, UserRole.SOPORTE_TECNICO)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        roles.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { role ->
                    RoleCard(
                        role = role,
                        selected = state.selectedRole == role,
                        onClick = { viewModel.onRegRoleSelect(role) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Si la fila tiene 1 solo elemento, rellena
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    // Contraseñas lado a lado
    Text("SEGURIDAD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Contraseña", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onRegPasswordChange,
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Confirmar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onRegConfirmPasswordChange,
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    // Barra de fortaleza
    val strength = viewModel.passwordStrengthLabel()
    if (strength.isNotBlank()) {
        Spacer(Modifier.height(6.dp))
        PasswordStrengthBar(password = state.password)
        Spacer(Modifier.height(4.dp))
        Text(strength, style = MaterialTheme.typography.labelSmall, color = StatusGreen)
    }

    Spacer(Modifier.height(16.dp))

    // Términos
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Checkbox(
                checked = state.termsAccepted,
                onCheckedChange = viewModel::onTermsAcceptedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                buildAnnotatedString {
                    append("Acepto los ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)) { append("términos de uso") }
                    append(" y el ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)) { append("tratamiento de datos") }
                    append(" según la normatividad vigente.")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun PasswordStrengthBar(password: String) {
    val segments = 4
    val filled = when {
        password.length >= 9 && password.any { it.isUpperCase() } && password.any { it.isDigit() } -> 4
        password.length >= 7 -> 3
        password.length >= 5 -> 2
        password.isNotEmpty() -> 1
        else -> 0
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
        repeat(segments) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i < filled) StatusGreen else MaterialTheme.colorScheme.outline),
            )
        }
    }
}

@Composable
private fun RoleCard(
    role: UserRole,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = modifier
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Radio button visual
            Spacer(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (selected) 5.dp else 1.5.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape,
                    ),
            )
            Column {
                Text(
                    text = role.displayName().replace(" de laboratorio", ""),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = role.description(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
