package com.example.aulix.domain.model

// Bloque de la agenda (docente / estudiante), agrupado por día.
data class EventoAgenda(
    val id: String,          // identificador para edición
    val hora: String,        // "10:00"
    val duracion: String,    // "2h"
    val titulo: String,      // "Configuración VLAN"
    val detalle: String,     // "Lab-B-204 · Grupo 21A"
    val dia: String,         // "JUEVES 22 · HOY"
    val laboratorio: String, // "Lab-B-204"
    val grupo: String = "",  // "21A"
    val enCurso: Boolean = false,
    val colorHex: Long,      // color de la barra lateral
)
