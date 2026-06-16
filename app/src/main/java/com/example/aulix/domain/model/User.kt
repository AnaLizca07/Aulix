package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: String = "",
    // `nombre` en BD → `fullName` en la app
    @SerialName("nombre") val fullName: String = "",
    val email: String = "",
    @SerialName("documento") val document: String = "",
    @SerialName("programa")  val program: String = "",
    val activo: Boolean = true,
    // Embedding: SELECT *, rol(id, nombre) FROM usuario
    val rol: RolEmbedido? = null,
    // @Transient: el SDK no lo deserializa; el repositorio lo rellena con copy()
    @Transient val role: UserRole = UserRole.ESTUDIANTE,
) {
    val initials: String
        get() = fullName.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}
