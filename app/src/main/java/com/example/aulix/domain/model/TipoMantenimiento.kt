package com.example.aulix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TipoMantenimiento {
    @SerialName("preventivo") PREVENTIVO,
    @SerialName("correctivo") CORRECTIVO,
    @SerialName("predictivo") CALIBRACION,
}
