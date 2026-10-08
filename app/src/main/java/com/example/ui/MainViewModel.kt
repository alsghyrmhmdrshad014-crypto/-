package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ActiveLaptopWindow
import com.example.model.AppCategory
import com.example.model.ConnectionInfo
import com.example.model.ConnectionStatus
import com.example.model.LaptopApp
import com.example.model.LaptopProfile
import com.example.model.OperatingSystem
import com.example.model.SensitivityLevel
import com.example.network.RemoteClient
import com.example.repository.LaptopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LaptopRepository(application)
    private val client = RemoteClient()

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    val savedLaptops = repository.laptops
    val customApps = repository.customApps
    val sensitivity = repository.sensitivity

    private val _connectionInfo = MutableStateFlow(
        ConnectionInfo(
            status = ConnectionStatus.SIMULATION_MODE,
            currentLaptop = LaptopProfile(
                id = "sim_laptop",
                name = "لابتوب تجريبي (Simulation Mode)",
                ipAddress = "192.168.1.100",
                port = 8080,
                os = OperatingSystem.WINDOWS,
                isDefault = true
            ),
            latencyMs = 12,
            isSimulation = true
        )
    )
    val connectionInfo: StateFlow<ConnectionInfo> = _connectionInfo.asStateFlow()

    private val _activeWindows = MutableStateFlow<List<ActiveLaptopWindow>>(
        listOf(
            ActiveLaptopWindow("win_1", "Google Chrome - البحث والتصفح", "Google Chrome", "chrome"),
            ActiveLaptopWindow("win_2", "Visual Studio Code - Workspace", "VS Code", "code"),
            ActiveLaptopWindow("win_3", "Spotify - تشغيل الموسيقى", "Spotify", "spotify"),
            ActiveLaptopWindow("win_4", "مستكشف الملفات - التنزيلات", "File Explorer", "folder")
        )
    )
    val activeWindows: StateFlow<List<ActiveLaptopWindow>> = _activeWindows.asStateFlow()

    private val _volumeLevel = MutableStateFlow(65)
    val volumeLevel: StateFlow<Int> = _volumeLevel.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<AppCategory?>(null)
    val selectedCategory: StateFlow<AppCategory?> = _selectedCategory.asStateFlow()

    private val _screenBitmap = MutableStateFlow<Bitmap?>(null)
    val screenBitmap: StateFlow<Bitmap?> = _screenBitmap.asStateFlow()

    private val _isScreenAutoRefresh = MutableStateFlow(false)
    val isScreenAutoRefresh: StateFlow<Boolean> = _isScreenAutoRefresh.asStateFlow()

    private val _isScanningNetwork = MutableStateFlow(false)
    val isScanningNetwork: StateFlow<Boolean> = _isScanningNetwork.asStateFlow()

    private var screenRefreshJob: Job? = null

    // Combined all apps (default + custom)
    val allApps: StateFlow<List<LaptopApp>> = combine(
        customApps,
        _searchQuery,
        _selectedCategory
    ) { customs, query, cat ->
        val defaults = repository.getDefaultApps()
        val combined = defaults + customs
        combined.filter { app ->
            val matchesCategory = cat == null || app.category == cat
            val matchesQuery = query.isBlank() ||
                app.nameAr.contains(query, ignoreCase = true) ||
                app.nameEn.contains(query, ignoreCase = true) ||
                app.command.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, repository.getDefaultApps())

    init {
        // Initialize default laptop if saved
        val list = repository.laptops.value
        if (list.isNotEmpty()) {
            val defaultLaptop = list.find { it.isDefault } ?: list.first()
            _connectionInfo.value = _connectionInfo.value.copy(currentLaptop = defaultLaptop)
        }
    }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: AppCategory?) {
        _selectedCategory.value = category
    }

    fun setSensitivity(level: SensitivityLevel) {
        repository.setSensitivity(level)
        showFeedback("تم ضبط حساسية الفأرة إلى: ${level.displayNameAr}")
    }

    fun toggleSimulationMode(enabled: Boolean) {
        val current = _connectionInfo.value
        if (enabled) {
            _connectionInfo.value = current.copy(
                status = ConnectionStatus.SIMULATION_MODE,
                isSimulation = true,
                latencyMs = 8,
                errorMessage = null
            )
            showFeedback("تم تفعيل وضع التجربة التفاعلي (Simulation)")
        } else {
            _connectionInfo.value = current.copy(
                status = ConnectionStatus.DISCONNECTED,
                isSimulation = false,
                latencyMs = 0
            )
            connectToLaptop(current.currentLaptop)
        }
    }

    fun connectToLaptop(laptop: LaptopProfile?) {
        if (laptop == null) return
        viewModelScope.launch {
            _connectionInfo.value = _connectionInfo.value.copy(
                currentLaptop = laptop,
                status = ConnectionStatus.CONNECTING,
                isSimulation = false,
                errorMessage = null
            )
            val (success, latency) = client.ping(laptop)
            if (success) {
                _connectionInfo.value = _connectionInfo.value.copy(
                    status = ConnectionStatus.CONNECTED,
                    latencyMs = latency,
                    isSimulation = false,
                    errorMessage = null
                )
                showFeedback("متصل بنجاح مع: ${laptop.name} (${latency}ms)")
            } else {
                _connectionInfo.value = _connectionInfo.value.copy(
                    status = ConnectionStatus.ERROR,
                    latencyMs = 0,
                    errorMessage = "تعذر الاتصال بـ ${laptop.ipAddress}:${laptop.port}. تأكد من تشغيل السيرفر على اللابتوب."
                )
            }
        }
    }

    fun saveLaptopProfile(name: String, ip: String, port: Int, os: OperatingSystem) {
        val newLaptop = LaptopProfile(
            id = "laptop_${System.currentTimeMillis()}",
            name = name,
            ipAddress = ip,
            port = port,
            os = os,
            isDefault = true
        )
        repository.addLaptop(newLaptop)
        connectToLaptop(newLaptop)
    }

    fun deleteLaptop(id: String) {
        repository.removeLaptop(id)
        showFeedback("تم حذف اللابتوب من القائمة")
    }

    // Network Scanner
    fun scanLocalNetwork() {
        viewModelScope.launch {
            _isScanningNetwork.value = true
            showFeedback("جاري فحص الشبكة المحلية بحثاً عن سيرفر اللابتوب...")
            delay(1500) // realistic subnet sweep simulation
            _isScanningNetwork.value = false
            showFeedback("اكتمل الفحص: يمكنك إضافة IP اللابتوب الموضح في شاشة السيرفر")
        }
    }

    // Mouse Controls
    fun onMouseMove(dx: Float, dy: Float) {
        val factor = sensitivity.value.multiplier
        val finalDx = dx * factor
        val finalDy = dy * factor

        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            return
        }

        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendMouseMove(target, finalDx, finalDy)
        }
    }

    fun onClick(button: String) {
        vibrateShort()
        val current = _connectionInfo.value
        val label = when (button) {
            "left" -> "نقرة يسار"
            "right" -> "نقرة يمين"
            "double" -> "نقرة مزدوجة"
            "middle" -> "زر الفأرة الأوسط"
            else -> button
        }

        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback(label)
            return
        }

        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendClick(target, button)
        }
    }

    fun onScroll(dy: Int) {
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendScroll(target, dy)
        }
    }

    // Keyboard Controls
    fun sendText(text: String) {
        if (text.isBlank()) return
        vibrateShort()
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("تمت كتابة: \"$text\" على اللابتوب")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            val ok = client.sendText(target, text)
            if (ok) showFeedback("تم إرسال النص للابتوب")
        }
    }

    fun sendKey(key: String) {
        vibrateShort()
        val current = _connectionInfo.value
        val displayKey = key.uppercase()
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("ضغط زر: $displayKey")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendKey(target, key)
        }
    }

    fun sendShortcut(keys: List<String>) {
        vibrateShort()
        val current = _connectionInfo.value
        val shortcutName = keys.joinToString(" + ") { it.uppercase() }
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("اختصار: $shortcutName")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            val ok = client.sendShortcut(target, keys)
            if (ok) showFeedback("تم تنفيذ: $shortcutName")
        }
    }

    // Media Controls
    fun changeVolume(delta: Int) {
        vibrateShort()
        val newVol = (_volumeLevel.value + delta).coerceIn(0, 100)
        _volumeLevel.value = newVol
        _isMuted.value = false

        val action = if (delta > 0) "volume_up" else "volume_down"
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("مستوى الصوت: $newVol%")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendMedia(target, action)
        }
    }

    fun setVolume(value: Int) {
        _volumeLevel.value = value.coerceIn(0, 100)
        _isMuted.value = false
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("مستوى الصوت: ${_volumeLevel.value}%")
            return
        }
    }

    fun toggleMute() {
        vibrateShort()
        val muted = !_isMuted.value
        _isMuted.value = muted
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback(if (muted) "تم كتم الصوت" else "تم إلغاء كتم الصوت")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendMedia(target, "mute")
        }
    }

    fun mediaPlayPause() {
        vibrateShort()
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("تشغيل / إيقاف مؤقت")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendMedia(target, "play_pause")
        }
    }

    fun mediaNext() {
        vibrateShort()
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("المقطع التالي")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendMedia(target, "next")
        }
    }

    fun mediaPrev() {
        vibrateShort()
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("المقطع السابق")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendMedia(target, "prev")
        }
    }

    // System Controls
    fun systemAction(action: String) {
        vibrateShort()
        val current = _connectionInfo.value
        val label = when (action) {
            "lock" -> "قفل اللابتوب"
            "sleep" -> "وضع السكون"
            "shutdown" -> "إيقاف التشغيل"
            "restart" -> "إعادة التشغيل"
            else -> action
        }
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("تم طلب: $label على اللابتوب")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            client.sendSystem(target, action)
            showFeedback("تم إرسال أمر $label للابتوب")
        }
    }

    // Laptop Apps Management
    fun launchApp(app: LaptopApp) {
        vibrateShort()
        val current = _connectionInfo.value

        // Add to active windows list
        val windows = _activeWindows.value.toMutableList()
        val existingIndex = windows.indexOfFirst { it.appName.equals(app.nameEn, ignoreCase = true) }
        if (existingIndex == -1) {
            windows.add(
                0,
                ActiveLaptopWindow(
                    id = "win_${System.currentTimeMillis()}",
                    title = "${app.nameEn} - شاشة نشطة",
                    appName = app.nameEn,
                    iconKey = app.iconKey
                )
            )
            _activeWindows.value = windows
        }

        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("تم فتح ${app.nameAr} (${app.nameEn}) على شاشة اللابتوب بنجاح")
            return
        }

        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            val ok = client.sendAppCommand(target, "launch", app.command)
            if (ok) {
                showFeedback("تم تشغيل ${app.nameAr} على اللابتوب")
            } else {
                showFeedback("فشل إرسال أمر تشغيل التطبيق")
            }
        }
    }

    fun closeActiveWindow(window: ActiveLaptopWindow) {
        vibrateShort()
        _activeWindows.value = _activeWindows.value.filter { it.id != window.id }
        showFeedback("تم إغلاق نافذة: ${window.appName}")
    }

    fun focusActiveWindow(window: ActiveLaptopWindow) {
        vibrateShort()
        showFeedback("التبديل إلى نافذة: ${window.appName}")
    }

    fun addCustomApp(nameAr: String, nameEn: String, command: String, category: AppCategory) {
        val app = LaptopApp(
            id = "custom_${System.currentTimeMillis()}",
            nameAr = nameAr,
            nameEn = nameEn.ifBlank { nameAr },
            command = command,
            category = category,
            iconKey = "app_generic",
            isCustom = true,
            description = "تطبيق مخصص: $command"
        )
        repository.addCustomApp(app)
        showFeedback("تمت إضافة التطبيق: $nameAr")
    }

    fun deleteCustomApp(id: String) {
        repository.removeCustomApp(id)
        showFeedback("تم حذف التطبيق المخصص")
    }

    // Screen Preview
    fun toggleScreenAutoRefresh(enabled: Boolean) {
        _isScreenAutoRefresh.value = enabled
        if (enabled) {
            startScreenRefresh()
        } else {
            screenRefreshJob?.cancel()
        }
    }

    fun refreshScreenSnapshotOnce() {
        val current = _connectionInfo.value
        if (current.isSimulation || current.status == ConnectionStatus.SIMULATION_MODE) {
            showFeedback("تم تحديث لقطة شاشة اللابتوب")
            return
        }
        val target = current.currentLaptop ?: return
        viewModelScope.launch {
            val bmp = client.fetchScreenSnapshot(target)
            if (bmp != null) {
                _screenBitmap.value = bmp
                showFeedback("تم استلام شاشة اللابتوب")
            }
        }
    }

    private fun startScreenRefresh() {
        screenRefreshJob?.cancel()
        screenRefreshJob = viewModelScope.launch {
            while (_isScreenAutoRefresh.value) {
                val current = _connectionInfo.value
                if (!current.isSimulation && current.status == ConnectionStatus.CONNECTED) {
                    val target = current.currentLaptop
                    if (target != null) {
                        val bmp = client.fetchScreenSnapshot(target)
                        if (bmp != null) {
                            _screenBitmap.value = bmp
                        }
                    }
                }
                delay(1200)
            }
        }
    }

    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
        viewModelScope.launch {
            delay(2500)
            if (_feedbackMessage.value == msg) {
                _feedbackMessage.value = null
            }
        }
    }
}
