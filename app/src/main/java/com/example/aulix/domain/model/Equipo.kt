package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Equipo(
    val id: String = "",
    val nombre: String = "",
    val marca: String? = null,
    // `numero_serie` en BD → `codigo` en la app
    @SerialName("numero_serie") val codigo: String = "",
    val tipo: String? = null,
    val estado: EstadoEquipo = EstadoEquipo.DISPONIBLE,
    // SELECT equipo(*, laboratorio(id, nombre, ubicacion))
    // @SerialName("laboratorio") mapea el objeto embebido del join
    @SerialName("laboratorio") val laboratorioEmbedido: LaboratorioEmbedido? = null,
    @Transient val infoAdicional: String = "",
) {
    // La UI existente accede a `equipo.laboratorio` como String — compat garantizada
    val laboratorio: String get() = laboratorioEmbedido?.nombre ?: ""
}
