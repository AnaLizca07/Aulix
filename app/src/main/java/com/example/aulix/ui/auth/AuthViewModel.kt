package com.example.aulix.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.AuthRepository
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.domain.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val keepSession: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedUser: User? = null,
)

data class RegisterUiState(
    val fullName: String = "",
    val document: String = "",
    val documentType: String = "C.C.",
    val email: String = "",
    val program: String = "",
    val emailValid: Boolean? = null,
    val selectedRole: UserRole? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registeredUser: User? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
) : ViewModel() {

    private val _loginState    = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // ── Login ────────────────────────────────────────────────────────────────
    fun onLoginEmailChange(value: String)    = _loginState.update { it.copy(email = value, error = null) }
    fun onLoginPasswordChange(value: String) = _loginState.update { it.copy(password = value, error = null) }
    fun onKeepSessionChange(value: Boolean)  = _loginState.update { it.copy(keepSession = value) }

    fun login() {
        val state = _loginState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _loginState.update { it.copy(error = "Completa todos los campos.") }
            return
        }
        _loginState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepo.signIn(state.email.trim(), state.password)
                .onSuccess { user ->
                    UserSession.login(user)
                    _loginState.update { it.copy(isLoading = false, loggedUser = user) }
                }
                .onFailure { e ->
                    _loginState.update { it.copy(isLoading = false, error = "Correo o contraseña incorrectos.") }
                }
        }
    }

    fun resetLoginState() = _loginState.update { LoginUiState() }

    // ── Registro — paso 1 ───────────────────────────────────────────────────
    fun onRegFullNameChange(v: String)     = _registerState.update { it.copy(fullName = v) }
    fun onRegDocumentChange(v: String)     = _registerState.update { it.copy(document = v) }
    fun onRegDocumentTypeChange(v: String) = _registerState.update { it.copy(documentType = v) }
    fun onRegProgramChange(v: String)      = _registerState.update { it.copy(program = v) }

    fun onRegEmailChange(v: String) {
        val institutional = v.endsWith("@cue.edu.co")
        _registerState.update {
            it.copy(email = v, emailValid = if (v.isBlank()) null else institutional)
        }
    }

    fun validateStep1(): Boolean {
        val s = _registerState.value
        return s.fullName.isNotBlank() && s.document.isNotBlank()
                && s.email.isNotBlank() && s.emailValid == true && s.program.isNotBlank()
    }

    // ── Registro — paso 2 ───────────────────────────────────────────────────
    fun onRegRoleSelect(role: UserRole)         = _registerState.update { it.copy(selectedRole = role) }
    fun onRegPasswordChange(v: String)          = _registerState.update { it.copy(password = v) }
    fun onRegConfirmPasswordChange(v: String)   = _registerState.update { it.copy(confirmPassword = v) }
    fun onTermsAcceptedChange(v: Boolean)       = _registerState.update { it.copy(termsAccepted = v) }

    fun passwordStrengthLabel(): String {
        val p = _registerState.value.password
        return when {
            p.length >= 9 && p.any { it.isUpperCase() } && p.any { it.isDigit() } ->
                "FUERTE · ${p.length} caracteres · mayúsculas · números"
            p.length >= 6 -> "MEDIA · aumenta la seguridad"
            p.isNotEmpty() -> "DÉBIL · muy corta"
            else -> ""
        }
    }

    fun register() {
        val s = _registerState.value
        when {
            s.selectedRole == null  -> { _registerState.update { it.copy(error = "Selecciona tu rol.") }; return }
            s.password.length < 6   -> { _registerState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres.") }; return }
            s.password != s.confirmPassword -> { _registerState.update { it.copy(error = "Las contraseñas no coinciden.") }; return }
            !s.termsAccepted        -> { _registerState.update { it.copy(error = "Debes aceptar los términos de uso.") }; return }
        }
        _registerState.update { it.copy(isLoading = true, error = null) }
        val rolNombre = when (s.selectedRole!!) {
            UserRole.DOCENTE         -> "docente"
            UserRole.ESTUDIANTE      -> "estudiante"
            UserRole.AUXILIAR        -> "auxiliar"
            UserRole.SOPORTE_TECNICO -> "soporte_tecnico"
        }
        viewModelScope.launch {
            authRepo.signUp(
                email     = s.email.trim(),
                password  = s.password,
                nombre    = s.fullName.trim(),
                rolNombre = rolNombre,
                documento = s.document.trim().ifBlank { null },
                programa  = s.program.trim().ifBlank { null },
            ).onSuccess { user ->
                UserSession.login(user)
                _registerState.update { it.copy(isLoading = false, registeredUser = user) }
            }.onFailure { e ->
                _registerState.update { it.copy(isLoading = false, error = e.message ?: "Error al registrar.") }
            }
        }
    }

    fun resetRegisterState() = _registerState.update { RegisterUiState() }
}
