-- ═══════════════════════════════════════════════════════════════════════════════
-- Aulix · Row Level Security
-- Ejecutar DESPUÉS de 0001_init.sql
--
-- EXPLICACIÓN DEL MODELO DE SEGURIDAD
-- ────────────────────────────────────
-- 1. Todas las tablas tienen RLS activado. Sin política = acceso denegado.
-- 2. auth.uid() devuelve el UUID del usuario autenticado en Supabase Auth.
-- 3. get_user_rol() resuelve el rol del usuario actual buscando en `usuario`.
-- 4. Políticas base: cualquier usuario autenticado puede LEER catálogos y datos
--    propios; la escritura está restringida por rol.
-- 5. Los comentarios "TODO" marcan dónde afinar cuando el modelo de permisos
--    esté definido completamente.
-- ═══════════════════════════════════════════════════════════════════════════════

-- ─────────────────────────────────────────────────────────────────────────────
-- Helper: devuelve el nombre del rol del usuario autenticado.
-- SECURITY DEFINER + STABLE permite que sea eficiente y seguro desde políticas.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE OR REPLACE FUNCTION get_user_rol()
RETURNS TEXT LANGUAGE sql STABLE SECURITY DEFINER AS $$
    SELECT r.nombre
    FROM   rol r
    JOIN   usuario u ON u.rol_id = r.id
    WHERE  u.id = auth.uid()
    LIMIT  1;
$$;

-- ═══════════════════════════════════════════════════════════════════════════════
-- CATÁLOGOS (solo lectura para todos los autenticados)
-- ═══════════════════════════════════════════════════════════════════════════════

ALTER TABLE facultad           ENABLE ROW LEVEL SECURITY;
ALTER TABLE programa_academico ENABLE ROW LEVEL SECURITY;
ALTER TABLE rol                ENABLE ROW LEVEL SECURITY;
ALTER TABLE asignatura         ENABLE ROW LEVEL SECURITY;

CREATE POLICY "catálogo_read_auth" ON facultad
    FOR SELECT USING (auth.role() = 'authenticated');
-- TODO: INSERT/UPDATE/DELETE reservados para rol administrador (aún no modelado)

CREATE POLICY "catálogo_read_auth" ON programa_academico
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "catálogo_read_auth" ON rol
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "catálogo_read_auth" ON asignatura
    FOR SELECT USING (auth.role() = 'authenticated');

-- ═══════════════════════════════════════════════════════════════════════════════
-- USUARIO (perfil público, escritura propia)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE usuario ENABLE ROW LEVEL SECURITY;

-- Todos los autenticados pueden leer perfiles (necesario para mostrar nombres en UI)
CREATE POLICY "usuario_read_auth" ON usuario
    FOR SELECT USING (auth.role() = 'authenticated');

-- El trigger de GoTrue hace el INSERT; el propio usuario puede completar su perfil
CREATE POLICY "usuario_insert_own" ON usuario
    FOR INSERT WITH CHECK (id = auth.uid());

-- Solo el propio usuario actualiza su perfil
CREATE POLICY "usuario_update_own" ON usuario
    FOR UPDATE USING (id = auth.uid());

-- ═══════════════════════════════════════════════════════════════════════════════
-- LABORATORIO (lectura libre, escritura auxiliar/soporte)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE laboratorio ENABLE ROW LEVEL SECURITY;

CREATE POLICY "laboratorio_read_auth" ON laboratorio
    FOR SELECT USING (auth.role() = 'authenticated');

-- Solo auxiliar y soporte pueden cambiar disponibilidad/estado
CREATE POLICY "laboratorio_update_ops" ON laboratorio
    FOR UPDATE USING (get_user_rol() IN ('auxiliar', 'soporte_tecnico'));
-- TODO: INSERT/DELETE para administrador

-- ═══════════════════════════════════════════════════════════════════════════════
-- EQUIPO (lectura libre, estado modificable por auxiliar/soporte)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE equipo ENABLE ROW LEVEL SECURITY;

CREATE POLICY "equipo_read_auth" ON equipo
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "equipo_update_ops" ON equipo
    FOR UPDATE USING (get_user_rol() IN ('auxiliar', 'soporte_tecnico'));
-- TODO: INSERT/DELETE para administrador

-- ═══════════════════════════════════════════════════════════════════════════════
-- RESERVA (agenda: lectura libre; solo el docente dueño puede crear/editar la suya)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE reserva ENABLE ROW LEVEL SECURITY;

CREATE POLICY "reserva_read_auth" ON reserva
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "reserva_insert_docente" ON reserva
    FOR INSERT WITH CHECK (docente_id = auth.uid() AND get_user_rol() = 'docente');

CREATE POLICY "reserva_update_docente" ON reserva
    FOR UPDATE USING (docente_id = auth.uid() AND get_user_rol() = 'docente');
-- TODO: permitir a administrador cancelar cualquier reserva

-- ═══════════════════════════════════════════════════════════════════════════════
-- SESION (lectura libre; solo el docente dueño la abre/cierra)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE sesion ENABLE ROW LEVEL SECURITY;

CREATE POLICY "sesion_read_auth" ON sesion
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "sesion_insert_docente" ON sesion
    FOR INSERT WITH CHECK (docente_id = auth.uid() AND get_user_rol() = 'docente');

CREATE POLICY "sesion_update_docente" ON sesion
    FOR UPDATE USING (docente_id = auth.uid() AND get_user_rol() = 'docente');

-- ═══════════════════════════════════════════════════════════════════════════════
-- PRESTAMO (lectura libre; registro y gestión por auxiliar)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE prestamo ENABLE ROW LEVEL SECURITY;

CREATE POLICY "prestamo_read_auth" ON prestamo
    FOR SELECT USING (auth.role() = 'authenticated');

-- El auxiliar que registra el préstamo es quien lo inserta
CREATE POLICY "prestamo_insert_auxiliar" ON prestamo
    FOR INSERT WITH CHECK (auxiliar_id = auth.uid() AND get_user_rol() = 'auxiliar');

-- El auxiliar registrador puede marcar como devuelto/vencido
CREATE POLICY "prestamo_update_auxiliar" ON prestamo
    FOR UPDATE USING (auxiliar_id = auth.uid() AND get_user_rol() = 'auxiliar');
-- TODO: self-service: el solicitante puede solicitar devolución

-- ═══════════════════════════════════════════════════════════════════════════════
-- ASISTENCIA (lectura libre; solo el estudiante registra la suya)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE asistencia ENABLE ROW LEVEL SECURITY;

CREATE POLICY "asistencia_read_auth" ON asistencia
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "asistencia_insert_estudiante" ON asistencia
    FOR INSERT WITH CHECK (estudiante_id = auth.uid() AND get_user_rol() = 'estudiante');
-- TODO: docente puede corregir asistencia de sus propias sesiones

-- ═══════════════════════════════════════════════════════════════════════════════
-- INCIDENCIA (lectura libre; cualquier rol reporta; soporte actualiza estado)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE incidencia ENABLE ROW LEVEL SECURITY;

CREATE POLICY "incidencia_read_auth" ON incidencia
    FOR SELECT USING (auth.role() = 'authenticated');

-- Cualquier usuario autenticado puede reportar una incidencia
CREATE POLICY "incidencia_insert_auth" ON incidencia
    FOR INSERT WITH CHECK (reportado_por = auth.uid());

-- Solo soporte técnico puede cambiar estado y asignar
CREATE POLICY "incidencia_update_soporte" ON incidencia
    FOR UPDATE USING (get_user_rol() = 'soporte_tecnico');
-- TODO: el reportante puede añadir descripción pero no cambiar estado

-- ═══════════════════════════════════════════════════════════════════════════════
-- EVIDENCIA (lectura libre; docente y soporte adjuntan; borrado propio)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE evidencia ENABLE ROW LEVEL SECURITY;

CREATE POLICY "evidencia_read_auth" ON evidencia
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "evidencia_insert_ops" ON evidencia
    FOR INSERT WITH CHECK (get_user_rol() IN ('docente', 'soporte_tecnico'));
-- TODO: restringir a evidencias de sesiones/incidencias del propio usuario

-- ═══════════════════════════════════════════════════════════════════════════════
-- MANTENIMIENTO (lectura libre; soporte programa; auxiliar puede ver)
-- ═══════════════════════════════════════════════════════════════════════════════
ALTER TABLE mantenimiento ENABLE ROW LEVEL SECURITY;

CREATE POLICY "mantenimiento_read_auth" ON mantenimiento
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "mantenimiento_insert_soporte" ON mantenimiento
    FOR INSERT WITH CHECK (registrado_por = auth.uid() AND get_user_rol() = 'soporte_tecnico');

CREATE POLICY "mantenimiento_update_soporte" ON mantenimiento
    FOR UPDATE USING (registrado_por = auth.uid() AND get_user_rol() = 'soporte_tecnico');
