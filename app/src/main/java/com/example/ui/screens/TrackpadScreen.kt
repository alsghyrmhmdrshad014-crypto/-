package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SensitivityLevel

@Composable
fun TrackpadScreen(
    sensitivity: SensitivityLevel,
    onSensitivityChange: (SensitivityLevel) -> Unit,
    onMouseMove: (Float, Float) -> Unit,
    onClick: (String) -> Unit,
    onScroll: (Int) -> Unit,
    onKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    var isTouching by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sensitivity Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "السرعة:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SensitivityLevel.entries.forEach { level ->
                    FilterChip(
                        selected = sensitivity == level,
                        onClick = { onSensitivityChange(level) },
                        label = { Text(level.displayNameAr, fontSize = 11.sp) },
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("sensitivity_${level.name.lowercase()}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Keypad Shortcut Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Esc" to "esc",
                "Tab" to "tab",
                "Win" to "win",
                "Space" to "space",
                "Enter" to "enter",
                "⌫" to "backspace"
            ).forEach { (display, key) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    OutlinedButton(
                        onClick = { onKey(key) },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("quick_key_$key"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = display,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Trackpad Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .border(
                    width = 1.5.dp,
                    color = if (isTouching) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onClick("left")
                        },
                        onDoubleTap = {
                            onClick("double")
                        },
                        onLongPress = {
                            onClick("right")
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isTouching = true
                            touchPosition = offset
                        },
                        onDragEnd = {
                            isTouching = false
                            touchPosition = null
                        },
                        onDragCancel = {
                            isTouching = false
                            touchPosition = null
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            touchPosition = change.position
                            onMouseMove(dragAmount.x, dragAmount.y)
                        }
                    )
                }
                .testTag("trackpad_surface")
        ) {
            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 40.dp.toPx()
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)

                var x = step
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f,
                        pathEffect = pathEffect
                    )
                    x += step
                }

                var y = step
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = pathEffect
                    )
                    y += step
                }

                // Touch indicator ripple
                touchPosition?.let { pos ->
                    drawCircle(
                        color = Color(0xFF6366F1).copy(alpha = 0.25f),
                        radius = 45.dp.toPx(),
                        center = pos
                    )
                    drawCircle(
                        color = Color(0xFF6366F1).copy(alpha = 0.8f),
                        radius = 12.dp.toPx(),
                        center = pos
                    )
                }
            }

            // Trackpad Center Instruction
            if (!isTouching) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اسحب لتحريك مؤشر اللابتوب",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "انقر لليسار • ضغطة مطولة لليمين",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            // Scroll Buttons Bar on Right Edge
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    modifier = Modifier.size(42.dp)
                ) {
                    OutlinedButton(
                        onClick = { onScroll(120) },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("scroll_up_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "تمرير لأعلى",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    modifier = Modifier.size(42.dp)
                ) {
                    OutlinedButton(
                        onClick = { onScroll(-120) },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("scroll_down_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "تمرير لأسفل",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Middle Click & Double Click Helpers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onClick("double") },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("double_click_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdsClick,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("نقرة مزدوجة", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { onClick("middle") },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("middle_click_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mouse,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("الزر الأوسط", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Large Tactile Left / Right Mouse Click Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onClick("left") },
                modifier = Modifier
                    .weight(1.3f)
                    .height(64.dp)
                    .testTag("left_click_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "زر أيسر",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Left Click",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Button(
                onClick = { onClick("right") },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .testTag("right_click_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "زر أيمن",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Right Click",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
