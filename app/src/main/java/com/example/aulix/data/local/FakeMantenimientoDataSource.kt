package com.example.aulix.data.local

import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.Mantenimiento
import com.example.aulix.domain.model.TipoMantenimiento

object FakeMantenimientoDataSource {

    private val mantenimientos = mutableListOf<Mantenimiento>()

    fun registrarMantenimiento(
        equipo: Equipo,
        tipo: TipoMantenimiento,
        fechaProgramada: String,
        horaProgramada: String,
        tecnicoAsignado: String,
        observaciones: String,
        registradoPor: String
    ): Mantenimiento {
        val nuevo = Mantenimiento(
            id = "MNT-${"%03d".format(mantenimientos.size + 1)}",
            equipo = equipo,
            tipo = tipo,
            fechaProgramada = fechaProgramada,
            horaProgramada = horaProgramada,
            tecnicoAsignado = tecnicoAsignado,
            observaciones = observaciones,
            registradoPor = registradoPor,
            fechaRegistro = obtenerFechaActual()
        )
        mantenimientos.add(nuevo)
        return nuevo
    }

    fun getMantenimientosPorEquipo(equipoId: String): List<Mantenimiento> =
        mantenimientos.filter { it.equipo.id == equipoId }

    private fun obtenerFechaActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }
}
