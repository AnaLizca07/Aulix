package com.example.aulix.data.repository

import com.example.aulix.domain.model.Asistente
import com.example.aulix.domain.model.AsignaturaCatalog
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.LaboratorioCatalog
import com.example.aulix.domain.model.Sesion
import com.example.aulix.domain.model.EstadoSesion
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class SesionRepository @Inject constructor(private val supabase: SupabaseClient) {

    // Select con todos los joins necesarios para reconstruir el modelo Sesion
    private val sesionSelect = """
        *,
        reserva(
            id, titulo, practica, grupo, hora_inicio, hora_fin, fecha, color_hex,
            asignatura(id, nombre, codigo),
            laboratorio(id, nombre, ubicacion)
        )
    """.trimIndent()

    // Sesión activa del docente autenticado (estado = 'activa')
    suspend fun getSesionActiva(): Result<Sesion?> = runCatching {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return@runCatching null
        val rows = supabase.from("sesion")
            .select(Columns.raw(sesionSelect)) {
                filter {
                    eq("docente_id", uid)
                    eq("estado", "activa")
                }
            }
            .decodeList<Sesion>()
        rows.firstOrNull()
    }

    // Cualquier sesión activa en el sistema (para que el estudiante pueda registrar asistencia)
    suspend fun getSesionActivaEstudiante(): Result<Sesion?> = runCatching {
        supabase.from("sesion")
            .select(Columns.raw(sesionSelect)) {
                filter { eq("estado", "activa") }
            }
            .decodeList<Sesion>()
            .firstOrNull()
    }

    // Próxima sesión programada del docente (estado = 'pendiente', fecha >= hoy)
    suspend fun getSesionProgramada(): Result<Sesion?> = runCatching {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return@runCatching null
        android.util.Log.e("SesionRepo", "getSesionProgramada uid=$uid")
        val rows = supabase.from("sesion")
            .select(Columns.raw(sesionSelect)) {
                filter {
                    eq("docente_id", uid)
                    eq("estado", "pendiente")
                }
            }
            .decodeList<Sesion>()
        android.util.Log.e("SesionRepo", "getSesionProgramada rows=${rows.size} reserva=${rows.firstOrNull()?.reserva}")
        rows.firstOrNull()?.let { sesion ->
            sesion.copy(minutosRestantes = calcMinutosRestantes(sesion))
        }
    }

    // Abre la sesión pendiente: actualiza estado → 'activa' y genera código de asistencia
    suspend fun abrirSesion(sesionId: String, codigoAsistencia: String): Result<Sesion> = runCatching {
        supabase.from("sesion").update({
            set("estado", "activa")
            set("codigo_asistencia", codigoAsistencia)
            set("hora_apertura", "now()")
        }) {
            filter { eq("id", sesionId) }
            select(Columns.raw(sesionSelect))
        }.decodeSingle()
    }

    // Crea una reserva con su sesión pendiente asociada (flujo nueva clase)
    suspend fun crearReserva(evento: EventoAgenda): Result<EventoAgenda> = runCatching {
        val uid = supabase.auth.currentUserOrNull()!!.id
        val reserva = supabase.from("reserva")
            .insert(
                ReservaInsert(
                    titulo       = evento.titulo,
                    grupo        = evento.grupo.ifBlank { null },
                    horaInicio   = if (evento.hora.length == 5) "${evento.hora}:00" else evento.hora,
                    horaFin      = calcHoraFin(evento.hora, evento.duracion),
                    fecha        = evento.fechaIso.ifBlank { evento.dia },
                    colorHex     = "#%06X".format(evento.colorHex and 0xFFFFFF),
                    docenteId    = uid,
                    estado       = "pendiente",
                    asignaturaId = evento.asignaturaId,
                    laboratorioId = evento.laboratorioId,
                )
            ) { select() }
            .decodeSingle<ReservaMinima>()
        supabase.from("sesion").insert(
            SesionInsert(
                reservaId        = reserva.id,
                docenteId        = uid,
                codigoAsistencia = "",
                estado           = "pendiente",
            )
        )
        evento.copy(id = reserva.id)
    }

    // Genera un nuevo código de asistencia para una sesión activa
    suspend fun renovarCodigo(sesionId: String, nuevoCodigo: String): Result<Sesion> = runCatching {
        supabase.from("sesion").update({
            set("codigo_asistencia", nuevoCodigo)
        }) {
            filter { eq("id", sesionId) }
            select(Columns.raw(sesionSelect))
        }.decodeSingle()
    }

    // Actualiza una reserva existente
    suspend fun actualizarReserva(evento: EventoAgenda): Result<Unit> = runCatching {
        supabase.from("reserva").update(
            ReservaInsert(
                titulo        = evento.titulo,
                grupo         = evento.grupo.ifBlank { null },
                horaInicio    = if (evento.hora.length == 5) "${evento.hora}:00" else evento.hora,
                horaFin       = calcHoraFin(evento.hora, evento.duracion),
                fecha         = evento.fechaIso.ifBlank { evento.dia },
                colorHex      = "#%06X".format(evento.colorHex and 0xFFFFFF),
                docenteId     = supabase.auth.currentUserOrNull()!!.id,
                estado        = "pendiente",
                asignaturaId  = evento.asignaturaId,
                laboratorioId = evento.laboratorioId,
            )
        ) {
            filter { eq("id", evento.id) }
        }
    }

    // Catálogo de asignaturas
    suspend fun getAsignaturasCatalog(): Result<List<AsignaturaCatalog>> = runCatching {
        supabase.from("asignatura").select(Columns.raw("id, nombre, codigo")).decodeList()
    }

    // Catálogo de laboratorios
    suspend fun getLaboratoriosCatalog(): Result<List<LaboratorioCatalog>> = runCatching {
        supabase.from("laboratorio").select(Columns.raw("id, nombre, ubicacion")).decodeList()
    }

    // Cierra la sesión activa
    suspend fun cerrarSesion(sesionId: String): Result<Unit> = runCatching {
        supabase.from("sesion").update({
            set("estado", "cerrada")
            set("hora_cierre", "now()")
        }) {
            filter { eq("id", sesionId) }
        }
    }

    // Agenda del docente: reservas (pendientes y activas) a partir de hoy
    suspend fun getAgenda(): Result<List<EventoAgenda>> = runCatching {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return@runCatching emptyList()
        val reservaSelect = """
            id, titulo, practica, grupo, hora_inicio, hora_fin, fecha, color_hex, estado,
            asignatura(id, nombre, codigo),
            laboratorio(id, nombre, ubicacion)
        """.trimIndent()
        supabase.from("reserva")
            .select(Columns.raw(reservaSelect)) {
                filter { eq("docente_id", uid) }
            }
            .decodeList<ReservaAgendaRow>()
            .map { it.toEventoAgenda() }
    }

    // Todas las sesiones del docente autenticado (para métricas/indicadores)
    suspend fun getMisSesiones(): Result<List<Sesion>> = runCatching {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return@runCatching emptyList()
        supabase.from("sesion")
            .select(Columns.raw(sesionSelect)) {
                filter { eq("docente_id", uid) }
            }
            .decodeList<Sesion>()
    }

    // Lista de asistentes de una sesión activa (via tabla asistencia)
    suspend fun getAsistentes(sesionId: String): Result<List<Asistente>> = runCatching {
        supabase.from("asistencia")
            .select(Columns.raw("hora_registro, estudiante:estudiante_id(nombre)")) {
                filter { eq("sesion_id", sesionId) }
            }
            .decodeList<AsistenciaRow>()
            .map { row ->
                Asistente(
                    nombre = row.estudiante?.nombre ?: "",
                    hora   = row.horaRegistro.safeTime(),
                )
            }
    }
}

// ── Helpers privados ──────────────────────────────────────────────────────────

private fun calcMinutosRestantes(sesion: com.example.aulix.domain.model.Sesion): Int {
    return try {
        val fecha = sesion.reserva?.fecha
        val hora  = sesion.reserva?.horaInicio
        android.util.Log.e("SesionRepo", "calcMinutos → reserva=${sesion.reserva != null} fecha='$fecha' hora='$hora'")
        if (fecha.isNullOrBlank() || hora.isNullOrBlank()) return 0
        val inicio = java.time.LocalDateTime.of(
            java.time.LocalDate.parse(fecha),
            java.time.LocalTime.parse(hora.take(5)),
        )
        val diff = java.time.Duration.between(java.time.LocalDateTime.now(), inicio).toMinutes()
        android.util.Log.e("SesionRepo", "calcMinutos → inicio=$inicio diff=$diff min")
        diff.toInt().coerceAtLeast(0)
    } catch (e: Exception) {
        android.util.Log.e("SesionRepo", "calcMinutos falló: ${e.message}")
        0
    }
}

private fun calcHoraFin(horaInicio: String, duracion: String): String {
    return try {
        val parts = horaInicio.take(5).split(":")
        val totalMin = parts[0].toInt() * 60 + parts[1].toInt() +
            when {
                duracion.contains("1.5") -> 90
                else -> duracion.replace("h", "").toIntOrNull()?.times(60) ?: 120
            }
        "%02d:%02d:00".format(totalMin / 60 % 24, totalMin % 60)
    } catch (_: Exception) { "00:00:00" }
}

// ── Tipos internos para deserialización de consultas específicas ──────────────

@kotlinx.serialization.Serializable
private data class ReservaInsert(
    val titulo: String,
    val grupo: String? = null,
    @kotlinx.serialization.SerialName("hora_inicio")   val horaInicio: String,
    @kotlinx.serialization.SerialName("hora_fin")      val horaFin: String,
    val fecha: String,
    @kotlinx.serialization.SerialName("color_hex")     val colorHex: String,
    @kotlinx.serialization.SerialName("docente_id")    val docenteId: String,
    val estado: String,
    @kotlinx.serialization.SerialName("asignatura_id") val asignaturaId: String,
    @kotlinx.serialization.SerialName("laboratorio_id") val laboratorioId: String,
)

@kotlinx.serialization.Serializable
private data class SesionInsert(
    @kotlinx.serialization.SerialName("reserva_id")        val reservaId: String,
    @kotlinx.serialization.SerialName("docente_id")        val docenteId: String,
    @kotlinx.serialization.SerialName("codigo_asistencia") val codigoAsistencia: String,
    val estado: String,
)

@kotlinx.serialization.Serializable
private data class ReservaMinima(val id: String)

@kotlinx.serialization.Serializable
private data class ReservaAgendaRow(
    val id: String,
    val titulo: String? = null,
    val practica: String? = null,
    val grupo: String? = null,
    @kotlinx.serialization.SerialName("hora_inicio") val horaInicio: String = "",
    @kotlinx.serialization.SerialName("hora_fin")    val horaFin: String = "",
    val fecha: String = "",
    @kotlinx.serialization.SerialName("color_hex") val colorHex: String? = null,
    val estado: String = "pendiente",
    val asignatura: com.example.aulix.domain.model.AsignaturaEmbedida? = null,
    val laboratorio: com.example.aulix.domain.model.LaboratorioEmbedido? = null,
) {
    fun toEventoAgenda(): EventoAgenda {
        val labNombre  = laboratorio?.nombre ?: ""
        val asigNombre = asignatura?.nombre  ?: ""
        val colorLong  = colorHex?.trimStart('#')?.toLongOrNull(16)?.let { 0xFF000000L or it } ?: 0xFF2C5BA8L
        val durH = try {
            val hI = horaInicio.take(5).split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            val hF = horaFin.take(5).split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            val diff = hF - hI
            if (diff > 0) "${diff / 60}h${if (diff % 60 != 0) "${diff % 60}m" else ""}" else "?"
        } catch (_: Exception) { "?" }
        return EventoAgenda(
            id         = id,
            hora       = horaInicio.take(5),
            duracion   = durH,
            titulo     = titulo ?: asigNombre,
            detalle    = "$labNombre · Grupo ${grupo ?: ""}",
            dia        = fecha,
            laboratorio = labNombre,
            grupo      = grupo ?: "",
            enCurso    = estado == "activa",
            colorHex   = colorLong,
        )
    }
}

@kotlinx.serialization.Serializable
private data class AsistenciaRow(
    @kotlinx.serialization.SerialName("hora_registro") val horaRegistro: String = "",
    val estudiante: com.example.aulix.domain.model.UsuarioEmbedido? = null,
)

private fun String.safeTime(): String =
    if (length >= 16) substring(11, 16) else take(5)
