package com.example.aulix.domain.model

// ── Tipos de documento ────────────────────────────────────────────────────────
// Agrega o elimina opciones aquí según los documentos aceptados por la institución.
object DocumentTypes {
    val options = listOf(
        "C.C.",   // Cédula de ciudadanía
        "T.I.",   // Tarjeta de identidad
        "C.E.",   // Cédula de extranjería
        "P.A.",   // Pasaporte
        "P.E.P.", // Permiso especial de permanencia
    )
}

// ── Programas académicos / dependencias ───────────────────────────────────────
// Agrega aquí los programas e.g cuando cambie la oferta académica de la institución.
object AcademicPrograms {
    val options = listOf(
        "Ingeniería de Software",
        "Ingeniería Industrial",
        "Ingeniería Civil",
        "Medicina",
        "Psicología",
        "M.V.Z",
        "Enfermería",
        "Administración de empresas",
        "Marketing Digital",
        "Soporte TI",
        "Laboratorios CUE",
        "Biblioteca",
        "Bienestar",
    )
}
