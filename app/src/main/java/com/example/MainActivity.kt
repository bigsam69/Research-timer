package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.core.content.ContextCompat
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val focusManager = LocalFocusManager.current
                var currentScreen by remember { mutableStateOf("main") }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { focusManager.clearFocus() }
                        ),
                    containerColor = CocStoneBg
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // 1. CoC Style Wooden Header
                            WoodenHeaderBar(
                                currentScreen = currentScreen,
                                onNavigate = { currentScreen = it }
                            )

                            // 2. Main Content Area
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (currentScreen == "settings") {
                                    SettingsScreenContent()
                                } else {
                                    ResearchTimerScreenContent()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WoodenHeaderBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    // CoC Style Wooden Bar with a gradient and thick borders
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(
                width = 3.dp,
                color = CocBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(CocWoodCardLight, CocWoodCard)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (currentScreen == "settings") {
            // Back button on the left
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .testTag("back_button")
                    .clickable { onNavigate("main") }
                    .drawBehind {
                        drawRoundRect(
                            color = CocBorder,
                            topLeft = Offset(0f, 4f),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                        drawRoundRect(
                            color = CocGoldDark,
                            topLeft = Offset(0f, 2f),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                        drawRoundRect(
                            color = CocGold,
                            topLeft = Offset(0f, 0f),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height - 2f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                    .border(2.dp, CocBorder, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◀",
                    style = TextStyle(
                        color = CocBorder,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }

        // Title text with bold, uppercase, gaming text-shadow style
        Box(contentAlignment = Alignment.Center) {
            val titleText = if (currentScreen == "settings") "SETTINGS" else "RESEARCH TIMER"
            // Shadow text behind
            Text(
                text = titleText,
                style = TextStyle(
                    color = CocBorder,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.offset(x = 1.5.dp, y = 1.5.dp)
            )
            // Foreground text
            Text(
                text = titleText,
                style = TextStyle(
                    color = CocParchmentBg,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            )
        }

        if (currentScreen == "main") {
            // Settings button on the right
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(40.dp)
                    .testTag("settings_button")
                    .clickable { onNavigate("settings") }
                    .drawBehind {
                        drawRoundRect(
                            color = CocBorder,
                            topLeft = Offset(0f, 4f),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                        drawRoundRect(
                            color = CocGoldDark,
                            topLeft = Offset(0f, 2f),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                        drawRoundRect(
                            color = CocGold,
                            topLeft = Offset(0f, 0f),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height - 2f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                    .border(2.dp, CocBorder, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚙️", style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}

@Composable
fun ResearchTimerScreenContent() {
    val context = LocalContext.current
    
    // Collect states
    val remainingSeconds by TimerStateManager.remainingSeconds.collectAsState()
    val totalDurationSeconds by TimerStateManager.totalDurationSeconds.collectAsState()
    val isRunning by TimerStateManager.isRunning.collectAsState()
    val isAlarmActive by TimerStateManager.isAlarmActive.collectAsState()
    
    val inputDaysStr by TimerStateManager.inputDays.collectAsState()
    val inputHoursStr by TimerStateManager.inputHours.collectAsState()
    val inputOffsetMinutesStr by TimerStateManager.inputOffsetMinutes.collectAsState()
    
    // Notification permission launcher
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    // Auto permission check
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Mathematical values
    val daysVal = inputDaysStr.toLongOrNull() ?: 0L
    val hoursVal = inputHoursStr.toLongOrNull() ?: 0L
    
    val totalInputSeconds = (daysVal * 24L * 3600L) + (hoursVal * 3600L)
    val boostedSeconds = totalInputSeconds / 24L
    
    // Efficiency gain calculation: Saved time = Total original duration minus speedup duration
    val efficiencyGainSeconds = totalInputSeconds - boostedSeconds
    
    // Quick set timer is boosted duration minus the custom user offset minutes
    val offsetMinVal = inputOffsetMinutesStr.toLongOrNull() ?: 0L
    val quickTimerDurationSeconds = boostedSeconds - (offsetMinVal * 60L)
    
    // Determine actual timer setting value
    val finalTimerDuration = if (quickTimerDurationSeconds <= 0L) {
        10L // Minimum demo fallback
    } else {
        quickTimerDurationSeconds
    }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Notification permission banner if missing
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ClashPermissionBanner {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Active countdown timer panel styled with dashed border
        AnimatedVisibility(
            visible = isRunning || isAlarmActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ActiveResearchTimerPanel(
                isRunning = isRunning,
                isAlarmActive = isAlarmActive,
                remainingSeconds = remainingSeconds,
                totalDurationSeconds = totalDurationSeconds,
                onCancelClick = { TimerService.stopService(context) },
                onDismissAlarmClick = { TimerService.stopAlarm(context) }
            )
        }

        // Input Section: Stone Panel
        ClashCard(
            modifier = Modifier.fillMaxWidth(),
            isWood = false
        ) {
            Text(
                text = "LABORATORY DURATION",
                style = TextStyle(
                    color = CocTextGrey,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Days Input
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClashTextField(
                        value = inputDaysStr,
                        onValueChange = { TimerStateManager.inputDays.value = it },
                        label = "DAYS",
                        testTag = "input_days"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ClashAdjusterButton(symbol = "-", onClick = {
                            val current = inputDaysStr.toLongOrNull() ?: 0L
                            TimerStateManager.inputDays.value = maxOf(0L, current - 1L).toString()
                        }, testTag = "sub_day")
                        ClashAdjusterButton(symbol = "+", onClick = {
                            val current = inputDaysStr.toLongOrNull() ?: 0L
                            TimerStateManager.inputDays.value = minOf(99L, current + 1L).toString()
                        }, testTag = "add_day")
                    }
                }

                Text(
                    text = ":",
                    style = TextStyle(
                        color = CocTextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                    ),
                    modifier = Modifier.padding(bottom = 44.dp)
                )

                // Hours Input
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClashTextField(
                        value = inputHoursStr,
                        onValueChange = { TimerStateManager.inputHours.value = it },
                        label = "HOURS",
                        testTag = "input_hours"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ClashAdjusterButton(symbol = "-", onClick = {
                            val current = inputHoursStr.toLongOrNull() ?: 0L
                            TimerStateManager.inputHours.value = maxOf(0L, current - 1L).toString()
                        }, testTag = "sub_hour")
                        ClashAdjusterButton(symbol = "+", onClick = {
                            val current = inputHoursStr.toLongOrNull() ?: 0L
                            TimerStateManager.inputHours.value = minOf(23L, current + 1L).toString()
                        }, testTag = "add_hour")
                    }
                }
            }
        }

        // Offset Input Section
        ClashCard(
            modifier = Modifier.fillMaxWidth(),
            isWood = false
        ) {
            Text(
                text = "ALARM OFFSET MINUTES",
                style = TextStyle(
                    color = CocTextGrey,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClashAdjusterButton(symbol = "-", onClick = {
                    val current = inputOffsetMinutesStr.toLongOrNull() ?: 0L
                    TimerStateManager.inputOffsetMinutes.value = maxOf(0L, current - 1L).toString()
                }, testTag = "sub_offset")

                Spacer(modifier = Modifier.width(16.dp))

                Box(modifier = Modifier.width(120.dp)) {
                    ClashTextField(
                        value = inputOffsetMinutesStr,
                        onValueChange = { TimerStateManager.inputOffsetMinutes.value = it },
                        label = "MINUTES",
                        testTag = "input_offset"
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                ClashAdjusterButton(symbol = "+", onClick = {
                    val current = inputOffsetMinutesStr.toLongOrNull() ?: 0L
                    TimerStateManager.inputOffsetMinutes.value = minOf(1440L, current + 1L).toString()
                }, testTag = "add_offset")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The alarm will go off this many minutes before research completion so you can prepare your next upgrade.",
                style = TextStyle(
                    color = CocTextGrey,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Magic Scroll Display: Outputs and Ratios
        MagicScrollCard(
            efficiencyGainFormatted = formatCalculatedTime(boostedSeconds),
            ratioLabel = "1/24x"
        )

        // Action Quick Set Button
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val hasValidDuration = totalInputSeconds > 0L
            val buttonSubtext = if (hasValidDuration) {
                "NEW TIME - ${inputOffsetMinutesStr}M (${formatCalculatedTime(quickTimerDurationSeconds)})"
            } else {
                "PLEASE ENTER RESEARCH DURATION"
            }

            ClashButton(
                text = "QUICK SET TIMER",
                subText = buttonSubtext,
                onClick = {
                    focusManager.clearFocus()
                    TimerService.startService(context, finalTimerDuration)
                },
                enabled = hasValidDuration,
                modifier = Modifier.fillMaxWidth(),
                testTag = "start_timer_action"
            )

            if (quickTimerDurationSeconds <= 0L && hasValidDuration) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, CocInputBorder, RoundedCornerShape(8.dp))
                        .background(CocInputBg, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "ℹ️ Boosted duration is short. The timer has been clamped to 10 seconds for convenient demonstration.",
                        style = TextStyle(
                            color = CocTextGrey,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PulsingIndicator(isAlarmActive: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p_alpha"
    )

    Box(
        modifier = modifier
            .size(10.dp)
            .drawBehind {
                drawCircle(
                    color = (if (isAlarmActive) Color.Red else Color(0xFF60A5FA)).copy(
                        alpha = pulseAlpha
                    )
                )
            }
    )
}

@Composable
fun ActiveResearchTimerPanel(
    isRunning: Boolean,
    isAlarmActive: Boolean,
    remainingSeconds: Long,
    totalDurationSeconds: Long,
    onCancelClick: () -> Unit,
    onDismissAlarmClick: () -> Unit
) {
    // CoC Active countdown running timer overlay box with dashed style border
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.25f),
                    topLeft = Offset(0f, 6f),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            }
            .border(
                width = 2.dp,
                color = CocInputBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .background(CocInputBg, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingIndicator(isAlarmActive = isAlarmActive)
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isAlarmActive) "ALARM ACTIVE" else "ACTIVE RESEARCH",
                        style = TextStyle(
                            color = if (isAlarmActive) Color.Red else Color(0xFF93C5FD),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatSeconds(remainingSeconds),
                    style = TextStyle(
                        color = CocTextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    if (isAlarmActive) {
                        ClashButton(
                            text = "DISMISS",
                            onClick = onDismissAlarmClick,
                            color = CocGold,
                            darkColor = CocGoldDark,
                            lightHighlight = CocGoldLight,
                            modifier = Modifier.weight(1f).height(44.dp),
                            testTag = "dismiss_alarm_overlay"
                        )
                    }

                    ClashButton(
                        text = "STOP",
                        onClick = onCancelClick,
                        color = Color(0xFFB91C1C),
                        darkColor = Color(0xFF7F1D1D),
                        lightHighlight = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f).height(44.dp),
                        testTag = "cancel_timer_overlay"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Embedded Potion Anim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                ElixirBubblesAnimation(isRunning = isRunning && !isAlarmActive)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simple Progress display
            val fraction = if (totalDurationSeconds > 0) remainingSeconds.toFloat() / totalDurationSeconds.toFloat() else 0f
            ClashProgress(progress = fraction, label = "${(fraction * 100).toInt()}%")
        }
    }
}

@Composable
fun SimulatedGameBottomTabs() {
    // Bottom simulation representing immersive game layouts
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .border(
                width = 3.dp,
                color = CocStoneCardBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .background(Color(0xFF1A2B3C)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Calculator (Active)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { /* Active */ }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFEAB308).copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        style = TextStyle(color = Color(0xFFEAB308), fontSize = 18.sp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "CALCULATOR",
                    style = TextStyle(
                        color = Color(0xFFEAB308),
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // Tab 2: History (Disabled/Simulated)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "HISTORY",
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // Tab 3: Settings (Disabled/Simulated)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙️",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "SETTINGS",
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ClashPermissionBanner(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.5.dp, CocBorder, RoundedCornerShape(8.dp))
            .background(CocWoodCard, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🔔 Enable Notifications to receive Laboratory completed sound alerts and keep running in the background!",
                style = TextStyle(
                    color = CocTextWhite,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = CocElixir),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "GRANT PERMISSION",
                    style = TextStyle(color = CocTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            }
        }
    }
}

@Composable
fun ElixirBubblesAnimation(isRunning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubble")
    
    val bubbleOffset1 = if (isRunning) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -80f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "b1"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val bubbleScale1 = if (isRunning) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "bs1"
        )
    } else {
        remember { mutableStateOf(0.8f) }
    }

    val bubbleOffset2 = if (isRunning) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -90f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "b2"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val circlePath = remember { Path() }
    val wavePath = remember { Path() }

    Canvas(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(CocWoodCard)
            .border(3.dp, CocBorder, CircleShape)
    ) {
        circlePath.reset()
        circlePath.addOval(androidx.compose.ui.geometry.Rect(Offset.Zero, size))
        
        clipPath(circlePath) {
            // Draw the pink elixir fluid level inside the sphere
            val fluidHeight = size.height * 0.7f
            wavePath.reset()
            wavePath.moveTo(0f, size.height)
            wavePath.lineTo(0f, size.height - fluidHeight)
            wavePath.quadraticTo(size.width * 0.25f, size.height - fluidHeight - 5f, size.width * 0.5f, size.height - fluidHeight)
            wavePath.quadraticTo(size.width * 0.75f, size.height - fluidHeight + 5f, size.width, size.height - fluidHeight)
            wavePath.lineTo(size.width, size.height)
            wavePath.close()
            
            drawPath(wavePath, color = CocElixir)
            
            // Bubbles rising
            if (isRunning) {
                drawCircle(
                    color = CocElixirLight,
                    radius = 8f * bubbleScale1.value,
                    center = Offset(size.width * 0.3f, size.height - 20f + bubbleOffset1.value)
                )
                drawCircle(
                    color = CocTextWhite,
                    radius = 12f * (bubbleScale1.value * 0.8f),
                    center = Offset(size.width * 0.6f, size.height - 40f + bubbleOffset2.value)
                )
            }
        }
    }
}



private fun formatSeconds(seconds: Long): String {
    val d = seconds / 86400
    val h = (seconds % 86400) / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (d > 0) {
        "${d}d ${h}h ${m}m ${s}s"
    } else if (h > 0) {
        "${h}h ${m}m ${s}s"
    } else {
        String.format("%02d:%02d", m, s)
    }
}

private fun formatCalculatedTime(seconds: Long): String {
    if (seconds <= 0L) return "0s"
    val d = seconds / 86400L
    val h = (seconds % 86400L) / 3600L
    val m = (seconds % 3600L) / 60L
    val s = seconds % 60L
    
    val list = mutableListOf<String>()
    if (d > 0) list.add("${d}d")
    if (h > 0) list.add("${h}h")
    if (m > 0) list.add("${m}m")
    if (s > 0 || list.isEmpty()) list.add("${s}s")
    
    return list.joinToString(" ")
}

@Composable
fun SettingsScreenContent() {
    val gradualVolume by TimerStateManager.gradualVolumeIncrease.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ClashCard(
            modifier = Modifier.fillMaxWidth(),
            isWood = false
        ) {
            Text(
                text = "ALARM OPTION",
                style = TextStyle(
                    color = CocTextGrey,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GRADUALLY INCREASE VOLUME",
                        style = TextStyle(
                            color = CocTextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When active, the alarm volume will start low and smoothly rise to full volume over 15 seconds.",
                        style = TextStyle(
                            color = CocTextGrey,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                ClashSwitch(
                    checked = gradualVolume,
                    onCheckedChange = { TimerStateManager.gradualVolumeIncrease.value = it },
                    testTag = "gradual_volume_switch"
                )
            }
        }
    }
}
