package com.example.aulix.core.network

import com.example.aulix.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

// El cliente Supabase se crea una sola vez y es inyectado como @Singleton via Hilt.
// SUPABASE_ANON_KEY es la clave pública (anon key). Es seguro incluirla en el cliente
// porque la seguridad real está en Row Level Security (RLS) del lado del servidor.
fun buildSupabaseClient() = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
) {
    install(Postgrest)
    install(Auth)
}
