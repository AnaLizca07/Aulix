package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mantenimiento(
    val id: String = "",
    // SELECT mantenimiento(*, equipo(*, laboratorio(nombre)),
    //   registrador:registrado_por(id, nombre))
    val equipo: Equipo = Equipo(),
    val tipo: TipoMantenimiento = TipoMantenimiento.PREVENTIVO,
    @SerialName("fecha_programada") val fechaProgramada: String = "",
    @SerialName("hora_programada")  val horaProgramada: String = "09:00",
    @SerialName("tecnico_asignado") val tecnicoAsignado: String = "",
    val observaciones: String? = null,
    @SerialName("registrador")      val registradorEmbedido: UsuarioEmbedido? = null,
    @SerialName("created_at")       val createdAt: String = "",
) {
    val registradoPor: String get() = registradorEmbedido?.nombre ?: ""
    val fechaRegistro: String get() = createdAt.take(10)
}
