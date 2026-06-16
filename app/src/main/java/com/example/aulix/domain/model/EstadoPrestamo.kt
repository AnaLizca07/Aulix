package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EstadoPrestamo {
    @SerialName("activo")   ACTIVO,
    @SerialName("devuelto") DEVUELTO,
    @SerialName("vencido")  VENCIDO,
}
