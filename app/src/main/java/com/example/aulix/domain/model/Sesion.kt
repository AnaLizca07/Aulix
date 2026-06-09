package com.example.aulix.domain.model

// Sesión de práctica de laboratorio. Modelo compartido por Docente y Estudiante.
data class Sesion(
    val id: String,
    val titulo: String,          // "Configuración VLAN"
    val asignatura: String,      // "Programación de Redes"
    val grupo: String,           // "21A"
    val laboratorio: String,     // "Lab-B-204"
    val edificio: String = "",   // "Edificio B, P2"
    val horaInicio: String,      // "10:00"
    val horaFin: String,         // "12:00"
    val totalEstudiantes: Int,   // 24
    val auxiliar: String = "",   // "D. Marín (asignado)"
    val practica: String = "",   // "Configuración inicial de VLAN trunk"
    val capacidad: String = "",  // "24 puestos · 18 equipos"
    val estado: EstadoSesion = EstadoSesion.PROGRAMADA,
    val asistentesConfirmados: Int = 0,
    val codigoAsistencia: String = "479231",
    val minutosRestantes: Int = 23,
) {
    val rangoHorario: String get() = "$horaInicio → $horaFin"
    val asignaturaGrupo: String get() = "$asignatura · Grupo $grupo"
}

// Asistente que confirmó presencia en una sesión activa.
data class Asistente(
    val nombre: String,          // "Marín, Diego"
    val hora: String,            // "10:04"
    val esNuevo: Boolean = false,
)
