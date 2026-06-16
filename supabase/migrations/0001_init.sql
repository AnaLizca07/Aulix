-- ═══════════════════════════════════════════════════════════════════════════════
-- Aulix · Migración inicial
-- Ejecutar en Supabase > SQL Editor (con usuario postgres / service_role)
--
-- Notas de diseño:
--   · IDs: UUID v4 via gen_random_uuid()
--   · Timestamps: TIMESTAMPTZ con default now()
--   · Estados: TEXT + CHECK (más fácil de extender que ENUMs)
--   · FKs: CASCADE para hijos directos (evidencia→incidencia, asistencia→sesion);
--           RESTRICT para catálogos y entidades independientes
--   · Índices en todas las FKs
--   · Las columnas marcadas "-- +" extienden el spec base para soportar la UI existente
-- ═══════════════════════════════════════════════════════════════════════════════

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. FACULTAD
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS facultad (
    id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre TEXT NOT NULL,
    codigo TEXT NOT NULL UNIQUE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. PROGRAMA ACADÉMICO
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS programa_academico (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre      TEXT NOT NULL,
    codigo      TEXT NOT NULL UNIQUE,
    facultad_id UUID NOT NULL REFERENCES facultad(id) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_programa_facultad ON programa_academico(facultad_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. ROL
-- Valores esperados: 'docente', 'estudiante', 'auxiliar', 'soporte_tecnico'
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rol (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre      TEXT NOT NULL UNIQUE,
    descripcion TEXT
);

-- Datos iniciales de roles
INSERT INTO rol (nombre, descripcion) VALUES
    ('docente',          'Gestiona sesiones de laboratorio'),
    ('estudiante',       'Registra asistencia a prácticas'),
    ('auxiliar',         'Administra préstamos de equipos'),
    ('soporte_tecnico',  'Atiende y resuelve incidencias')
ON CONFLICT (nombre) DO NOTHING;

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. USUARIO
-- Perfil que extiende auth.users (Supabase GoTrue).
-- La contraseña la gestiona Supabase; aquí NO se guarda password_hash.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuario (
    id         UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nombre     TEXT NOT NULL,
    email      TEXT NOT NULL UNIQUE,
    documento  TEXT,           -- cédula / código estudiantil
    programa   TEXT,           -- +: texto libre (p.ej. "Ing. de Sistemas")
    rol_id     UUID REFERENCES rol(id) ON DELETE RESTRICT,
    activo     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ     NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_usuario_rol ON usuario(rol_id);

-- Trigger: inserta el perfil en `usuario` automáticamente al crear un usuario en GoTrue
CREATE OR REPLACE FUNCTION handle_new_auth_user()
RETURNS TRIGGER LANGUAGE plpgsql SECURITY DEFINER AS $$
BEGIN
    INSERT INTO public.usuario (id, nombre, email)
    VALUES (
        NEW.id,
        COALESCE(NEW.raw_user_meta_data->>'nombre', split_part(NEW.email, '@', 1)),
        NEW.email
    )
    ON CONFLICT (id) DO NOTHING;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION handle_new_auth_user();

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. ASIGNATURA
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS asignatura (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre      TEXT NOT NULL,
    codigo      TEXT NOT NULL,
    programa_id UUID NOT NULL REFERENCES programa_academico(id) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_asignatura_programa ON asignatura(programa_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. LABORATORIO
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS laboratorio (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre         TEXT    NOT NULL,
    ubicacion      TEXT,
    disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    capacidad      INTEGER NOT NULL DEFAULT 0,
    estado         TEXT    NOT NULL DEFAULT 'activo'
        CHECK (estado IN ('activo', 'mantenimiento', 'fuera_de_servicio')),
    programa_id    UUID REFERENCES programa_academico(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_laboratorio_programa ON laboratorio(programa_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 7. EQUIPO
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS equipo (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre         TEXT NOT NULL,
    marca          TEXT,                             -- +: requerido por la UI
    numero_serie   TEXT NOT NULL UNIQUE,
    tipo           TEXT,
    estado         TEXT NOT NULL DEFAULT 'disponible'
        CHECK (estado IN ('disponible', 'prestado', 'en_mantenimiento', 'fuera_de_servicio')),
    laboratorio_id UUID NOT NULL REFERENCES laboratorio(id) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_equipo_laboratorio ON equipo(laboratorio_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 8. RESERVA
-- Bloque horario agendado (equivale a EventoAgenda en la app).
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reserva (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    laboratorio_id UUID NOT NULL REFERENCES laboratorio(id) ON DELETE RESTRICT,
    docente_id     UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    asignatura_id  UUID NOT NULL REFERENCES asignatura(id) ON DELETE RESTRICT,
    titulo         TEXT,                             -- +: título del evento de agenda
    practica       TEXT,                             -- +: descripción de la práctica
    color_hex      TEXT,                             -- +: color de barra en la agenda
    grupo          TEXT,                             -- +: código de grupo (p.ej. "21A")
    fecha          DATE     NOT NULL,
    hora_inicio    TIME     NOT NULL,
    hora_fin       TIME     NOT NULL,
    estado         TEXT     NOT NULL DEFAULT 'pendiente'
        CHECK (estado IN ('pendiente', 'activa', 'cancelada', 'completada')),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_reserva_hora CHECK (hora_fin > hora_inicio)
);
CREATE INDEX IF NOT EXISTS idx_reserva_laboratorio ON reserva(laboratorio_id);
CREATE INDEX IF NOT EXISTS idx_reserva_docente      ON reserva(docente_id);
CREATE INDEX IF NOT EXISTS idx_reserva_asignatura   ON reserva(asignatura_id);
CREATE INDEX IF NOT EXISTS idx_reserva_fecha        ON reserva(fecha);

-- ─────────────────────────────────────────────────────────────────────────────
-- 9. SESION
-- Instancia activa de una reserva: docente la abre y cierra en tiempo real.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sesion (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reserva_id        UUID NOT NULL REFERENCES reserva(id) ON DELETE RESTRICT,
    docente_id        UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    codigo_asistencia TEXT,                          -- +: código de 6 dígitos para asistencia manual
    hora_apertura     TIMESTAMPTZ,
    hora_cierre       TIMESTAMPTZ,
    observaciones     TEXT,
    estado            TEXT NOT NULL DEFAULT 'pendiente'
        CHECK (estado IN ('pendiente', 'activa', 'cerrada', 'cancelada'))
);
CREATE INDEX IF NOT EXISTS idx_sesion_reserva  ON sesion(reserva_id);
CREATE INDEX IF NOT EXISTS idx_sesion_docente  ON sesion(docente_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 10. PRESTAMO
-- Dos FKs a usuario: solicitante_id (quien recibe) y auxiliar_id (quien entrega).
-- sesion_id es NULLABLE: un préstamo puede existir sin sesión activa.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS prestamo (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipo_id        UUID NOT NULL REFERENCES equipo(id) ON DELETE RESTRICT,
    solicitante_id   UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    auxiliar_id      UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    sesion_id        UUID REFERENCES sesion(id) ON DELETE SET NULL,
    fecha_prestamo   TIMESTAMPTZ NOT NULL DEFAULT now(),
    fecha_devolucion TIMESTAMPTZ,
    estado           TEXT NOT NULL DEFAULT 'activo'
        CHECK (estado IN ('activo', 'devuelto', 'vencido')),
    sin_novedad      BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX IF NOT EXISTS idx_prestamo_equipo       ON prestamo(equipo_id);
CREATE INDEX IF NOT EXISTS idx_prestamo_solicitante  ON prestamo(solicitante_id);
CREATE INDEX IF NOT EXISTS idx_prestamo_auxiliar     ON prestamo(auxiliar_id);
CREATE INDEX IF NOT EXISTS idx_prestamo_sesion       ON prestamo(sesion_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 11. ASISTENCIA
-- UNIQUE (sesion_id, estudiante_id): un estudiante solo puede registrarse una vez.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS asistencia (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sesion_id     UUID NOT NULL REFERENCES sesion(id) ON DELETE CASCADE,
    estudiante_id UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    hora_registro TIMESTAMPTZ NOT NULL DEFAULT now(),
    metodo        TEXT NOT NULL CHECK (metodo IN ('qr', 'codigo')),
    presente      BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (sesion_id, estudiante_id)
);
CREATE INDEX IF NOT EXISTS idx_asistencia_sesion     ON asistencia(sesion_id);
CREATE INDEX IF NOT EXISTS idx_asistencia_estudiante ON asistencia(estudiante_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 12. INCIDENCIA
-- reportado_por y asignado_a son FKs distintas a usuario.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS incidencia (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sesion_id     UUID REFERENCES sesion(id) ON DELETE SET NULL,
    equipo_id     UUID NOT NULL REFERENCES equipo(id) ON DELETE RESTRICT,
    reportado_por UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    asignado_a    UUID REFERENCES usuario(id) ON DELETE SET NULL, -- +: para "Mis asignadas"
    titulo        TEXT NOT NULL,                                    -- +: título corto
    descripcion   TEXT NOT NULL,
    severidad     TEXT NOT NULL DEFAULT 'media'
        CHECK (severidad IN ('alta', 'media', 'baja')),
    estado        TEXT NOT NULL DEFAULT 'abierta'
        CHECK (estado IN ('abierta', 'en_atencion', 'resuelta')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_incidencia_sesion        ON incidencia(sesion_id);
CREATE INDEX IF NOT EXISTS idx_incidencia_equipo        ON incidencia(equipo_id);
CREATE INDEX IF NOT EXISTS idx_incidencia_reportado_por ON incidencia(reportado_por);
CREATE INDEX IF NOT EXISTS idx_incidencia_asignado_a    ON incidencia(asignado_a);
CREATE INDEX IF NOT EXISTS idx_incidencia_estado        ON incidencia(estado);

-- ─────────────────────────────────────────────────────────────────────────────
-- 13. EVIDENCIA
-- Fotos / notas adjuntas a una incidencia. CASCADE: si se elimina la incidencia
-- se eliminan sus evidencias.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS evidencia (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    incidencia_id UUID NOT NULL REFERENCES incidencia(id) ON DELETE CASCADE,
    url_foto      TEXT,
    descripcion   TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_evidencia_incidencia ON evidencia(incidencia_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 14. MANTENIMIENTO  (+: no estaba en el spec de 13 tablas, pero es necesario
--     para ProgramarMantenimientoScreen)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS mantenimiento (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipo_id        UUID NOT NULL REFERENCES equipo(id) ON DELETE RESTRICT,
    registrado_por   UUID NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
    tipo             TEXT NOT NULL DEFAULT 'preventivo'
        CHECK (tipo IN ('preventivo', 'correctivo', 'predictivo')),
    fecha_programada DATE     NOT NULL,
    hora_programada  TIME     NOT NULL DEFAULT '09:00',
    tecnico_asignado TEXT,
    observaciones    TEXT,
    estado           TEXT NOT NULL DEFAULT 'programado'
        CHECK (estado IN ('programado', 'en_proceso', 'completado', 'cancelado')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_mantenimiento_equipo         ON mantenimiento(equipo_id);
CREATE INDEX IF NOT EXISTS idx_mantenimiento_registrado_por ON mantenimiento(registrado_por);
