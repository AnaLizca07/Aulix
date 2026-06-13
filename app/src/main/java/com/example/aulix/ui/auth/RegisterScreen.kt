package com.example.aulix.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.aulix.R
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.AcademicPrograms
import com.example.aulix.domain.model.DocumentTypes
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.*
import com.example.aulix.ui.theme.*

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

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Header con gradiente ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A3A6B), Cobalto, Color(0xFF3D6BAA))
                    )
                )
        ) {
            // Círculo decorativo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 260.dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.07f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = (-20).dp, y = 120.dp)
                    .clip(CircleShape)
                    .background(Cobre.copy(alpha = 0.3f))
            )

            // Título (sin el botón atrás — se pone como overlay al final)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 108.dp),
            ) {
                Text(
                    text = "AULIX · NUEVO USUARIO",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                    color = Color.White.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                )
            }
        }

        // ── Tarjeta de formulario ─────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(172.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 220.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Indicador de pasos visual
                StepIndicator(currentStep = step)

                Spacer(Modifier.height(28.dp))

                if (step == 1) {
                    RegisterStep1(state = state, viewModel = viewModel)
                } else {
                    RegisterStep2(state = state, viewModel = viewModel)
                }

                if (state.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("⚠", color = MaterialTheme.colorScheme.error)
                        Text(state.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // ── Botón fijo al fondo ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background,
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (step == 1) {
                AulixButton(
                    text = "Continuar",
                    onClick = { if (viewModel.validateStep1()) step = 2 },
                    enabled = viewModel.validateStep1(),
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp))
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

        // ── Botón atrás + logo como overlay (mayor Z-order = recibe toques primero) ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 52.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable(onClick = if (step == 1) onNavigateToLogin else ({ step = 1 })),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Image(
                painter = painterResource(id = R.drawable.logo_uni),
                contentDescription = "Aulix logo",
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
        }
    }
}

// ── Indicador de pasos visual ─────────────────────────────────────────────────
@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Paso 1
        StepBubble(number = 1, label = "Tus datos", active = currentStep == 1, done = currentStep > 1)
        // Línea conectora
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(2.dp)
                .background(
                    if (currentStep > 1) Cobalto else MaterialTheme.colorScheme.outline
                )
        )
        // Paso 2
        StepBubble(number = 2, label = "Rol y seguridad", active = currentStep == 2, done = false)
    }
}

@Composable
private fun StepBubble(number: Int, label: String, active: Boolean, done: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        done   -> Cobalto
                        active -> Cobalto
                        else   -> MaterialTheme.colorScheme.outline
                    }
                ),
        ) {
            if (done) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = "$number",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active || done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Paso 1 ────────────────────────────────────────────────────────────────────
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
        Text("CORREO INSTITUCIONAL",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp))
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
                ) { Text("VÁLIDO", style = MaterialTheme.typography.labelSmall, color = StatusGreen) }
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

// ── Paso 2 ────────────────────────────────────────────────────────────────────
@Composable
private fun RegisterStep2(state: RegisterUiState, viewModel: AuthViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    Text("TU ROL EN LA UNIVERSIDAD",
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))

    val roles = listOf(UserRole.DOCENTE, UserRole.ESTUDIANTE, UserRole.AUXILIAR, UserRole.SOPORTE_TECNICO)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        roles.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { role ->
                    RoleCard(
                        role = role,
                        selected = state.selectedRole == role,
                        onClick = { viewModel.onRegRoleSelect(role) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    // Separador visual
    Spacer(Modifier.height(4.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    Spacer(Modifier.height(20.dp))

    Text("CONTRASEÑA",
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(10.dp))

    OutlinedTextField(
        value = state.password,
        onValueChange = viewModel::onRegPasswordChange,
        singleLine = true,
        placeholder = { Text("Mín. 8 caracteres", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    )

    val strength = viewModel.passwordStrengthLabel()
    if (strength.isNotBlank()) {
        Spacer(Modifier.height(8.dp))
        PasswordStrengthBar(password = state.password)
        Spacer(Modifier.height(4.dp))
        Text(strength, style = MaterialTheme.typography.labelSmall, color = StatusGreen)
    }

    Spacer(Modifier.height(16.dp))

    Text("CONFIRMAR CONTRASEÑA",
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(10.dp))

    val passwordsMatch = state.confirmPassword.isEmpty() || state.password == state.confirmPassword
    OutlinedTextField(
        value = state.confirmPassword,
        onValueChange = viewModel::onRegConfirmPasswordChange,
        singleLine = true,
        placeholder = { Text("Repite tu contraseña", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant) },
        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = !passwordsMatch,
        leadingIcon = {
            Icon(
                imageVector = if (passwordsMatch) Icons.Default.Lock else Icons.Default.Lock,
                contentDescription = null,
                tint = if (passwordsMatch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp),
            )
        },
        trailingIcon = {
            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                Icon(
                    imageVector = if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (confirmVisible) "Ocultar" else "Mostrar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (passwordsMatch) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            unfocusedBorderColor = if (passwordsMatch) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
    if (!passwordsMatch) {
        Spacer(Modifier.height(4.dp))
        Text("Las contraseñas no coinciden",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error)
    }

    Spacer(Modifier.height(16.dp))

    // Caja de términos
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
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

@Composable
private fun PasswordStrengthBar(password: String) {
    val filled = when {
        password.length >= 9 && password.any { it.isUpperCase() } && password.any { it.isDigit() } -> 4
        password.length >= 7 -> 3
        password.length >= 5 -> 2
        password.isNotEmpty() -> 1
        else -> 0
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
        repeat(4) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when {
                            i >= filled -> MaterialTheme.colorScheme.outline
                            filled <= 1 -> StatusRed
                            filled <= 2 -> StatusAmber
                            else        -> StatusGreen
                        }
                    ),
            )
        }
    }
}

// ── Tarjeta de rol con icono y color propio ───────────────────────────────────
private data class RoleMeta(val icon: ImageVector, val color: Color, val bgColor: Color)

@Composable
private fun roleMetaFor(role: UserRole): RoleMeta = when (role) {
    UserRole.DOCENTE         -> RoleMeta(Icons.Default.School,   RoleDocente,    RoleDocente.copy(alpha = 0.1f))
    UserRole.ESTUDIANTE      -> RoleMeta(Icons.Default.Person,   RoleEstudiante, RoleEstudiante.copy(alpha = 0.1f))
    UserRole.AUXILIAR        -> RoleMeta(Icons.Default.Inventory,RoleAuxiliar,   RoleAuxiliar.copy(alpha = 0.1f))
    UserRole.SOPORTE_TECNICO -> RoleMeta(Icons.Default.Build,    RoleSoporte,    RoleSoporte.copy(alpha = 0.1f))
}

@Composable
private fun RoleCard(
    role: UserRole,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val meta = roleMetaFor(role)
    val borderColor = if (selected) meta.color else MaterialTheme.colorScheme.outline
    val bgColor     = if (selected) meta.bgColor else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        modifier = modifier.border(
            width = if (selected) 2.dp else 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(16.dp),
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            // Icono con fondo circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) meta.color.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
            ) {
                Icon(
                    imageVector = meta.icon,
                    contentDescription = null,
                    tint = if (selected) meta.color else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = role.displayName().replace(" de laboratorio", ""),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (selected) meta.color else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = role.description(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
            )
        }
    }
}
