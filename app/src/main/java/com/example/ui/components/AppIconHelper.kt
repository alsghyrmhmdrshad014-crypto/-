package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class IconStyle(
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color = Color.White
)

object AppIconHelper {

    fun getStyleForApp(iconKey: String): IconStyle {
        return when (iconKey.lowercase()) {
            "chrome" -> IconStyle(Icons.Default.Language, Color(0xFFEA4335))
            "edge" -> IconStyle(Icons.Default.Web, Color(0xFF0078D7))
            "firefox" -> IconStyle(Icons.Default.Language, Color(0xFFFF7139))
            "code" -> IconStyle(Icons.Default.Code, Color(0xFF007ACC))
            "word" -> IconStyle(Icons.Default.Description, Color(0xFF2B579A))
            "excel" -> IconStyle(Icons.Default.TableChart, Color(0xFF217346))
            "powerpoint" -> IconStyle(Icons.Default.PlayCircle, Color(0xFFD24726))
            "spotify" -> IconStyle(Icons.Default.Headphones, Color(0xFF1DB954))
            "vlc" -> IconStyle(Icons.Default.VideoLibrary, Color(0xFFFF8800))
            "youtube" -> IconStyle(Icons.Default.PlayArrow, Color(0xFFFF0000))
            "folder" -> IconStyle(Icons.Default.Folder, Color(0xFFFFB900))
            "calc" -> IconStyle(Icons.Default.Calculate, Color(0xFF008272))
            "notepad" -> IconStyle(Icons.Default.Description, Color(0xFF68768A))
            "terminal", "cmd" -> IconStyle(Icons.Default.Terminal, Color(0xFF1E1E1E))
            "taskmgr" -> IconStyle(Icons.Default.Widgets, Color(0xFF5C2D91))
            "settings" -> IconStyle(Icons.Default.Settings, Color(0xFF4A5568))
            "window" -> IconStyle(Icons.Default.Window, Color(0xFF0078D4))
            else -> IconStyle(Icons.Default.Widgets, Color(0xFF4F46E5))
        }
    }
}

@Composable
fun AppIconBadge(
    iconKey: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shapeRadius: Dp = 12.dp
) {
    val style = AppIconHelper.getStyleForApp(iconKey)
    Box(
        modifier = modifier
            .size(size)
            .background(style.backgroundColor, RoundedCornerShape(shapeRadius)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = style.iconColor,
            modifier = Modifier.size(size * 0.58f)
        )
    }
}
