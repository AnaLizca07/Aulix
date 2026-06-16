package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Prestamo(
    val id: String = "",
    // SELECT prestamo(*, equipo(*, laboratorio(nombre)),
    //   solicitante:solicitante_id(id, nombre, programa),
    //   auxiliar:auxiliar_id(id, nombre))
    val equipo: Equipo = Equipo(),
    @SerialName("solicitante") val solicitanteEmbedido: UsuarioEmbedido? = null,
    @SerialName("auxiliar")    val auxiliarEmbedido: UsuarioEmbedido? = null,
    @SerialName("sin_novedad") val sinNovedad: Boolean = true,
    val observaciones: String? = null,
    val estado: EstadoPrestamo = EstadoPrestamo.ACTIVO,
    @SerialName("fecha_prestamo")   val fechaPrestamo: String = "",
    @SerialName("fecha_devolucion") val fechaDevolucion: String? = null,
    @Transient val duracionHoras: Int = 2,
) {
    // Propiedades UI existentes delegadas a objetos embebidos
    val destinatarioNombre: String   get() = solicitanteEmbedido?.nombre ?: ""
    val destinatarioId: String       get() = solicitanteEmbedido?.id ?: ""
    val destinatarioPrograma: String get() = solicitanteEmbedido?.programa ?: ""
    val responsable: String          get() = auxiliarEmbedido?.nombre ?: ""
    val horaInicio: String           get() = fechaPrestamo.safeSubstring(11, 16)
    val horaDevolucion: String       get() = (fechaDevolucion ?: "").safeSubstring(11, 16)
    val fecha: String                get() = fechaPrestamo.take(10)
}

private fun String.safeSubstring(start: Int, end: Int): String =
    if (length >= end) substring(start, end) else ""
