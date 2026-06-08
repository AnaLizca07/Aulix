package com.example.aulix.data.local

import com.example.aulix.domain.model.Asignatura
import com.example.aulix.domain.model.EstadoAsistencia
import com.example.aulix.domain.model.EstadoSesion
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.RegistroAsistencia
import com.example.aulix.domain.model.Sesion

// Datos quemados del flujo Estudiante. Reemplazar con llamadas a API REST.
object FakeEstudianteDataSource {

    // Sesión actualmente abierta por el docente para el estudiante (si la hay).
    fun getSesionActiva(): Sesion = Sesion(
        id = "RF-09",
        titulo = "Configuración VLAN",
        asignatura = "Programación de Redes",
        grupo = "21A",
        laboratorio = "Lab-B-204",
        horaInicio = "10:00",
        horaFin = "12:00",
        totalEstudiantes = 24,
        estado = EstadoSesion.ACTIVA,
        asistentesConfirmados = 18,
        codigoAsistencia = "479231",
    )

    // Asignaturas matriculadas por el estudiante.
    fun getAsignaturas(): List<Asignatura> = listOf(
        Asignatura("PR2031", "Programación de Redes", "21A", "Hoy 10:00",
            esHoy = true, colorHex = 0xFF2C5BA8),
        Asignatura("BD2042", "Bases de Datos", "21A", "Lun 26 · 08:00",
            colorHex = 0xFFB36A2E),
        Asignatura("IA3051", "Inteligencia Artificial", "22A", "Mié 28 · 10:00",
            colorHex = 0xFF059669),
        Asignatura("MO4012", "Programación Móvil", "21A", "Vie 30 · 14:00",
            colorHex = 0xFF0891B2),
    )

    // Comprobante generado tras registrar asistencia.
    data class Comprobante(
        val sesion: String,
        val asignatura: String,
        val laboratorio: String,
        val horaRegistro: String,
        val id: String,
        val confirmados: Int,
    )

    fun registrarAsistencia(): Comprobante = Comprobante(
        sesion = "Configuración VLAN",
        asignatura = "Prog. de Redes · 21A",
        laboratorio = "Lab-B-204",
        horaRegistro = "10:04 · 22 may 2026",
        id = "AS-9F3K2D",
        confirmados = 18,
    )

    fun getLaboratorios(): List<String> = listOf("Lab-A-102", "Lab-B-204", "Lab-C-305", "Lab-D-110")

    // ── Agenda del estudiante (solo sus asignaturas matriculadas) ────────────────
    private val agenda = mutableListOf(
        EventoAgenda("se1", "10:00", "2h", "Configuración VLAN", "Programación de Redes · 21A",
            "JUEVES 22 · HOY", "Lab-B-204", grupo = "21A", enCurso = true, colorHex = 0xFF2C5BA8),
        EventoAgenda("se2", "14:00", "2h", "Modelado E-R", "Bases de Datos · 21A",
            "VIERNES 23", "Lab-A-102", grupo = "21A", colorHex = 0xFFB36A2E),
        EventoAgenda("se3", "10:00", "1.5h", "Redes neuronales", "Inteligencia Artificial · 22A",
            "MIÉRCOLES 28", "Lab-C-305", grupo = "22A", colorHex = 0xFF059669),
        EventoAgenda("se4", "14:00", "2h", "UI con Compose", "Programación Móvil · 21A",
            "VIERNES 30", "Lab-A-102", grupo = "21A", colorHex = 0xFF0891B2),
    )

    // El estudiante solo consulta su agenda (no crea ni edita clases).
    fun getAgenda(): List<EventoAgenda> = agenda.toList()

    // ── Historial de asistencias del estudiante ──────────────────────────────────
    fun getHistorial(): List<RegistroAsistencia> = listOf(
        RegistroAsistencia("h1", "Configuración VLAN", "Programación de Redes", "Lab-B-204",
            "JUE 22 MAY", "10:04", "QR", EstadoAsistencia.ASISTIO),
        RegistroAsistencia("h2", "Normalización BD", "Bases de Datos", "Lab-A-102",
            "LUN 19 MAY", "08:05", "Código", EstadoAsistencia.ASISTIO),
        RegistroAsistencia("h3", "Subredes IP", "Programación de Redes", "Lab-B-204",
            "JUE 15 MAY", "10:18", "QR", EstadoAsistencia.TARDE),
        RegistroAsistencia("h4", "Árboles de decisión", "Inteligencia Artificial", "Lab-C-305",
            "MIÉ 14 MAY", "—", "—", EstadoAsistencia.FALTA),
        RegistroAsistencia("h5", "Layouts en Compose", "Programación Móvil", "Lab-A-102",
            "VIE 09 MAY", "14:02", "QR", EstadoAsistencia.ASISTIO),
        RegistroAsistencia("h6", "Consultas SQL", "Bases de Datos", "Lab-A-102",
            "LUN 05 MAY", "08:01", "Código", EstadoAsistencia.ASISTIO),
    )

    // Resumen: porcentaje de asistencia y conteos.
    fun getResumenHistorial(): Triple<Int, Int, Int> {
        val historial = getHistorial()
        val asistidas = historial.count { it.estado != EstadoAsistencia.FALTA }
        val faltas = historial.count { it.estado == EstadoAsistencia.FALTA }
        val porcentaje = if (historial.isEmpty()) 0 else (asistidas * 100) / historial.size
        return Triple(porcentaje, asistidas, faltas)
    }
}
