package com.example.aulix.domain.model

data class Mantenimiento(
    val id: String,
    val equipo: Equipo,
    val tipo: TipoMantenimiento,
    val fechaProgramada: String,
    val horaProgramada: String,
    val tecnicoAsignado: String,
    val observaciones: String,
    val registradoPor: String,
    val fechaRegistro: String
)
