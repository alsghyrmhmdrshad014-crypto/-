package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionInfo
import com.example.model.ConnectionStatus
import com.example.model.LaptopProfile
import com.example.model.OperatingSystem
import com.example.network.PythonServerScript

@Composable
fun ConnectionScreen(
    connectionInfo: ConnectionInfo,
    savedLaptops: List<LaptopProfile>,
    isScanningNetwork: Boolean,
    onConnectLaptop: (LaptopProfile) -> Unit,
    onSaveLaptop: (name: String, ip: String, port: Int, os: OperatingSystem) -> Unit,
    onDeleteLaptop: (String) -> Unit,
    onToggleSimulation: (Boolean) -> Unit,
    onScanNetwork: () -> Unit,
    onShowFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var isScriptExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simulation Mode Toggle Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulation_mode_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (connectionInfo.isSimulation)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "وضع التجربة التفاعلي (Simulation)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "اختبر جميع وظائف التحكم دون الحاجة للاتصال بلابتوب حقيقي",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = connectionInfo.isSimulation,
                        onCheckedChange = { onToggleSimulation(it) },
                        modifier = Modifier.testTag("simulation_mode_switch")
                    )
                }
            }
        }

        // Active / Saved Laptops Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأجهزة المحفوظة (${savedLaptops.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onScanNetwork,
                        enabled = !isScanningNetwork,
                        modifier = Modifier.height(38.dp).testTag("scan_network_button"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        if (isScanningNetwork) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.WifiFind, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فحص الشبكة", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.height(38.dp).testTag("add_laptop_button"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة جهاز", fontSize = 11.sp)
                    }
                }
            }
        }

        // Saved Laptops List
        if (savedLaptops.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Laptop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا يوجد لابتوب مضاف حتى الآن", fontWeight = FontWeight.Medium)
                        Text(
                            text = "اضغط على زر (إضافة جهاز) للاتصال باللابتوب",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(savedLaptops, key = { it.id }) { laptop ->
                val isCurrent = connectionInfo.currentLaptop?.id == laptop.id
                val isConnected = isCurrent && connectionInfo.status == ConnectionStatus.CONNECTED

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().testTag("saved_laptop_${laptop.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isConnected)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isConnected) Color(0xFF10B981).copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Laptop,
                                    contentDescription = null,
                                    tint = if (isConnected) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = laptop.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (isConnected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "متصل الآن",
                                                color = Color(0xFF047857),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "IP: ${laptop.ipAddress}:${laptop.port} • ${laptop.os.displayName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onDeleteLaptop(laptop.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف الجهاز",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = { onConnectLaptop(laptop) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(38.dp).testTag("connect_btn_${laptop.id}"),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text(if (isConnected) "إعادة فحص" else "اتصال", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Companion Python Server Setup Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("python_server_setup_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "سيرفر اللابتوب (Python Companion)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        IconButton(
                            onClick = { isScriptExpanded = !isScriptExpanded }
                        ) {
                            Icon(
                                imageVector = if (isScriptExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "عرض الكود"
                            )
                        }
                    }

                    Text(
                        text = "خطوات تشغيل التحكم الفعلي باللابتوب عبر الواي فاي:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        StepItem("1", "قم بتثبيت المكتبات على اللابتوب (مرة واحدة):", "pip install flask pyautogui pillow")
                        StepItem("2", "انسخ كود السكربت بالزر أدناه واحفظه باسم:", PythonServerScript.SCRIPT_FILENAME)
                        StepItem("3", "شغّل السكربت على اللابتوب:", "python ${PythonServerScript.SCRIPT_FILENAME}")
                        StepItem("4", "سيظهر لك الـ IP الخاص باللابتوب في شاشة الأوامر، اكتبه هنا واضغط اتصال!")
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Laptop Server Script", PythonServerScript.SCRIPT_CODE)
                            clipboard.setPrimaryClip(clip)
                            onShowFeedback("تم نسخ كود سيرفر اللابتوب إلى الحافظة بنجاح!")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("copy_script_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نسخ كود السكربت بالكامل (Copy Python Script)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (isScriptExpanded) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth().height(260.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = PythonServerScript.SCRIPT_CODE,
                                        color = Color(0xFF38BDF8),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddNewLaptopDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, ip, port, os ->
                onSaveLaptop(name, ip, port, os)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StepItem(number: String, text: String, code: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (code != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewLaptopDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, ip: String, port: Int, os: OperatingSystem) -> Unit
) {
    var name by remember { mutableStateOf("لابتوب جديد") }
    var ip by remember { mutableStateOf("192.168.1.") }
    var portText by remember { mutableStateOf("8080") }
    var selectedOs by remember { mutableStateOf(OperatingSystem.WINDOWS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إضافة لابتوب جديد", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم اللابتوب (مثال: لابتوب الألعاب)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_laptop_name_field")
                )

                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("عنوان IP اللابتوب في الواي فاي") },
                    placeholder = { Text("مثال: 192.168.1.105") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_laptop_ip_field")
                )

                OutlinedTextField(
                    value = portText,
                    onValueChange = { portText = it },
                    label = { Text("رقم المنفذ (Port)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_laptop_port_field")
                )

                Text("نظام تشغيل اللابتوب:", style = MaterialTheme.typography.bodySmall)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OperatingSystem.entries.forEach { os ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedOs = os }
                        ) {
                            RadioButton(
                                selected = selectedOs == os,
                                onClick = { selectedOs = os }
                            )
                            Text(os.displayName, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val port = portText.toIntOrNull() ?: 8080
                    if (name.isNotBlank() && ip.isNotBlank()) {
                        onSave(name, ip, port, selectedOs)
                    }
                },
                enabled = name.isNotBlank() && ip.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_laptop_btn")
            ) {
                Text("حفظ والاتصال")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
