package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EstadoEquipo {
    @SerialName("disponible")        DISPONIBLE,
    @SerialName("prestado")          PRESTADO,
    @SerialName("en_mantenimiento")  REPARACION,
    @SerialName("fuera_de_servicio") FUERA_DE_SERVICIO,
}
