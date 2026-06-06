package com.example.aulix.data.local

import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole

// Usuarios quemados para desarrollo. Reemplazar con llamadas a API REST.
object FakeAuthDataSource {

    private val users = listOf(
        User(
            id = "1",
            fullName = "Carolina Gómez Restrepo",
            email = "c.gomez@cue.edu.co",
            document = "1045892310",
            program = "Ingeniería de Software",
            role = UserRole.DOCENTE
        ),
        User(
            id = "2",
            fullName = "Mateo Vargas León",
            email = "m.vargas@cue.edu.co",
            document = "1023847561",
            program = "Programación de Redes",
            role = UserRole.ESTUDIANTE
        ),
        User(
            id = "3",
            fullName = "Diego Marín",
            email = "d.marin@cue.edu.co",
            document = "1034782910",
            program = "Lab-B-204",
            role = UserRole.AUXILIAR
        ),
        User(
            id = "4",
            fullName = "Juana Ruiz",
            email = "j.ruiz@cue.edu.co",
            document = "1056732841",
            program = "Soporte TI",
            role = UserRole.SOPORTE_TECNICO
        ),
    )

    // Contraseña fija para todos los usuarios de prueba
    private const val FAKE_PASSWORD = "Password1"

    fun login(email: String, password: String): User? {
        if (password != FAKE_PASSWORD) return null
        return users.find { it.email.equals(email.trim(), ignoreCase = true) }
    }

    fun isEmailAvailable(email: String): Boolean =
        users.none { it.email.equals(email.trim(), ignoreCase = true) }

    fun isInstitutionalEmail(email: String): Boolean =
        email.trim().endsWith("@cue.edu.co", ignoreCase = true)
}
