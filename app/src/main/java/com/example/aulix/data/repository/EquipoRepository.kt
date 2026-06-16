package com.example.aulix.data.repository

import com.example.aulix.domain.model.Destinatario
import com.example.aulix.domain.model.Equipo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class EquipoRepository @Inject constructor(private val supabase: SupabaseClient) {

    private val equipoSelect = "*, laboratorio(id, nombre, ubicacion)"

    // Lista todos los equipos disponibles
    suspend fun getEquipos(): Result<List<Equipo>> = runCatching {
        supabase.from("equipo").select(Columns.raw(equipoSelect)).decodeList()
    }

    // Equipos disponibles para préstamo (estado = 'disponible')
    suspend fun getEquiposDisponibles(): Result<List<Equipo>> = runCatching {
        supabase.from("equipo")
            .select(Columns.raw(equipoSelect)) { filter { eq("estado", "disponible") } }
            .decodeList()
    }

    // Búsqueda por nombre o número de serie (ilike = case-insensitive)
    suspend fun buscarEquipos(query: String): Result<List<Equipo>> = runCatching {
        if (query.isBlank()) return getEquiposDisponibles()
        supabase.from("equipo")
            .select(Columns.raw(equipoSelect)) {
                filter { ilike("nombre", "%$query%") }
            }
            .decodeList()
    }

    suspend fun getEquipoById(id: String): Result<Equipo> = runCatching {
        supabase.from("equipo")
            .select(Columns.raw(equipoSelect)) { filter { eq("id", id) } }
            .decodeSingle()
    }

    // Actualiza el estado de un equipo (auxiliar / soporte)
    suspend fun actualizarEstado(id: String, nuevoEstado: String): Result<Unit> = runCatching {
        supabase.from("equipo").update({ set("estado", nuevoEstado) }) {
            filter { eq("id", id) }
        }
    }

    // Inventario: todos los equipos con conteo por estado
    suspend fun getInventario(): Result<List<Equipo>> = runCatching {
        supabase.from("equipo").select(Columns.raw(equipoSelect)).decodeList()
    }

    // Destinatarios posibles (usuarios estudiantes/docentes)
    suspend fun getDestinatarios(): Result<List<Destinatario>> = runCatching {
        supabase.from("usuario")
            .select(Columns.raw("id, nombre, programa, rol:rol_id(nombre)")) {
                filter { eq("activo", true) }
            }
            .decodeList<DestinatarioRow>()
            .map { Destinatario(nombre = it.nombre, id = it.id, programa = it.programa ?: "") }
    }
}

@kotlinx.serialization.Serializable
private data class DestinatarioRow(
    val id: String,
    val nombre: String,
    val programa: String? = null,
)
