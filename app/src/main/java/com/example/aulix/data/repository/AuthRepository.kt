package com.example.aulix.data.repository

import com.example.aulix.domain.model.RolEmbedido
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthRepository @Inject constructor(private val supabase: SupabaseClient) {

    // SELECT con embedding del rol para poder construir UserRole
    private val userSelect = "*, rol(id, nombre)"

    suspend fun signIn(email: String, password: String): Result<User> = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        fetchCurrentUser()!!
    }

    suspend fun signUp(
        email: String,
        password: String,
        nombre: String,
        rolNombre: String,
        documento: String?,
        programa: String?,
    ): Result<User> = runCatching {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject { put("nombre", nombre) }
        }
        // Ahora el usuario está autenticado — el trigger ya insertó la fila en `usuario`.
        // Buscamos el rol_id (la RLS lo permite porque ya hay sesión activa).
        val rolId = supabase.from("rol")
            .select { filter { eq("nombre", rolNombre) } }
            .decodeSingle<RolEmbedido>()
            .id
        val uid = supabase.auth.currentUserOrNull()!!.id
        supabase.from("usuario").update({
            set("nombre", nombre)
            set("rol_id", rolId)
            if (documento != null) set("documento", documento)
            if (programa  != null) set("programa",  programa)
        }) {
            filter { eq("id", uid) }
        }
        fetchCurrentUser()!!
    }

    suspend fun signOut(): Result<Unit> = runCatching {
        supabase.auth.signOut()
    }

    // Recupera sesión activa (uso en splash/inicio de la app)
    suspend fun restoreSession(): Result<User?> = runCatching {
        supabase.auth.currentUserOrNull() ?: return@runCatching null
        fetchCurrentUser()
    }

    private suspend fun fetchCurrentUser(): User? {
        val uid = supabase.auth.currentUserOrNull()?.id ?: return null
        val row = supabase.from("usuario")
            .select(Columns.raw(userSelect)) { filter { eq("id", uid) } }
            .decodeSingle<User>()
        // @Transient `role` no se deserializa — lo completamos aquí
        return row.copy(role = row.rol?.toUserRole() ?: UserRole.ESTUDIANTE)
    }
}
