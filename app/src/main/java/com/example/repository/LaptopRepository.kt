package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppCategory
import com.example.model.LaptopApp
import com.example.model.LaptopProfile
import com.example.model.OperatingSystem
import com.example.model.SensitivityLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class LaptopRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("laptop_remote_prefs", Context.MODE_PRIVATE)

    private val _laptops = MutableStateFlow<List<LaptopProfile>>(emptyList())
    val laptops: StateFlow<List<LaptopProfile>> = _laptops.asStateFlow()

    private val _customApps = MutableStateFlow<List<LaptopApp>>(emptyList())
    val customApps: StateFlow<List<LaptopApp>> = _customApps.asStateFlow()

    private val _sensitivity = MutableStateFlow(SensitivityLevel.NORMAL)
    val sensitivity: StateFlow<SensitivityLevel> = _sensitivity.asStateFlow()

    init {
        loadLaptops()
        loadCustomApps()
        loadSensitivity()
    }

    fun getDefaultApps(): List<LaptopApp> {
        return listOf(
            // Browsers
            LaptopApp(
                id = "chrome",
                nameAr = "جوجل كروم",
                nameEn = "Google Chrome",
                command = "chrome",
                category = AppCategory.BROWSERS,
                iconKey = "chrome",
                isFavorite = true,
                description = "متصفح الإنترنت الأكثر شهرة"
            ),
            LaptopApp(
                id = "edge",
                nameAr = "مايكروسوفت إيدج",
                nameEn = "Microsoft Edge",
                command = "edge",
                category = AppCategory.BROWSERS,
                iconKey = "edge",
                description = "متصفح ويندوز الافتراضي"
            ),
            LaptopApp(
                id = "firefox",
                nameAr = "موزيلا فايرفوكس",
                nameEn = "Mozilla Firefox",
                command = "firefox",
                category = AppCategory.BROWSERS,
                iconKey = "firefox",
                description = "متصفح سريع وداعم للخصوصية"
            ),

            // Productivity
            LaptopApp(
                id = "vscode",
                nameAr = "فيجوال ستوديو كود",
                nameEn = "VS Code",
                command = "code",
                category = AppCategory.PRODUCTIVITY,
                iconKey = "code",
                isFavorite = true,
                description = "محرر البرمجة والأكواد"
            ),
            LaptopApp(
                id = "word",
                nameAr = "مايكروسوفت وورد",
                nameEn = "Microsoft Word",
                command = "word",
                category = AppCategory.PRODUCTIVITY,
                iconKey = "word",
                isFavorite = true,
                description = "كتابة وتنسيق المستندات والتقارير"
            ),
            LaptopApp(
                id = "excel",
                nameAr = "مايكروسوفت إكسل",
                nameEn = "Microsoft Excel",
                command = "excel",
                category = AppCategory.PRODUCTIVITY,
                iconKey = "excel",
                description = "الجداول والبيانات الحسابية"
            ),
            LaptopApp(
                id = "powerpoint",
                nameAr = "بوربوينت",
                nameEn = "PowerPoint",
                command = "powerpoint",
                category = AppCategory.PRODUCTIVITY,
                iconKey = "powerpoint",
                description = "العروض التقديمية والشرائح"
            ),

            // Media & Entertainment
            LaptopApp(
                id = "spotify",
                nameAr = "سبوتيفاي",
                nameEn = "Spotify",
                command = "spotify",
                category = AppCategory.MEDIA,
                iconKey = "spotify",
                isFavorite = true,
                description = "مشغل الموسيقى والبودكاست"
            ),
            LaptopApp(
                id = "vlc",
                nameAr = "مشغل VLC",
                nameEn = "VLC Player",
                command = "vlc",
                category = AppCategory.MEDIA,
                iconKey = "vlc",
                description = "مشغل الفيديوهات والصوتيات العالمي"
            ),
            LaptopApp(
                id = "youtube",
                nameAr = "يوتيوب",
                nameEn = "YouTube",
                command = "chrome https://www.youtube.com",
                category = AppCategory.MEDIA,
                iconKey = "youtube",
                isFavorite = true,
                description = "فتح موقع يوتيوب في المتصفح"
            ),

            // System Tools
            LaptopApp(
                id = "explorer",
                nameAr = "مستكشف الملفات",
                nameEn = "File Explorer",
                command = "explorer",
                category = AppCategory.SYSTEM,
                iconKey = "folder",
                isFavorite = true,
                description = "استعراض ملفات ومجلدات اللابتوب"
            ),
            LaptopApp(
                id = "calc",
                nameAr = "الآلة الحاسبة",
                nameEn = "Calculator",
                command = "calc",
                category = AppCategory.SYSTEM,
                iconKey = "calc",
                description = "الحاسبة السريعة"
            ),
            LaptopApp(
                id = "notepad",
                nameAr = "المفكرة",
                nameEn = "Notepad",
                command = "notepad",
                category = AppCategory.SYSTEM,
                iconKey = "notepad",
                description = "تدوين الملاحظات النصية السريعة"
            ),
            LaptopApp(
                id = "cmd",
                nameAr = "موجه الأوامر / الطرفية",
                nameEn = "Terminal / CMD",
                command = "cmd",
                category = AppCategory.SYSTEM,
                iconKey = "terminal",
                description = "واجهة سطر الأوامر"
            ),
            LaptopApp(
                id = "taskmgr",
                nameAr = "مدير المهام",
                nameEn = "Task Manager",
                command = "taskmgr",
                category = AppCategory.SYSTEM,
                iconKey = "taskmgr",
                description = "مراقبة وإدارة أداء البرامج"
            ),
            LaptopApp(
                id = "settings",
                nameAr = "إعدادات اللابتوب",
                nameEn = "Settings",
                command = "settings",
                category = AppCategory.SYSTEM,
                iconKey = "settings",
                description = "لوحة التحكم والإعدادات"
            )
        )
    }

    private fun loadLaptops() {
        val jsonStr = prefs.getString("saved_laptops", null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<LaptopProfile>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        LaptopProfile(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            ipAddress = obj.getString("ipAddress"),
                            port = obj.optInt("port", 8080),
                            os = try {
                                OperatingSystem.valueOf(obj.optString("os", "WINDOWS"))
                            } catch (e: Exception) {
                                OperatingSystem.WINDOWS
                            },
                            isDefault = obj.optBoolean("isDefault", false),
                            lastConnected = obj.optLong("lastConnected", System.currentTimeMillis())
                        )
                    )
                }
                _laptops.value = list
                return
            } catch (e: Exception) {
                // fall through to defaults
            }
        }

        // Initial default laptop profiles
        val initialLaptops = listOf(
            LaptopProfile(
                id = "laptop_home",
                name = "لابتوب المنزل (Windows)",
                ipAddress = "192.168.1.100",
                port = 8080,
                os = OperatingSystem.WINDOWS,
                isDefault = true
            ),
            LaptopProfile(
                id = "laptop_work",
                name = "لابتوب العمل (MacBook)",
                ipAddress = "192.168.1.105",
                port = 8080,
                os = OperatingSystem.MAC,
                isDefault = false
            )
        )
        _laptops.value = initialLaptops
        saveLaptops(initialLaptops)
    }

    fun saveLaptops(list: List<LaptopProfile>) {
        _laptops.value = list
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("ipAddress", item.ipAddress)
                put("port", item.port)
                put("os", item.os.name)
                put("isDefault", item.isDefault)
                put("lastConnected", item.lastConnected)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_laptops", array.toString()).apply()
    }

    fun addLaptop(laptop: LaptopProfile) {
        val current = _laptops.value.toMutableList()
        val index = current.indexOfFirst { it.id == laptop.id }
        if (index >= 0) {
            current[index] = laptop
        } else {
            current.add(laptop)
        }
        saveLaptops(current)
    }

    fun removeLaptop(id: String) {
        val current = _laptops.value.filter { it.id != id }
        saveLaptops(current)
    }

    private fun loadCustomApps() {
        val jsonStr = prefs.getString("custom_apps", null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<LaptopApp>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        LaptopApp(
                            id = obj.getString("id"),
                            nameAr = obj.getString("nameAr"),
                            nameEn = obj.optString("nameEn", obj.getString("nameAr")),
                            command = obj.getString("command"),
                            category = AppCategory.CUSTOM,
                            iconKey = obj.optString("iconKey", "app_generic"),
                            isFavorite = obj.optBoolean("isFavorite", false),
                            isCustom = true,
                            description = obj.optString("description", "")
                        )
                    )
                }
                _customApps.value = list
            } catch (e: Exception) {
                _customApps.value = emptyList()
            }
        }
    }

    fun addCustomApp(app: LaptopApp) {
        val current = _customApps.value.toMutableList()
        val index = current.indexOfFirst { it.id == app.id }
        if (index >= 0) {
            current[index] = app
        } else {
            current.add(app)
        }
        _customApps.value = current
        saveCustomApps(current)
    }

    fun removeCustomApp(id: String) {
        val current = _customApps.value.filter { it.id != id }
        _customApps.value = current
        saveCustomApps(current)
    }

    private fun saveCustomApps(list: List<LaptopApp>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("nameAr", item.nameAr)
                put("nameEn", item.nameEn)
                put("command", item.command)
                put("iconKey", item.iconKey)
                put("isFavorite", item.isFavorite)
                put("description", item.description)
            }
            array.put(obj)
        }
        prefs.edit().putString("custom_apps", array.toString()).apply()
    }

    private fun loadSensitivity() {
        val name = prefs.getString("trackpad_sensitivity", SensitivityLevel.NORMAL.name)
        _sensitivity.value = try {
            SensitivityLevel.valueOf(name ?: SensitivityLevel.NORMAL.name)
        } catch (e: Exception) {
            SensitivityLevel.NORMAL
        }
    }

    fun setSensitivity(level: SensitivityLevel) {
        _sensitivity.value = level
        prefs.edit().putString("trackpad_sensitivity", level.name).apply()
    }
}
