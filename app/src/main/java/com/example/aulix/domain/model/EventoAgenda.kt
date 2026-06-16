package com.example.aulix.domain.model

// Bloque de la agenda (docente / estudiante), agrupado por día.
data class EventoAgenda(
    val id: String,               // identificador para edición (reserva.id tras guardar)
    val hora: String,             // "10:00"
    val duracion: String,         // "2h"
    val titulo: String,           // "Configuración VLAN"
    val detalle: String,          // "Lab-B-204 · Grupo 21A"
    val dia: String,              // display label "JUEVES 22 · HOY"
    val laboratorio: String,      // nombre del laboratorio
    val grupo: String = "",       // "21A"
    val enCurso: Boolean = false,
    val colorHex: Long,           // color de la barra lateral
    val asignaturaId: String = "",  // UUID para insertar en Supabase
    val laboratorioId: String = "", // UUID para insertar en Supabase
    val fechaIso: String = "",      // "2026-06-17" para insertar en Supabase
)
