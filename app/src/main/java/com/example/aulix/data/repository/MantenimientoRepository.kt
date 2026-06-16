package com.example.aulix.data.repository

import com.example.aulix.domain.model.Mantenimiento
import com.example.aulix.domain.model.TipoMantenimiento
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class MantenimientoRepository @Inject constructor(private val supabase: SupabaseClient) {

    private val mantenimientoSelect = """
        *,
        equipo(*, laboratorio(id, nombre, ubicacion)),
        registrador:registrado_por(id, nombre)
    """.trimIndent()

    suspend fun getMantenimientosByEquipo(equipoId: String): Result<List<Mantenimiento>> = runCatching {
        supabase.from("mantenimiento")
            .select(Columns.raw(mantenimientoSelect)) { filter { eq("equipo_id", equipoId) } }
            .decodeList()
    }

    suspend fun programar(
        equipoId: String,
        tipo: TipoMantenimiento,
        fechaProgramada: String,
        horaProgramada: String,
        tecnicoAsignado: String,
        observaciones: String,
    ): Result<Mantenimiento> = runCatching {
        val uid    = supabase.auth.currentUserOrNull()!!.id
        val tipoStr = when (tipo) {
            TipoMantenimiento.PREVENTIVO -> "preventivo"
            TipoMantenimiento.CORRECTIVO -> "correctivo"
            TipoMantenimiento.CALIBRACION -> "predictivo"
        }
        supabase.from("mantenimiento").insert(
            mapOf(
                "equipo_id"        to equipoId,
                "registrado_por"   to uid,
                "tipo"             to tipoStr,
                "fecha_programada" to fechaProgramada,
                "hora_programada"  to horaProgramada,
                "tecnico_asignado" to tecnicoAsignado,
                "observaciones"    to observaciones,
            )
        ) { select(Columns.raw(mantenimientoSelect)) }.decodeSingle<Mantenimiento>().also {
            // Marcar el equipo como en mantenimiento
            supabase.from("equipo").update({ set("estado", "en_mantenimiento") }) {
                filter { eq("id", equipoId) }
            }
        }
    }
}
