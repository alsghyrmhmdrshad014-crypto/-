package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.TopStatusBar
import com.example.ui.screens.ConnectionScreen
import com.example.ui.screens.ControlsScreen
import com.example.ui.screens.LaptopAppsScreen
import com.example.ui.screens.TrackpadScreen
import com.example.ui.theme.MyApplicationTheme

enum class MainTab(
    val titleAr: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    TRACKPAD("الفأرة واللمس", "Trackpad", Icons.Filled.Mouse, Icons.Outlined.Mouse, "nav_trackpad"),
    APPS("تطبيقات اللابتوب", "Laptop Apps", Icons.Filled.Apps, Icons.Outlined.Apps, "nav_apps"),
    CONTROLS("التحكم والوسائط", "Controls", Icons.Filled.Tune, Icons.Outlined.Tune, "nav_controls"),
    CONNECTION("الاتصال والسيرفر", "Wi-Fi Server", Icons.Filled.Wifi, Icons.Outlined.Wifi, "nav_connection")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen(
    viewModel: MainViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(MainTab.TRACKPAD) }

    val connectionInfo by viewModel.connectionInfo.collectAsStateWithLifecycle()
    val sensitivity by viewModel.sensitivity.collectAsStateWithLifecycle()
    val allApps by viewModel.allApps.collectAsStateWithLifecycle()
    val activeWindows by viewModel.activeWindows.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val volumeLevel by viewModel.volumeLevel.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val screenBitmap by viewModel.screenBitmap.collectAsStateWithLifecycle()
    val isScreenAutoRefresh by viewModel.isScreenAutoRefresh.collectAsStateWithLifecycle()
    val savedLaptops by viewModel.savedLaptops.collectAsStateWithLifecycle()
    val isScanningNetwork by viewModel.isScanningNetwork.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    BackHandler(enabled = currentTab != MainTab.TRACKPAD) {
        currentTab = MainTab.TRACKPAD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopStatusBar(
                    connectionInfo = connectionInfo,
                    onStatusClick = { currentTab = MainTab.CONNECTION },
                    onReconnectClick = {
                        if (connectionInfo.isSimulation) {
                            viewModel.toggleSimulationMode(false)
                        } else {
                            viewModel.connectToLaptop(connectionInfo.currentLaptop)
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleAr
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleAr,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.TRACKPAD -> {
                    TrackpadScreen(
                        sensitivity = sensitivity,
                        onSensitivityChange = { viewModel.setSensitivity(it) },
                        onMouseMove = { dx, dy -> viewModel.onMouseMove(dx, dy) },
                        onClick = { viewModel.onClick(it) },
                        onScroll = { viewModel.onScroll(it) },
                        onKey = { viewModel.sendKey(it) }
                    )
                }
                MainTab.APPS -> {
                    LaptopAppsScreen(
                        apps = allApps,
                        activeWindows = activeWindows,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onLaunchApp = { viewModel.launchApp(it) },
                        onFocusWindow = { viewModel.focusActiveWindow(it) },
                        onCloseWindow = { viewModel.closeActiveWindow(it) },
                        onAddCustomApp = { nameAr, nameEn, cmd, cat ->
                            viewModel.addCustomApp(nameAr, nameEn, cmd, cat)
                        },
                        onDeleteCustomApp = { viewModel.deleteCustomApp(it) }
                    )
                }
                MainTab.CONTROLS -> {
                    ControlsScreen(
                        volumeLevel = volumeLevel,
                        isMuted = isMuted,
                        screenBitmap = screenBitmap,
                        isScreenAutoRefresh = isScreenAutoRefresh,
                        onVolumeChange = { viewModel.changeVolume(it) },
                        onVolumeSliderChange = { viewModel.setVolume(it) },
                        onToggleMute = { viewModel.toggleMute() },
                        onMediaPlayPause = { viewModel.mediaPlayPause() },
                        onMediaNext = { viewModel.mediaNext() },
                        onMediaPrev = { viewModel.mediaPrev() },
                        onSendText = { viewModel.sendText(it) },
                        onSendKey = { viewModel.sendKey(it) },
                        onSendShortcut = { viewModel.sendShortcut(it) },
                        onSystemAction = { viewModel.systemAction(it) },
                        onRefreshScreen = { viewModel.refreshScreenSnapshotOnce() },
                        onToggleScreenAutoRefresh = { viewModel.toggleScreenAutoRefresh(it) }
                    )
                }
                MainTab.CONNECTION -> {
                    ConnectionScreen(
                        connectionInfo = connectionInfo,
                        savedLaptops = savedLaptops,
                        isScanningNetwork = isScanningNetwork,
                        onConnectLaptop = { viewModel.connectToLaptop(it) },
                        onSaveLaptop = { name, ip, port, os ->
                            viewModel.saveLaptopProfile(name, ip, port, os)
                        },
                        onDeleteLaptop = { viewModel.deleteLaptop(it) },
                        onToggleSimulation = { viewModel.toggleSimulationMode(it) },
                        onScanNetwork = { viewModel.scanLocalNetwork() },
                        onShowFeedback = { viewModel.showFeedback(it) }
                    )
                }
            }

            // Tactile Feedback Banner overlay
            AnimatedVisibility(
                visible = feedbackMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                feedbackMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.inversePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
