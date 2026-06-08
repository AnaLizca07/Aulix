package com.example.aulix.domain.model

enum class EstadoAsistencia { ASISTIO, TARDE, FALTA }

// Registro histórico de asistencia del estudiante a una sesión.
data class RegistroAsistencia(
    val id: String,
    val sesion: String,      // "Configuración VLAN"
    val asignatura: String,  // "Programación de Redes"
    val laboratorio: String, // "Lab-B-204"
    val fecha: String,       // "JUE 22 MAY"
    val hora: String,        // "10:04"
    val via: String,         // "QR" | "Código"
    val estado: EstadoAsistencia,
)
