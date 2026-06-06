package com.example.aulix.ui.auth

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakeAuthDataSource
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ── Estado del login ─────────────────────────────────────────────────────────
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val keepSession: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedUser: User? = null,
)

// ── Estado del registro ──────────────────────────────────────────────────────
data class RegisterUiState(
    // Paso 1
    val fullName: String = "",
    val document: String = "",
    val documentType: String = "C.C.",
    val email: String = "",
    val program: String = "",
    val emailValid: Boolean? = null,

    // Paso 2
    val selectedRole: UserRole? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null,
    val registeredUser: User? = null,
)

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // ── Login ────────────────────────────────────────────────────────────────
    fun onLoginEmailChange(value: String) = _loginState.update { it.copy(email = value, error = null) }
    fun onLoginPasswordChange(value: String) = _loginState.update { it.copy(password = value, error = null) }
    fun onKeepSessionChange(value: Boolean) = _loginState.update { it.copy(keepSession = value) }

    fun login() {
        val state = _loginState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _loginState.update { it.copy(error = "Completa todos los campos.") }
            return
        }
        _loginState.update { it.copy(isLoading = true, error = null) }
        val user = FakeAuthDataSource.login(state.email, state.password)
        if (user != null) {
            _loginState.update { it.copy(isLoading = false, loggedUser = user) }
        } else {
            _loginState.update { it.copy(isLoading = false, error = "Correo o contraseña incorrectos.") }
        }
    }

    fun resetLoginState() = _loginState.update { LoginUiState() }

    // ── Registro — paso 1 ───────────────────────────────────────────────────
    fun onRegFullNameChange(v: String) = _registerState.update { it.copy(fullName = v) }
    fun onRegDocumentChange(v: String) = _registerState.update { it.copy(document = v) }
    fun onRegDocumentTypeChange(v: String) = _registerState.update { it.copy(documentType = v) }
    fun onRegProgramChange(v: String) = _registerState.update { it.copy(program = v) }

    fun onRegEmailChange(v: String) {
        val institutional = FakeAuthDataSource.isInstitutionalEmail(v)
        val available = if (institutional) FakeAuthDataSource.isEmailAvailable(v) else false
        _registerState.update {
            it.copy(email = v, emailValid = if (v.isBlank()) null else institutional && available)
        }
    }

    fun validateStep1(): Boolean {
        val s = _registerState.value
        return s.fullName.isNotBlank()
                && s.document.isNotBlank()
                && s.email.isNotBlank()
                && s.emailValid == true
                && s.program.isNotBlank()
    }

    // ── Registro — paso 2 ───────────────────────────────────────────────────
    fun onRegRoleSelect(role: UserRole) = _registerState.update { it.copy(selectedRole = role) }
    fun onRegPasswordChange(v: String) = _registerState.update { it.copy(password = v) }
    fun onRegConfirmPasswordChange(v: String) = _registerState.update { it.copy(confirmPassword = v) }
    fun onTermsAcceptedChange(v: Boolean) = _registerState.update { it.copy(termsAccepted = v) }

    fun passwordStrengthLabel(): String {
        val p = _registerState.value.password
        return when {
            p.length >= 9 && p.any { it.isUpperCase() } && p.any { it.isDigit() } -> "FUERTE · ${p.length} caracteres · mayúsculas · números"
            p.length >= 6 -> "MEDIA · aumenta la seguridad"
            p.isNotEmpty() -> "DÉBIL · muy corta"
            else -> ""
        }
    }

    fun register() {
        val s = _registerState.value
        when {
            s.selectedRole == null -> { _registerState.update { it.copy(error = "Selecciona tu rol.") }; return }
            s.password.length < 6  -> { _registerState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres.") }; return }
            s.password != s.confirmPassword -> { _registerState.update { it.copy(error = "Las contraseñas no coinciden.") }; return }
            !s.termsAccepted -> { _registerState.update { it.copy(error = "Debes aceptar los términos de uso.") }; return }
        }
        _registerState.update { it.copy(isLoading = true, error = null) }
        // Simula registro exitoso — en producción llamar al API
        val newUser = User(
            id = "99",
            fullName = s.fullName,
            email = s.email,
            document = s.document,
            program = s.program,
            role = s.selectedRole!!
        )
        _registerState.update { it.copy(isLoading = false, registeredUser = newUser) }
    }

    fun resetRegisterState() = _registerState.update { RegisterUiState() }
}
