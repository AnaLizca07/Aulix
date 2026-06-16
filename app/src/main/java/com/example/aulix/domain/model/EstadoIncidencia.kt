package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EstadoIncidencia {
    @SerialName("abierta")     ABIERTA,
    @SerialName("en_atencion") EN_ATENCION,
    @SerialName("resuelta")    RESUELTA,
}
