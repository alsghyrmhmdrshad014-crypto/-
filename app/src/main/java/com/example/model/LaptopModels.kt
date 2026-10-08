package com.example.model

enum class OperatingSystem(val displayName: String, val iconName: String) {
    WINDOWS("Windows", "windows"),
    MAC("macOS", "apple"),
    LINUX("Linux", "terminal")
}

data class LaptopProfile(
    val id: String,
    val name: String,
    val ipAddress: String,
    val port: Int = 8080,
    val os: OperatingSystem = OperatingSystem.WINDOWS,
    val isDefault: Boolean = false,
    val lastConnected: Long = System.currentTimeMillis()
)

enum class AppCategory(val titleAr: String, val titleEn: String) {
    BROWSERS("المتصفحات", "Browsers"),
    PRODUCTIVITY("الإنتاجية والأوفيس", "Productivity"),
    MEDIA("الترفيه والوسائط", "Media & Entertainment"),
    SYSTEM("أدوات النظام", "System Tools"),
    CUSTOM("تطبيقات مخصصة", "Custom Apps")
}

data class LaptopApp(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val command: String, // executable or alias e.g. "chrome", "code", "spotify"
    val category: AppCategory,
    val iconKey: String, // e.g. "chrome", "code", "spotify", "vlc", "word", "excel", "folder", "calc"
    val isFavorite: Boolean = false,
    val isRunning: Boolean = false,
    val isCustom: Boolean = false,
    val description: String = ""
)

data class ActiveLaptopWindow(
    val id: String,
    val title: String,
    val appName: String,
    val iconKey: String = "window"
)

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    SIMULATION_MODE,
    ERROR
}

data class ConnectionInfo(
    val status: ConnectionStatus = ConnectionStatus.SIMULATION_MODE,
    val currentLaptop: LaptopProfile? = null,
    val latencyMs: Long = 0,
    val errorMessage: String? = null,
    val isSimulation: Boolean = true
)

enum class SensitivityLevel(val displayNameAr: String, val displayNameEn: String, val multiplier: Float) {
    SLOW("بطيء", "Slow", 0.8f),
    NORMAL("عادي", "Normal", 1.4f),
    FAST("سريع", "Fast", 2.2f),
    GAMING("فائق", "Ultra", 3.0f)
}
