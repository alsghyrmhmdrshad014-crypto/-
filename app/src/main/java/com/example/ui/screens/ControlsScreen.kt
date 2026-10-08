package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlsScreen(
    volumeLevel: Int,
    isMuted: Boolean,
    screenBitmap: Bitmap?,
    isScreenAutoRefresh: Boolean,
    onVolumeChange: (Int) -> Unit,
    onVolumeSliderChange: (Int) -> Unit,
    onToggleMute: () -> Unit,
    onMediaPlayPause: () -> Unit,
    onMediaNext: () -> Unit,
    onMediaPrev: () -> Unit,
    onSendText: (String) -> Unit,
    onSendKey: (String) -> Unit,
    onSendShortcut: (List<String>) -> Unit,
    onSystemAction: (String) -> Unit,
    onRefreshScreen: () -> Unit,
    onToggleScreenAutoRefresh: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    var showShutdownDialog by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Keyboard & Typing Section
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("keyboard_card"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "لوحة المفاتيح والكتابة عن بُعد",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Typing field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("اكتب أي نص أو رابط هنا...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("remote_text_field"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (textInput.isNotEmpty()) {
                                        onSendText(textInput)
                                        textInput = ""
                                    }
                                }
                            )
                        )

                        FilledIconButton(
                            onClick = {
                                if (textInput.isNotEmpty()) {
                                    onSendText(textInput)
                                    textInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("send_text_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "إرسال النص للابتوب"
                            )
                        }
                    }

                    // Arrow Keys & Navigation Pad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Directional Cross
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            KeyButton("↑", "up", onSendKey, modifier = Modifier.size(38.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                KeyButton("←", "left", onSendKey, modifier = Modifier.size(38.dp))
                                KeyButton("↓", "down", onSendKey, modifier = Modifier.size(38.dp))
                                KeyButton("→", "right", onSendKey, modifier = Modifier.size(38.dp))
                            }
                        }

                        // Special Action Keys Grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f).padding(start = 16.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                KeyTextButton("Enter", "enter", onSendKey, Modifier.weight(1f))
                                KeyTextButton("Backspace", "backspace", onSendKey, Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                KeyTextButton("Win ⊞", "win", onSendKey, Modifier.weight(1f))
                                KeyTextButton("Space ␣", "space", onSendKey, Modifier.weight(1f))
                            }
                        }
                    }

                    // Function Keys Bar (F1-F12)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..12).forEach { fNum ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .width(46.dp)
                                    .height(34.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onSendKey("f$fNum") },
                                    modifier = Modifier.fillMaxSize().testTag("key_f$fNum"),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("F$fNum", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick System Shortcuts Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shortcuts_card"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اختصارات ويندوز / ماك السريعة",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShortcutChip(
                            icon = Icons.Default.ContentCopy,
                            title = "نسخ",
                            keys = listOf("ctrl", "c"),
                            onClick = { onSendShortcut(listOf("ctrl", "c")) },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutChip(
                            icon = Icons.Default.ContentPaste,
                            title = "لصق",
                            keys = listOf("ctrl", "v"),
                            onClick = { onSendShortcut(listOf("ctrl", "v")) },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutChip(
                            icon = Icons.Default.SettingsBackupRestore,
                            title = "تراجع",
                            keys = listOf("ctrl", "z"),
                            onClick = { onSendShortcut(listOf("ctrl", "z")) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShortcutChip(
                            icon = Icons.Default.Window,
                            title = "تبديل (Alt+Tab)",
                            keys = listOf("alt", "tab"),
                            onClick = { onSendShortcut(listOf("alt", "tab")) },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutChip(
                            icon = Icons.Default.Computer,
                            title = "سطح المكتب",
                            keys = listOf("win", "d"),
                            onClick = { onSendShortcut(listOf("win", "d")) },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutChip(
                            icon = Icons.Default.SelectAll,
                            title = "تحديد الكل",
                            keys = listOf("ctrl", "a"),
                            onClick = { onSendShortcut(listOf("ctrl", "a")) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Media & Volume Controller Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("media_card"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "التحكم بالصوت والوسائط",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = if (isMuted) "مكتوم" else "$volumeLevel%",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Volume Slider & Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onVolumeChange(-5) },
                            modifier = Modifier.testTag("volume_down_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = "خفض الصوت")
                        }

                        Slider(
                            value = volumeLevel.toFloat(),
                            onValueChange = { onVolumeSliderChange(it.toInt()) },
                            valueRange = 0f..100f,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("volume_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        IconButton(
                            onClick = { onVolumeChange(5) },
                            modifier = Modifier.testTag("volume_up_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "رفع الصوت")
                        }

                        FilledIconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("mute_button"),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeMute,
                                contentDescription = "كتم الصوت",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Media Playback Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onMediaPrev,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("media_prev_button")
                        ) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "السابق", modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        FilledIconButton(
                            onClick = onMediaPlayPause,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("media_play_pause_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "تشغيل / إيقاف", modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = onMediaNext,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("media_next_button")
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "التالي", modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        // Presentation Remote Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("presentation_card"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CoPresent,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جهاز تحكم العروض التقديمية (Slides Remote)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSendKey("left") },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الشريحة السابقة", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onSendKey("right") },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("الشريحة التالية", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSendKey("f5") },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("بدء العرض (F5)", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onSendKey("esc") },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إنهاء العرض (Esc)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live Laptop Screen Preview Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("screen_preview_card"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "نظرة على شاشة اللابتوب",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onRefreshScreen,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "تحديث لقطة الشاشة")
                            }
                        }
                    }

                    // Display screen or mockup
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (screenBitmap != null) {
                            Image(
                                bitmap = screenBitmap.asImageBitmap(),
                                contentDescription = "شاشة اللابتوب الحية",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            // High-tech simulated laptop desktop preview
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "شاشة اللابتوب نشطة",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "انقر فوق زر التحديث لأخذ لقطة حية من الشاشة",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Power & System Actions Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("system_power_card"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "التحكم بالطاقة والنظام",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSystemAction("lock") },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("قفل الشاشة", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onSystemAction("sleep") },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Nightlight, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("وضع السكون", fontSize = 11.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showShutdownDialog = "restart" },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعادة تشغيل", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showShutdownDialog = "shutdown" },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إيقاف التشغيل", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Safety Confirmation Dialog for Shutdown / Restart
    showShutdownDialog?.let { action ->
        val isShutdown = action == "shutdown"
        AlertDialog(
            onDismissRequest = { showShutdownDialog = null },
            title = {
                Text(if (isShutdown) "تأكيد إيقاف تشغيل اللابتوب" else "تأكيد إعادة التشغيل", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    if (isShutdown)
                        "هل أنت متأكد من رغبتك في إيقاف تشغيل جهاز اللابتوب عبر الواي فاي؟ تأكد من حفظ أعمالك أولاً."
                    else
                        "هل أنت متأكد من رغبتك في إعادة تشغيل اللابتوب الآن؟"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSystemAction(action)
                        showShutdownDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isShutdown) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(if (isShutdown) "نعم، إيقاف التشغيل" else "نعم، إعادة التشغيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShutdownDialog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun KeyButton(
    symbol: String,
    key: String,
    onSendKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = { onSendKey(key) },
            modifier = Modifier.fillMaxSize().testTag("key_$key"),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(symbol, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun KeyTextButton(
    label: String,
    key: String,
    onSendKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.height(38.dp)
    ) {
        OutlinedButton(
            onClick = { onSendKey(key) },
            modifier = Modifier.fillMaxSize().testTag("key_$key"),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ShortcutChip(
    icon: ImageVector,
    title: String,
    keys: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        modifier = modifier.height(44.dp)
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().testTag("shortcut_${keys.joinToString("_")}"),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}
