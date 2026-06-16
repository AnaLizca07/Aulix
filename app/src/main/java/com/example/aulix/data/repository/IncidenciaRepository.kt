package com.example.aulix.data.repository

import com.example.aulix.domain.model.EventoIncidencia
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.PrioridadIncidencia
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class IncidenciaRepository @Inject constructor(private val supabase: SupabaseClient) {

    private val incidenciaSelect = """
        *,
        equipo(*, laboratorio(id, nombre, ubicacion)),
        reportante:reportado_por(id, nombre, rol:rol_id(nombre)),
        asignado:asignado_a(id, nombre)
    """.trimIndent()

    suspend fun getIncidencias(): Result<List<Incidencia>> = runCatching {
        supabase.from("incidencia")
            .select(Columns.raw(incidenciaSelect))
            .decodeList<Incidencia>()
            .map { it.conLineaTiempoSintetica() }
    }

    suspend fun getIncidenciaById(id: String): Result<Incidencia?> = runCatching {
        supabase.from("incidencia")
            .select(Columns.raw(incidenciaSelect)) { filter { eq("id", id) } }
            .decodeSingleOrNull<Incidencia>()
            ?.conLineaTiempoSintetica()
    }

    // KPIs para el dashboard de Soporte
    suspend fun getKPIs(): Result<Triple<Int, Int, Int>> = runCatching {
        val todas = supabase.from("incidencia")
            .select(Columns.raw("estado, created_at"))
            .decodeList<EstadoFechaRow>()
        val hoy = java.time.LocalDate.now().toString()
        Triple(
            todas.count { it.estado == "abierta" },
            todas.count { it.estado == "en_atencion" },
            todas.count { it.estado == "resuelta" && it.createdAt.take(10) == hoy },
        )
    }

    suspend fun cambiarEstado(id: String, nuevoEstado: EstadoIncidencia): Result<Unit> = runCatching {
        val estadoStr = when (nuevoEstado) {
            EstadoIncidencia.ABIERTA     -> "abierta"
            EstadoIncidencia.EN_ATENCION -> "en_atencion"
            EstadoIncidencia.RESUELTA    -> "resuelta"
        }
        supabase.from("incidencia").update({ set("estado", estadoStr) }) {
            filter { eq("id", id) }
        }
    }

    suspend fun asignar(id: String, tecnicoId: String): Result<Unit> = runCatching {
        supabase.from("incidencia").update({ set("asignado_a", tecnicoId) }) {
            filter { eq("id", id) }
        }
    }

    suspend fun registrar(
        titulo: String,
        descripcion: String,
        severidad: PrioridadIncidencia,
        equipoId: String,
        sesionId: String?,
    ): Result<Incidencia> = runCatching {
        val uid = supabase.auth.currentUserOrNull()!!.id
        val sevStr = when (severidad) {
            PrioridadIncidencia.ALTA  -> "alta"
            PrioridadIncidencia.MEDIA -> "media"
            PrioridadIncidencia.BAJA  -> "baja"
        }
        val payload = buildMap<String, Any?> {
            put("titulo",        titulo)
            put("descripcion",   descripcion)
            put("severidad",     sevStr)
            put("equipo_id",     equipoId)
            put("reportado_por", uid)
            put("estado",        "abierta")
            if (sesionId != null) put("sesion_id", sesionId)
        }
        supabase.from("incidencia").insert(payload) { select(Columns.raw(incidenciaSelect)) }
            .decodeSingle<Incidencia>()
            .conLineaTiempoSintetica()
    }

    // Construye una línea de tiempo sintética a partir de los datos disponibles
    // (no hay tabla evento_incidencia — los eventos se derivan del estado actual)
    private fun Incidencia.conLineaTiempoSintetica(): Incidencia {
        val eventos = mutableListOf<EventoIncidencia>()
        eventos += EventoIncidencia(
            hora        = horaReporte,
            descripcion = "Incidencia reportada por ${reportadoPor.ifBlank { "usuario" }}",
            autor       = "Sistema",
        )
        if (estado == EstadoIncidencia.EN_ATENCION || estado == EstadoIncidencia.RESUELTA) {
            eventos += EventoIncidencia(
                hora        = horaReporte,
                descripcion = "Tomada para atención${asignadoA?.let { " por $it" } ?: ""}",
                autor       = asignadoA ?: "Sistema",
            )
        }
        if (estado == EstadoIncidencia.RESUELTA) {
            eventos += EventoIncidencia(
                hora        = horaReporte,
                descripcion = "Incidencia marcada como resuelta",
                autor       = asignadoA ?: "Sistema",
            )
        }
        return copy(lineaTiempo = eventos)
    }
}

@kotlinx.serialization.Serializable
private data class EstadoFechaRow(
    val estado: String = "",
    @kotlinx.serialization.SerialName("created_at") val createdAt: String = "",
)
