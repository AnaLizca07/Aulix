package com.example.aulix.domain.model

enum class EstadoSesion {
    PROGRAMADA, ACTIVA, CERRADA
}

enum class EstadoCierre {
    NORMAL, CON_INCIDENCIA, CANCELADA
}

enum class TipoIncidencia {
    EQUIPO, SEGURIDAD, INFRAESTRUCTURA, OTRA
}
