package com.example.aulix.data.local

import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.EventoIncidencia
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.model.PrioridadIncidencia

object FakeIncidenciaDataSource {

    private val incidencias = mutableListOf(
        Incidencia(
            id = "INC-001",
            titulo = "Pantalla no enciende",
            descripcion = "El osciloscopio no enciende al presionar el botón de encendido. La pantalla permanece apagada y no hay señal de vida del equipo. Se verificó el cable de poder y el tomacorriente, ambos funcionan correctamente.",
            prioridad = PrioridadIncidencia.ALTA,
            estado = EstadoIncidencia.ABIERTA,
            equipo = FakePrestamoDataSource.getEquipoById("eq1")!!,
            reportadoPor = "Carlos García",
            rolReportante = "Docente",
            fecha = "2026-06-07",
            horaReporte = "09:15",
            asignadoA = "Julio Ramírez",
            lineaTiempo = listOf(
                EventoIncidencia("09:15", "Incidencia reportada por el docente Carlos García.", "Sistema"),
                EventoIncidencia("09:22", "Asignada al técnico Julio Ramírez para revisión.", "Sistema")
            )
        ),
        Incidencia(
            id = "INC-002",
            titulo = "Puerto de red no responde",
            descripcion = "El router no responde en los puertos Fa0/0 y Fa0/1. Los estudiantes no pueden conectarse a la red de práctica configurada para la sesión de Redes II. El LED de actividad permanece apagado.",
            prioridad = PrioridadIncidencia.ALTA,
            estado = EstadoIncidencia.EN_ATENCION,
            equipo = FakePrestamoDataSource.getEquipoById("eq5")!!,
            reportadoPor = "María Vásquez",
            rolReportante = "Auxiliar",
            fecha = "2026-06-07",
            horaReporte = "10:45",
            asignadoA = "Julio Ramírez",
            lineaTiempo = listOf(
                EventoIncidencia("10:45", "Incidencia reportada por el auxiliar María Vásquez.", "Sistema"),
                EventoIncidencia("10:50", "Asignada al técnico Julio Ramírez.", "Sistema"),
                EventoIncidencia("11:03", "Se inició diagnóstico: verificando configuración de interfaces y estado del IOS.", "Julio Ramírez")
            )
        ),
        Incidencia(
            id = "INC-003",
            titulo = "Sonda de medición dañada",
            descripcion = "La sonda del canal CH1 tiene el clip de tierra roto. La medición en canal 1 es inestable y genera lecturas erróneas. El osciloscopio funciona correctamente en CH2.",
            prioridad = PrioridadIncidencia.MEDIA,
            estado = EstadoIncidencia.ABIERTA,
            equipo = FakePrestamoDataSource.getEquipoById("eq2")!!,
            reportadoPor = "Pedro Alvarado",
            rolReportante = "Estudiante",
            fecha = "2026-06-06",
            horaReporte = "14:30",
            asignadoA = "Julio Ramírez",
            lineaTiempo = listOf(
                EventoIncidencia("14:30", "Incidencia reportada por el estudiante Pedro Alvarado.", "Sistema"),
                EventoIncidencia("14:35", "Asignada al técnico Julio Ramírez para revisión.", "Sistema")
            )
        ),
        Incidencia(
            id = "INC-004",
            titulo = "Cable HDMI no transmite imagen",
            descripcion = "El cable HDMI no transmite imagen al conectarse al proyector del laboratorio. Se observan artefactos visuales y pérdida intermitente de señal durante la clase magistral.",
            prioridad = PrioridadIncidencia.BAJA,
            estado = EstadoIncidencia.RESUELTA,
            equipo = FakePrestamoDataSource.getEquipoById("eq7")!!,
            reportadoPor = "Laura Pérez",
            rolReportante = "Docente",
            fecha = "2026-06-05",
            horaReporte = "08:00",
            asignadoA = "Julio Ramírez",
            lineaTiempo = listOf(
                EventoIncidencia("08:00", "Incidencia reportada por la docente Laura Pérez.", "Sistema"),
                EventoIncidencia("08:10", "Asignada al técnico Julio Ramírez.", "Sistema"),
                EventoIncidencia("09:00", "Revisado: el conector del cable tenía acumulación de polvo en los pines. Se limpió con aire comprimido y se verificó conexión.", "Julio Ramírez"),
                EventoIncidencia("09:05", "Incidencia marcada como resuelta. Equipo operativo.", "Sistema")
            )
        ),
        Incidencia(
            id = "INC-005",
            titulo = "Pantalla con líneas horizontales",
            descripcion = "La pantalla LCD del multímetro presenta líneas horizontales intermitentes que dificultan la lectura de las mediciones. El problema ocurre especialmente al medir voltaje en DC.",
            prioridad = PrioridadIncidencia.MEDIA,
            estado = EstadoIncidencia.ABIERTA,
            equipo = FakePrestamoDataSource.getEquipoById("eq4")!!,
            reportadoPor = "Ana Salazar",
            rolReportante = "Estudiante",
            fecha = "2026-06-08",
            horaReporte = "11:20",
            asignadoA = null,
            lineaTiempo = listOf(
                EventoIncidencia("11:20", "Incidencia reportada por la estudiante Ana Salazar.", "Sistema"),
                EventoIncidencia("11:21", "Sin asignar — en espera de técnico disponible.", "Sistema")
            )
        )
    )

    fun getIncidencias(): List<Incidencia> = incidencias

    fun getIncidenciaById(id: String): Incidencia? = incidencias.find { it.id == id }

    fun getIncidenciasPorEquipo(equipoId: String): List<Incidencia> =
        incidencias.filter { it.equipo.id == equipoId }

    fun getKPIs(): Triple<Int, Int, Int> {
        val abiertas = incidencias.count { it.estado == EstadoIncidencia.ABIERTA }
        val enAtencion = incidencias.count { it.estado == EstadoIncidencia.EN_ATENCION }
        val hoy = obtenerFechaActual()
        val resueltasHoy = incidencias.count { it.estado == EstadoIncidencia.RESUELTA && it.fecha == hoy }
        return Triple(abiertas, enAtencion, resueltasHoy)
    }

    fun cambiarEstado(incidenciaId: String, nuevoEstado: EstadoIncidencia) {
        val index = incidencias.indexOfFirst { it.id == incidenciaId }
        if (index == -1) return
        val hora = obtenerHoraActual()
        val descripcionEvento = when (nuevoEstado) {
            EstadoIncidencia.ABIERTA -> "Estado cambiado a Abierta."
            EstadoIncidencia.EN_ATENCION -> "Estado cambiado a En atención. Técnico inició revisión."
            EstadoIncidencia.RESUELTA -> "Incidencia marcada como resuelta."
        }
        val nuevaFecha = if (nuevoEstado == EstadoIncidencia.RESUELTA) obtenerFechaActual() else incidencias[index].fecha
        incidencias[index] = incidencias[index].copy(
            estado = nuevoEstado,
            fecha = nuevaFecha,
            lineaTiempo = incidencias[index].lineaTiempo + EventoIncidencia(hora, descripcionEvento, "Sistema")
        )
    }

    fun registrarIncidencia(
        titulo: String,
        descripcion: String,
        prioridad: PrioridadIncidencia,
        equipo: Equipo,
        reportadoPor: String,
        rolReportante: String
    ): Incidencia {
        val hora = obtenerHoraActual()
        val fecha = obtenerFechaActual()
        val nueva = Incidencia(
            id = "INC-${"%03d".format(incidencias.size + 1)}",
            titulo = titulo,
            descripcion = descripcion,
            prioridad = prioridad,
            estado = EstadoIncidencia.ABIERTA,
            equipo = equipo,
            reportadoPor = reportadoPor,
            rolReportante = rolReportante,
            fecha = fecha,
            horaReporte = hora,
            asignadoA = null,
            lineaTiempo = listOf(
                EventoIncidencia(hora, "Incidencia registrada por $reportadoPor.", "Sistema")
            )
        )
        incidencias.add(0, nueva)
        return nueva
    }

    private fun obtenerHoraActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%02d:%02d".format(
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE)
        )
    }

    private fun obtenerFechaActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }
}
