package com.example.aulix.ui.theme

import androidx.compose.ui.graphics.Color

// ── 6 tokens de marca ────────────────────────────────────────────────────────
val Lienzo  = Color(0xFFFAF8F3)   // fondo principal light
val Arena   = Color(0xFFF3EDDE)   // superficies secundarias light / info box
val Tinta   = Color(0xFF0F2742)   // texto principal · fondo dark
val Cobalto = Color(0xFF2C5BA8)   // acento primario (botones, campos activos)
val Cielo   = Color(0xFFDCE7F5)   // azul claro (chips, estados, hover)
val Cobre   = Color(0xFFB36A2E)   // acento secundario / cobre

// ── Derivados light ──────────────────────────────────────────────────────────
val TintaLight      = Color(0xFF1A3A5C)   // texto secundario light
val TintaMuted      = Color(0xFF6B7E96)   // texto muted/placeholder light
val SurfaceLight    = Color(0xFFFFFFFF)   // superficie de campos
val BorderLight     = Color(0xFFD8D0C4)   // borde campos sin foco
val CobaltoLight    = Color(0xFF3D6FBF)   // cobalto hover/pressed light

// ── Derivados dark ───────────────────────────────────────────────────────────
val TintaDark       = Color(0xFF0A1929)   // fondo principal dark (más profundo que Tinta)
val SurfaceDark     = Color(0xFF162135)   // superficie de cards/campos dark
val SurfaceVarDark  = Color(0xFF1E2F45)   // superficie variante dark
val BorderDark      = Color(0xFF2A3D54)   // borde campos dark
val CobaltoDark     = Color(0xFF8BAAD8)   // cobalto adaptado para dark (más claro)
val CieloDark       = Color(0xFF2A4165)   // cielo en dark
val TextOnDark      = Color(0xFFF0F4F8)   // texto primario dark
val TextMutedDark   = Color(0xFF8DA4BC)   // texto secundario dark

// ── Estados (compartidos ambos temas) ────────────────────────────────────────
val StatusGreen     = Color(0xFF16A34A)
val StatusGreenBg   = Color(0xFFDCFCE7)
val StatusGreenBgDk = Color(0xFF052E16)
val StatusAmber     = Color(0xFFD97706)
val StatusAmberBg   = Color(0xFFFEF3C7)
val StatusRed       = Color(0xFFDC2626)
val StatusRedBg     = Color(0xFFFEE2E2)
val StatusGray      = Color(0xFF6B7280)
val StatusGrayBg    = Color(0xFFF3F4F6)

// ── Roles ─────────────────────────────────────────────────────────────────────
val RoleDocente    = Color(0xFF2C5BA8)   // Cobalto
val RoleEstudiante = Color(0xFF0891B2)   // Cyan
val RoleAuxiliar   = Color(0xFF059669)   // Emerald
val RoleSoporte    = Color(0xFFB36A2E)   // Cobre
