package com.example.aulix.domain.model

data class Equipo(
    val id: String,
    val nombre: String,
    val marca: String,
    val codigo: String,
    val estado: EstadoEquipo,
    val laboratorio: String,
    val infoAdicional: String = ""
)
