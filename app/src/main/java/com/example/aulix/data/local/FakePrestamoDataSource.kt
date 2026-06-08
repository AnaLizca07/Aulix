package com.example.aulix.data.local

import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoEquipo
import com.example.aulix.domain.model.EstadoPrestamo
import com.example.aulix.domain.model.Prestamo

object FakePrestamoDataSource {

    private val equipos = mutableListOf(
        Equipo("eq1", "Osciloscopio Tektronix", "Tektronix",
            "TBS-1052B-EDU #04", EstadoEquipo.PRESTADO, "LAB-B-204",
            "D. Pérez · vence 11:30"),
        Equipo("eq2", "Osciloscopio Rigol", "Rigol",
            "DS-1054Z #07", EstadoEquipo.DISPONIBLE, "LAB-B-204",
            "Mantenimiento al día"),
        Equipo("eq3", "Osciloscopio Tektronix", "Tektronix",
            "TBS-1052B-EDU #08", EstadoEquipo.REPARACION, "LAB-B-204",
            "OT-2841 · desde 18 may"),
        Equipo("eq4", "Multímetro Fluke", "Fluke",
            "FLK-117 #02", EstadoEquipo.DISPONIBLE, "LAB-B-204", ""),
        Equipo("eq5", "Router Cisco", "Cisco",
            "CIS-2901 #01", EstadoEquipo.PRESTADO, "LAB-B-204",
            "A. Salazar · vence 12:15"),
        Equipo("eq6", "Analizador de red", "Keysight",
            "ANR-500 #01", EstadoEquipo.DISPONIBLE, "LAB-B-204", ""),
        Equipo("eq7", "Cable HDMI 5m", "Generic",
            "HDMI-5M #03", EstadoEquipo.DISPONIBLE, "LAB-B-204", "")
    )

    private val prestamos = mutableListOf(
        Prestamo("p1", equipos[0], "Pérez, Laura", "E-2003047",
            "Programación de Redes", "10:14", "13:00", 2,
            "Diego Marín", EstadoPrestamo.ACTIVO, true, "2026-05-22"),
        Prestamo("p2", equipos[3], "Vargas, José", "E-2001823",
            "Electrónica", "09:42", "10:27", 1,
            "Diego Marín", EstadoPrestamo.DEVUELTO, true, "2026-05-22"),
        Prestamo("p3", equipos[4], "Salazar, Ana", "E-2002341",
            "Redes", "09:15", "12:15", 3,
            "Diego Marín", EstadoPrestamo.ACTIVO, true, "2026-05-22"),
        Prestamo("p4", equipos[5], "García, J.", "E-2001100",
            "Electrónica", "16:30", "17:45", 1,
            "Diego Marín", EstadoPrestamo.DEVUELTO, true, "2026-05-21"),
        Prestamo("p5", equipos[6], "Torres, M.", "E-2002200",
            "Redes", "14:08", "15:08", 1,
            "Diego Marín", EstadoPrestamo.DEVUELTO, true, "2026-05-21")
    )

    fun getEquipos(): List<Equipo> = equipos
    fun getEquipoById(id: String): Equipo? = equipos.find { it.id == id }
    fun getPrestamosRecientes(): List<Prestamo> = prestamos
    fun getKPIs(): Triple<Int, Int, Int> {
        val disponibles = equipos.count { it.estado == EstadoEquipo.DISPONIBLE }
        val prestados = equipos.count { it.estado == EstadoEquipo.PRESTADO }
        val reparacion = equipos.count { it.estado == EstadoEquipo.REPARACION }
        return Triple(disponibles, prestados, reparacion)
    }

    fun getPrestamosAgrupados(): Map<String, List<Prestamo>> =
        prestamos.groupBy { it.fecha }

    fun registrarPrestamo(
        equipo: Equipo,
        destinatarioNombre: String,
        destinatarioId: String,
        destinatarioPrograma: String,
        duracionHoras: Int,
        responsable: String
    ): Prestamo {
        val index = equipos.indexOfFirst { it.id == equipo.id }
        if (index != -1) {
            equipos[index] = equipos[index].copy(estado = EstadoEquipo.PRESTADO)
        }
        val nuevoPrestamo = Prestamo(
            id = "p${prestamos.size + 1}",
            equipo = equipo,
            destinatarioNombre = destinatarioNombre,
            destinatarioId = destinatarioId,
            destinatarioPrograma = destinatarioPrograma,
            horaInicio = obtenerHoraActual(),
            horaDevolucion = calcularDevolucion(duracionHoras),
            duracionHoras = duracionHoras,
            responsable = responsable,
            estado = EstadoPrestamo.ACTIVO,
            sinNovedad = true,
            fecha = obtenerFechaActual()
        )
        prestamos.add(0, nuevoPrestamo)
        return nuevoPrestamo
    }

    fun devolverEquipo(prestamoId: String) {
        val index = prestamos.indexOfFirst { it.id == prestamoId }
        if (index != -1) {
            val prestamo = prestamos[index]
            prestamos[index] = prestamo.copy(estado = EstadoPrestamo.DEVUELTO)
            val equipoIndex = equipos.indexOfFirst { it.id == prestamo.equipo.id }
            if (equipoIndex != -1) {
                equipos[equipoIndex] = equipos[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
            }
        }
    }

    private fun obtenerHoraActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%02d:%02d".format(cal.get(java.util.Calendar.HOUR_OF_DAY),
                                   cal.get(java.util.Calendar.MINUTE))
    }

    private fun calcularDevolucion(horas: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.HOUR_OF_DAY, horas)
        return "%02d:%02d".format(cal.get(java.util.Calendar.HOUR_OF_DAY),
                                   cal.get(java.util.Calendar.MINUTE))
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
