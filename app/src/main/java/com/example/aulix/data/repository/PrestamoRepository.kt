package com.example.aulix.data.repository

import com.example.aulix.domain.model.Prestamo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class PrestamoRepository @Inject constructor(private val supabase: SupabaseClient) {

    private val prestamoSelect = """
        *,
        equipo(*, laboratorio(id, nombre, ubicacion)),
        solicitante:solicitante_id(id, nombre, programa),
        auxiliar:auxiliar_id(id, nombre)
    """.trimIndent()

    // Préstamos activos recientes
    suspend fun getPrestamosActivos(): Result<List<Prestamo>> = runCatching {
        supabase.from("prestamo")
            .select(Columns.raw(prestamoSelect)) { filter { eq("estado", "activo") } }
            .decodeList()
    }

    // Historial completo
    suspend fun getHistorial(): Result<List<Prestamo>> = runCatching {
        supabase.from("prestamo").select(Columns.raw(prestamoSelect)).decodeList()
    }

    // KPIs para el dashboard del auxiliar
    suspend fun getKPIs(): Result<Triple<Int, Int, Int>> = runCatching {
        val equipos = supabase.from("equipo")
            .select(Columns.raw("estado"))
            .decodeList<EstadoRow>()
        val disponibles  = equipos.count { it.estado == "disponible" }
        val prestados    = equipos.count { it.estado == "prestado" }
        val reparacion   = equipos.count { it.estado == "en_mantenimiento" }
        Triple(disponibles, prestados, reparacion)
    }

    // Registra un nuevo préstamo
    suspend fun registrar(
        equipoId: String,
        solicitanteId: String,
        duracionHoras: Int,
        sinNovedad: Boolean,
        sesionId: String?,
    ): Result<Prestamo> = runCatching {
        val uid = supabase.auth.currentUserOrNull()!!.id
        val devolucionMs = System.currentTimeMillis() + duracionHoras * 3600_000L
        val devolucionIso = java.time.Instant.ofEpochMilli(devolucionMs).toString()

        val payload = buildMap<String, Any?> {
            put("equipo_id",       equipoId)
            put("solicitante_id",  solicitanteId)
            put("auxiliar_id",     uid)
            put("sin_novedad",     sinNovedad)
            put("fecha_devolucion", devolucionIso)
            put("estado",          "activo")
            if (sesionId != null) put("sesion_id", sesionId)
        }
        supabase.from("prestamo").insert(payload) { select(Columns.raw(prestamoSelect)) }.decodeSingle()
    }

    // Devolver equipo
    suspend fun devolver(prestamoId: String): Result<Unit> = runCatching {
        supabase.from("prestamo").update({
            set("estado", "devuelto")
            set("fecha_devolucion", "now()")
        }) {
            filter { eq("id", prestamoId) }
        }
        // Actualiza estado del equipo a 'disponible'
        val row = supabase.from("prestamo")
            .select(Columns.raw("equipo_id")) { filter { eq("id", prestamoId) } }
            .decodeSingle<EquipoIdRow>()
        supabase.from("equipo").update({ set("estado", "disponible") }) {
            filter { eq("id", row.equipoId) }
        }
    }
}

@kotlinx.serialization.Serializable
private data class EstadoRow(val estado: String = "")

@kotlinx.serialization.Serializable
private data class EquipoIdRow(
    @kotlinx.serialization.SerialName("equipo_id") val equipoId: String = "",
)
