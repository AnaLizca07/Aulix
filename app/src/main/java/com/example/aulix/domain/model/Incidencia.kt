package com.example.aulix.domain.model

data class EventoIncidencia(
    val hora: String,
    val descripcion: String,
    val autor: String
)

data class Incidencia(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val prioridad: PrioridadIncidencia,
    val estado: EstadoIncidencia,
    val equipo: Equipo,
    val reportadoPor: String,
    val rolReportante: String,
    val fecha: String,
    val horaReporte: String,
    val asignadoA: String?,
    val lineaTiempo: List<EventoIncidencia>
)
