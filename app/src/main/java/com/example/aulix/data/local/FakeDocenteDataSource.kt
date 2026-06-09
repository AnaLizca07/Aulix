package com.example.aulix.data.local

import com.example.aulix.domain.model.Asistente
import com.example.aulix.domain.model.EstadoSesion
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.Evidencia
import com.example.aulix.domain.model.Sesion

// Datos quemados del flujo Docente. Reemplazar con llamadas a API REST.
object FakeDocenteDataSource {

    // ── Sesión próxima / activa del día ────────────────────────────────────────
    private var sesionActual = Sesion(
        id = "RF-09",
        titulo = "Configuración VLAN",
        asignatura = "Programación de Redes",
        grupo = "21A",
        laboratorio = "Lab-B-204",
        edificio = "Edificio B, P2",
        horaInicio = "10:00",
        horaFin = "12:00",
        totalEstudiantes = 24,
        auxiliar = "D. Marín (asignado)",
        practica = "Configuración inicial de VLAN trunk",
        capacidad = "24 puestos · 18 equipos",
        estado = EstadoSesion.PROGRAMADA,
        asistentesConfirmados = 18,
        codigoAsistencia = "479231",
        minutosRestantes = 23,
    )

    fun getSesionActual(): Sesion = sesionActual

    fun abrirSesion(horaReal: String) {
        sesionActual = sesionActual.copy(estado = EstadoSesion.ACTIVA, horaInicio = horaReal)
    }

    fun cerrarSesion() {
        sesionActual = sesionActual.copy(estado = EstadoSesion.CERRADA)
    }

    // ── Asistentes confirmados en vivo ───────────────────────────────────────────
    fun getAsistentesRecientes(): List<Asistente> = listOf(
        Asistente("Marín, Diego", "10:04", esNuevo = true),
        Asistente("Pérez, Laura", "10:04", esNuevo = true),
        Asistente("Vargas, José", "10:03"),
        Asistente("Salazar, Ana", "10:03"),
    )

    // ── Evidencias de la sesión ──────────────────────────────────────────────────
    fun getEvidencias(): List<Evidencia> = listOf(
        Evidencia("Setup inicial", "10:05", "Foto"),
        Evidencia("Topología red", "10:32", "Foto"),
        Evidencia("Falla switch 03", "11:08", "Foto", nota = "adjunta a #2847"),
        Evidencia("Configuración final", "11:52", "Foto"),
        Evidencia("Nota de cierre", "11:58", "Nota de voz", esNotaVoz = true),
    )

    // ── Agenda del docente ────────────────────────────────────────────────────────
    fun getLaboratorios(): List<String> = listOf("Lab-A-102", "Lab-B-204", "Lab-C-305", "Lab-D-110")

    private val agenda = mutableListOf(
        EventoAgenda("ev1", "10:00", "2h", "Configuración VLAN", "Lab-B-204 · Grupo 21A",
            "JUEVES 22 · HOY", "Lab-B-204", grupo = "21A", enCurso = true, colorHex = 0xFF2C5BA8),
        EventoAgenda("ev2", "14:00", "1.5h", "Asesoría individual", "Lab-B-204 · Tutorías",
            "JUEVES 22 · HOY", "Lab-B-204", grupo = "Tutorías", colorHex = 0xFF0F2742),
        EventoAgenda("ev3", "16:00", "2h", "Big Data — Práctica 3", "Lab-C-305 · Grupo 22B",
            "JUEVES 22 · HOY", "Lab-C-305", grupo = "22B", colorHex = 0xFFB36A2E),
        EventoAgenda("ev4", "14:00", "2h", "Móvil — Práctica 6", "Lab-A-102 · Grupo 21A",
            "VIERNES 23", "Lab-A-102", grupo = "21A", colorHex = 0xFF0F2742),
    )

    fun getAgenda(): List<EventoAgenda> = agenda.toList()

    fun getEventoById(id: String): EventoAgenda? = agenda.find { it.id == id }

    // Inserta (id en blanco) o actualiza un evento de la agenda.
    fun guardarEvento(evento: EventoAgenda) {
        val index = agenda.indexOfFirst { it.id == evento.id }
        if (index != -1) agenda[index] = evento
        else agenda.add(evento.copy(id = "ev${agenda.size + 1}"))
    }
}
