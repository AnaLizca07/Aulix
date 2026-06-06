package com.example.aulix.domain.model

enum class UserRole {
    DOCENTE,
    ESTUDIANTE,
    AUXILIAR,
    SOPORTE_TECNICO;

    fun displayName(): String = when (this) {
        DOCENTE          -> "Docente"
        ESTUDIANTE       -> "Estudiante"
        AUXILIAR         -> "Auxiliar de laboratorio"
        SOPORTE_TECNICO  -> "Soporte técnico"
    }

    fun description(): String = when (this) {
        DOCENTE          -> "Gestiona sesiones"
        ESTUDIANTE       -> "Registra asistencia"
        AUXILIAR         -> "Préstamos de equipos"
        SOPORTE_TECNICO  -> "Atiende incidencias"
    }
}
