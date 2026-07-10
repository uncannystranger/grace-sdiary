package com.grace.sdiary.ui.screens.settings

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.model.ThemeMode
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val themeMode by viewModel.theme.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val reducedMotion by viewModel.reducedMotion.collectAsState()
    val highContrast by viewModel.highContrast.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val backupStatus by viewModel.backupStatus.collectAsState()

    var editingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(userName) }

    LaunchedEffect(userName) { nameInput = userName }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) viewModel.restoreBackup(uri)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = glass.textHigh
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Settings",
                            style = AppTypography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = glass.textHigh
                        )
                    }
                }

                item {
                    SectionCard(
                        title = "Appearance",
                        icon = Icons.Outlined.PhoneIphone
                    ) {
                        Text(
                            text = "Theme",
                            style = AppTypography.labelLarge,
                            color = glass.textMid
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeMode.entries.forEach { mode ->
                                val isSelected = themeMode == mode
                                val label = when (mode) {
                                    ThemeMode.SYSTEM -> "System"
                                    ThemeMode.LIGHT -> "Light"
                                    ThemeMode.DARK -> "Dark"
                                }
                                val icon = when (mode) {
                                    ThemeMode.SYSTEM -> Icons.Outlined.PhoneIphone
                                    ThemeMode.LIGHT -> Icons.Outlined.LightMode
                                    ThemeMode.DARK -> Icons.Outlined.DarkMode
                                }
                                ThemeButton(
                                    label = label,
                                    icon = icon,
                                    isSelected = isSelected,
                                    onClick = { viewModel.setTheme(mode) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Font Size",
                            style = AppTypography.labelLarge,
                            color = glass.textMid
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("A", style = AppTypography.bodySmall, color = glass.textLow)
                            Slider(
                                value = fontScale,
                                onValueChange = viewModel::setFontScale,
                                valueRange = 0.8f..1.4f,
                                steps = 5,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Gold,
                                    activeTrackColor = Gold,
                                    inactiveTrackColor = glass.glassBorder
                                )
                            )
                            Text("A", style = AppTypography.titleLarge, color = glass.textLow)
                        }

                        Spacer(Modifier.height(12.dp))

                        SettingsToggle(
                            title = "Reduced Motion",
                            description = "Minimize animations",
                            checked = reducedMotion,
                            onCheckedChange = viewModel::setReducedMotion
                        )

                        SettingsToggle(
                            title = "High Contrast",
                            description = "Increase visual contrast",
                            checked = highContrast,
                            onCheckedChange = viewModel::setHighContrast
                        )
                    }
                }

                item {
                    SectionCard(
                        title = "Profile",
                        icon = Icons.Filled.Person
                    ) {
                        if (editingName) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text("Your Name") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Gold,
                                        unfocusedBorderColor = glass.glassBorder,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = Gold,
                                        focusedTextColor = glass.textHigh,
                                        unfocusedTextColor = glass.textHigh,
                                        focusedLabelColor = Gold,
                                        unfocusedLabelColor = glass.textMid
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                FilledTonalButton(
                                    onClick = {
                                        viewModel.setUserName(nameInput.trim())
                                        editingName = false
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Gold.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Save", color = Gold)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingName = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Display Name",
                                        style = AppTypography.labelLarge,
                                        color = glass.textMid
                                    )
                                    Text(
                                        text = userName.ifBlank { "Sihaam" },
                                        style = AppTypography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = glass.textHigh
                                    )
                                }
                                Text(
                                    text = "Edit",
                                    style = AppTypography.labelLarge,
                                    color = Teal
                                )
                            }
                        }
                    }
                }

                item {
                    SectionCard(
                        title = "Notifications",
                        icon = Icons.Filled.Notifications
                    ) {
                        SettingsToggle(
                            title = "Enable Notifications",
                            description = "Receive reminders and alerts",
                            checked = notificationsEnabled,
                            onCheckedChange = viewModel::setNotificationsEnabled
                        )
                    }
                }

                item {
                    SectionCard(
                        title = "Data",
                        icon = Icons.Filled.Storage
                    ) {
                        Button(
                            onClick = viewModel::createBackup,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gold.copy(alpha = 0.2f),
                                contentColor = Gold
                            )
                        ) {
                            Text("Create Backup", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                restoreLauncher.launch(arrayOf("application/octet-stream", "*/*"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = glass.textHigh
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(glass.glassBorder)
                            )
                        ) {
                            Text("Restore Backup", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.exportData("json") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Teal
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Teal.copy(alpha = 0.4f))
                                )
                            ) {
                                Text("Export JSON")
                            }
                            OutlinedButton(
                                onClick = { viewModel.exportData("csv") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Teal
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Teal.copy(alpha = 0.4f))
                                )
                            ) {
                                Text("Export CSV")
                            }
                        }

                        if (backupStatus != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = backupStatus!!,
                                style = AppTypography.bodySmall,
                                color = if (backupStatus!!.contains("fail") || backupStatus!!.contains("Fail"))
                                    com.grace.sdiary.ui.theme.Error
                                else Teal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        glass.glassBackground,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                item {
                    SectionCard(
                        title = "About",
                        icon = Icons.Filled.Info
                    ) {
                        Text(
                            text = "Sihaam's Diary",
                            style = AppTypography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = glass.textHigh
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0.0",
                            style = AppTypography.bodyMedium,
                            color = glass.textMid
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Made with \u2764\ufe0f",
                            style = AppTypography.bodyMedium,
                            color = glass.textLow
                        )
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = GraceTheme.glassColors
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = AppTypography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh
                )
            }
            HorizontalDivider(color = glass.glassBorder)
            content()
        }
    }
}

@Composable
private fun ThemeButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = GraceTheme.glassColors
    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.25f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "themeBg"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Gold.copy(alpha = bgAlpha)
                else glass.glassBackground
            )
            .then(
                if (isSelected) Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(Gold.copy(alpha = 0.15f), Color.Transparent)
                        ),
                        RoundedCornerShape(12.dp)
                    )
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Check else icon,
            contentDescription = null,
            tint = if (isSelected) Gold else glass.textMid,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = AppTypography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Gold else glass.textMid
        )
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val glass = GraceTheme.glassColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTypography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = glass.textHigh
            )
            Text(
                text = description,
                style = AppTypography.bodySmall,
                color = glass.textLow
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Gold,
                checkedTrackColor = Gold.copy(alpha = 0.3f),
                uncheckedThumbColor = glass.textMid,
                uncheckedTrackColor = glass.glassBackground
            )
        )
    }
}
