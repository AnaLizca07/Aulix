package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Tipos embebidos que PostgREST devuelve en joins.
// Usados en @SerialName("alias") val campo: EmbedXxx? dentro de los domain models.

@Serializable
data class LaboratorioEmbedido(
    val id: String = "",
    val nombre: String = "",
    val ubicacion: String? = null,
)

@Serializable
data class AsignaturaEmbedida(
    val id: String = "",
    val nombre: String = "",
    val codigo: String = "",
)

@Serializable
data class RolEmbedido(
    val id: String = "",
    val nombre: String = "",
) {
    fun toUserRole(): UserRole = when (nombre) {
        "docente"         -> UserRole.DOCENTE
        "estudiante"      -> UserRole.ESTUDIANTE
        "auxiliar"        -> UserRole.AUXILIAR
        "soporte_tecnico" -> UserRole.SOPORTE_TECNICO
        else              -> UserRole.ESTUDIANTE
    }
}

@Serializable
data class UsuarioEmbedido(
    val id: String = "",
    val nombre: String = "",
    val programa: String? = null,
    val rol: RolEmbedido? = null,
)

@Serializable
data class ReservaEmbedida(
    val id: String = "",
    val titulo: String? = null,
    val practica: String? = null,
    val grupo: String? = null,
    @SerialName("hora_inicio") val horaInicio: String = "",
    @SerialName("hora_fin") val horaFin: String = "",
    val fecha: String = "",
    @SerialName("color_hex") val colorHex: String? = null,
    val asignatura: AsignaturaEmbedida? = null,
    val laboratorio: LaboratorioEmbedido? = null,
)
