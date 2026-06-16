package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("docente")         DOCENTE,
    @SerialName("estudiante")      ESTUDIANTE,
    @SerialName("auxiliar")        AUXILIAR,
    @SerialName("soporte_tecnico") SOPORTE_TECNICO;

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
