package com.example.aulix.domain.model

// Evidencia (foto o nota de voz) adjunta a una sesión.
data class Evidencia(
    val titulo: String,      // "Setup inicial"
    val hora: String,        // "10:05"
    val tipo: String,        // "Foto" | "Nota de voz"
    val nota: String = "",   // "adjunta a #2847"
    val esNotaVoz: Boolean = false,
)
