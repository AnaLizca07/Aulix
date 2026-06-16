package com.example.aulix.data.repository

import com.example.aulix.domain.model.RegistroAsistencia
import com.example.aulix.domain.model.EstadoAsistencia
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class AsistenciaRepository @Inject constructor(private val supabase: SupabaseClient) {

    // Registra asistencia del estudiante autenticado a una sesión
    suspend fun registrar(sesionId: String, metodo: String): Result<String> = runCatching {
        val uid = supabase.auth.currentUserOrNull()!!.id
        supabase.from("asistencia").insert(
            mapOf(
                "sesion_id"    to sesionId,
                "estudiante_id" to uid,
                "metodo"       to metodo,
                "presente"     to true,
            )
        )
        sesionId
    }

    // Historial de asistencia del estudiante autenticado
    suspend fun getHistorial(): Result<List<RegistroAsistencia>> = runCatching {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return@runCatching emptyList()
        supabase.from("asistencia")
            .select(Columns.raw("""
                id, hora_registro, metodo,
                sesion:sesion_id(
                    codigo_asistencia,
                    reserva(
                        titulo, grupo,
                        asignatura(nombre),
                        laboratorio(nombre)
                    )
                )
            """.trimIndent())) {
                filter { eq("estudiante_id", uid) }
            }
            .decodeList<AsistenciaHistorialRow>()
            .map { it.toDomain() }
    }

    // Comprobante de la última asistencia registrada
    suspend fun getComprobanteReciente(sesionId: String): Result<Comprobante?> = runCatching {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return@runCatching null
        val row = supabase.from("asistencia")
            .select(Columns.raw("id, hora_registro, metodo, sesion:sesion_id(reserva(titulo, asignatura(nombre), laboratorio(nombre)))")) {
                filter {
                    eq("sesion_id",    sesionId)
                    eq("estudiante_id", uid)
                }
            }
            .decodeSingleOrNull<AsistenciaHistorialRow>() ?: return@runCatching null
        Comprobante(
            sesionNombre = row.sesion?.reserva?.titulo ?: "",
            asignatura   = row.sesion?.reserva?.asignatura?.nombre ?: "",
            laboratorio  = row.sesion?.reserva?.laboratorio?.nombre ?: "",
            hora         = row.horaRegistro.safeTime(),
            metodo       = row.metodo,
            codigoSesion = sesionId.take(8).uppercase(),
        )
    }

    data class Comprobante(
        val sesionNombre: String,
        val asignatura: String,
        val laboratorio: String,
        val hora: String,
        val metodo: String,
        val codigoSesion: String,
    )
}

@kotlinx.serialization.Serializable
private data class AsistenciaHistorialRow(
    val id: String = "",
    @kotlinx.serialization.SerialName("hora_registro") val horaRegistro: String = "",
    val metodo: String = "",
    val sesion: SesionResumenRow? = null,
) {
    fun toDomain() = RegistroAsistencia(
        id          = id,
        sesion      = sesion?.reserva?.titulo ?: "",
        asignatura  = sesion?.reserva?.asignatura?.nombre ?: "",
        laboratorio = sesion?.reserva?.laboratorio?.nombre ?: "",
        fecha       = horaRegistro.take(10),
        hora        = horaRegistro.safeTime(),
        via         = if (metodo == "qr") "QR" else "Código",
        estado      = EstadoAsistencia.ASISTIO,
    )
}

@kotlinx.serialization.Serializable
private data class SesionResumenRow(
    @kotlinx.serialization.SerialName("codigo_asistencia") val codigoAsistencia: String? = null,
    val reserva: ReservaResumenRow? = null,
)

@kotlinx.serialization.Serializable
private data class ReservaResumenRow(
    val titulo: String? = null,
    val grupo: String? = null,
    val asignatura: com.example.aulix.domain.model.AsignaturaEmbedida? = null,
    val laboratorio: com.example.aulix.domain.model.LaboratorioEmbedido? = null,
)

private fun String.safeTime(): String =
    if (length >= 16) substring(11, 16) else take(5)
