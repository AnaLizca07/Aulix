package com.example.aulix.domain.model

// Asignatura matriculada por el estudiante.
data class Asignatura(
    val codigo: String,        // "PR2031"
    val nombre: String,        // "Programación de Redes"
    val grupo: String,         // "21A"
    val proxima: String,       // "Hoy 10:00"
    val esHoy: Boolean = false,
    val colorHex: Long,        // color de la barra lateral
)
