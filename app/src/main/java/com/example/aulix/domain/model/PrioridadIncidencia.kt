package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PrioridadIncidencia {
    @SerialName("alta")  ALTA,
    @SerialName("media") MEDIA,
    @SerialName("baja")  BAJA,
}
