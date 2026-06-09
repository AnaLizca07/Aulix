package com.example.aulix.domain.model

data class Prestamo(
    val id: String,
    val equipo: Equipo,
    val destinatarioNombre: String,
    val destinatarioId: String,
    val destinatarioPrograma: String,
    val horaInicio: String,
    val horaDevolucion: String,
    val duracionHoras: Int,
    val responsable: String,
    val estado: EstadoPrestamo,
    val sinNovedad: Boolean = true,
    val fecha: String
)
