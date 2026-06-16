package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EstadoSesion {
    @SerialName("pendiente")  PROGRAMADA,
    @SerialName("activa")     ACTIVA,
    @SerialName("cerrada")    CERRADA,
    @SerialName("cancelada")  CANCELADA,
}

enum class EstadoCierre { NORMAL, CON_INCIDENCIA, CANCELADA }

enum class TipoIncidencia { EQUIPO, SEGURIDAD, INFRAESTRUCTURA, OTRA }
