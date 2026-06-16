package com.example.aulix.core.di

import com.example.aulix.core.network.buildSupabaseClient
import com.example.aulix.data.repository.AsistenciaRepository
import com.example.aulix.data.repository.AuthRepository
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.data.repository.MantenimientoRepository
import com.example.aulix.data.repository.PrestamoRepository
import com.example.aulix.data.repository.SesionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = buildSupabaseClient()

    @Provides
    @Singleton
    fun provideAuthRepository(supabase: SupabaseClient): AuthRepository =
        AuthRepository(supabase)

    @Provides
    @Singleton
    fun provideSesionRepository(supabase: SupabaseClient): SesionRepository =
        SesionRepository(supabase)

    @Provides
    @Singleton
    fun provideAsistenciaRepository(supabase: SupabaseClient): AsistenciaRepository =
        AsistenciaRepository(supabase)

    @Provides
    @Singleton
    fun provideEquipoRepository(supabase: SupabaseClient): EquipoRepository =
        EquipoRepository(supabase)

    @Provides
    @Singleton
    fun providePrestamoRepository(supabase: SupabaseClient): PrestamoRepository =
        PrestamoRepository(supabase)

    @Provides
    @Singleton
    fun provideIncidenciaRepository(supabase: SupabaseClient): IncidenciaRepository =
        IncidenciaRepository(supabase)

    @Provides
    @Singleton
    fun provideMantenimientoRepository(supabase: SupabaseClient): MantenimientoRepository =
        MantenimientoRepository(supabase)
}
