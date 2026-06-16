package com.example.aulix.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AsignaturaCatalog(
    val id: String = "",
    val nombre: String = "",
    val codigo: String = "",
)

@Serializable
data class LaboratorioCatalog(
    val id: String = "",
    val nombre: String = "",
    val ubicacion: String? = null,
)
