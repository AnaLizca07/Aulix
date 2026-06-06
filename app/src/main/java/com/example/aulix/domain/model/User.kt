package com.example.aulix.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val document: String,
    val program: String,
    val role: UserRole
) {
    val initials: String
        get() = fullName.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}
