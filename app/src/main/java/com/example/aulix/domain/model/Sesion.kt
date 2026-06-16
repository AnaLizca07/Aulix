package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Sesion(
    val id: String = "",
    @SerialName("reserva_id")        val reservaId: String = "",
    @SerialName("docente_id")        val docenteId: String = "",
    @SerialName("codigo_asistencia") val codigoAsistencia: String = "000000",
    @SerialName("hora_apertura")     val horaApertura: String? = null,
    @SerialName("hora_cierre")       val horaCierre: String? = null,
    val observaciones: String? = null,
    val estado: EstadoSesion = EstadoSesion.PROGRAMADA,
    // SELECT sesion(*, reserva(titulo, practica, grupo, hora_inicio, hora_fin, fecha,
    //   color_hex, asignatura(id, nombre, codigo), laboratorio(id, nombre, ubicacion)))
    val reserva: ReservaEmbedida? = null,
    // Campos calculados post-carga por el repositorio
    @Transient val asistentesConfirmados: Int = 0,
    @Transient val minutosRestantes: Int = 23,
    @Transient val totalEstudiantes: Int = 0,
    @Transient val auxiliar: String = "",
    @Transient val capacidad: String = "",
) {
    // Propiedades que la UI accede directamente — delegadas al objeto reserva embebido
    val titulo: String      get() = reserva?.titulo ?: ""
    val asignatura: String  get() = reserva?.asignatura?.nombre ?: ""
    val grupo: String       get() = reserva?.grupo ?: ""
    val laboratorio: String get() = reserva?.laboratorio?.nombre ?: ""
    val edificio: String    get() = reserva?.laboratorio?.ubicacion ?: ""
    val horaInicio: String  get() = reserva?.horaInicio?.take(5) ?: ""
    val horaFin: String     get() = reserva?.horaFin?.take(5) ?: ""
    val practica: String    get() = reserva?.practica ?: ""

    val rangoHorario: String    get() = "$horaInicio → $horaFin"
    val asignaturaGrupo: String get() = "$asignatura · Grupo $grupo"
}

data class Asistente(
    val nombre: String,
    val hora: String,
    val esNuevo: Boolean = false,
)
