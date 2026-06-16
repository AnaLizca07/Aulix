package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// Evento en la línea de tiempo de una incidencia (solo en memoria, sin tabla propia)
data class EventoIncidencia(
    val hora: String,
    val descripcion: String,
    val autor: String,
)

@Serializable
data class Incidencia(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    // La columna en BD es `severidad`; en la app se llama `prioridad`
    @SerialName("severidad") val prioridad: PrioridadIncidencia = PrioridadIncidencia.MEDIA,
    val estado: EstadoIncidencia = EstadoIncidencia.ABIERTA,
    @SerialName("created_at") val createdAt: String = "",
    // SELECT incidencia(*, equipo(*, laboratorio(id, nombre)),
    //   reportante:reportado_por(id, nombre, rol:rol_id(nombre)),
    //   asignado:asignado_a(id, nombre))
    val equipo: Equipo = Equipo(),
    @SerialName("reportante") val reportanteEmbedido: UsuarioEmbedido? = null,
    @SerialName("asignado")   val asignadoEmbedido: UsuarioEmbedido? = null,
    // Sin tabla propia — se construye en memoria con los cambios de estado
    @Transient val lineaTiempo: List<EventoIncidencia> = emptyList(),
) {
    // Propiedades de la UI existente — delegadas a los objetos embebidos
    val reportadoPor: String  get() = reportanteEmbedido?.nombre ?: ""
    val rolReportante: String get() = reportanteEmbedido?.rol?.nombre ?: ""
    val asignadoA: String?    get() = asignadoEmbedido?.nombre

    val fecha: String      get() = if (createdAt.length >= 10) createdAt.take(10) else ""
    val horaReporte: String get() = if (createdAt.length >= 16) createdAt.substring(11, 16) else ""
}
