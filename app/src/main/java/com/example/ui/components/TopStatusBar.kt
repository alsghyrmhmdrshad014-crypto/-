package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionInfo
import com.example.model.ConnectionStatus

@Composable
fun TopStatusBar(
    connectionInfo: ConnectionInfo,
    onStatusClick: () -> Unit,
    onReconnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusText, statusIcon) = when (connectionInfo.status) {
        ConnectionStatus.CONNECTED -> Triple(
            Color(0xFF10B981),
            "متصل بالواي فاي",
            Icons.Default.Wifi
        )
        ConnectionStatus.CONNECTING -> Triple(
            Color(0xFFF59E0B),
            "جاري الاتصال...",
            Icons.Default.Refresh
        )
        ConnectionStatus.SIMULATION_MODE -> Triple(
            Color(0xFF6366F1),
            "وضع التجربة (Simulation)",
            Icons.Default.Sensors
        )
        ConnectionStatus.ERROR -> Triple(
            Color(0xFFEF4444),
            "خطأ في الاتصال",
            Icons.Default.WifiOff
        )
        ConnectionStatus.DISCONNECTED -> Triple(
            Color(0xFF6B7280),
            "غير متصل",
            Icons.Default.WifiOff
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("top_status_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStatusClick() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Laptop,
                        contentDescription = "Laptop",
                        tint = statusColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = connectionInfo.currentLaptop?.name ?: "لابتوب بدون اسم",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                        if (connectionInfo.latencyMs > 0) {
                            Text(
                                text = "• ${connectionInfo.latencyMs}ms",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                IconButton(
                    onClick = onReconnectClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("reconnect_button")
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = "حالة الاتصال وإعادة المحاولة",
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
