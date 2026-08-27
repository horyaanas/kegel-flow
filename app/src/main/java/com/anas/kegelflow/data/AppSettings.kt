package com.anas.kegelflow.data

enum class AppLanguage { ARABIC, ENGLISH }
enum class AppThemeMode { LIGHT, DARK, SYSTEM }

data class AppSettings(
    val language: AppLanguage = AppLanguage.ARABIC,
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val prepCountdownSeconds: Int = 3,
    val selectedPlanLevel: String = "BEGINNER" // BEGINNER, INTERMEDIATE, ADVANCED, CUSTOM
)
