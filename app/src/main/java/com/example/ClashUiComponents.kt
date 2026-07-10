package com.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ClashCard(
    modifier: Modifier = Modifier,
    isWood: Boolean = false,
    content: @Composable ColumnScopeImpl.() -> Unit
) {
    // If wood, uses wood gradient, else uses CocStoneCard (#2a3c50) with outer slate border (#3e566d)
    val containerShape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = modifier
            .drawBehind {
                // Drop shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.35f),
                    topLeft = Offset(0f, 10f),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
            }
            .border(4.dp, if (isWood) CocBorder else CocStoneCardBorder, containerShape)
            .background(if (isWood) CocWoodCard else CocStoneCard, containerShape)
            .padding(16.dp)
    ) {
        val scope = ColumnScopeImpl()
        Column(modifier = Modifier.fillMaxWidth()) {
            scope.content()
        }
    }
}

interface ColumnScope {
    // Empty scope marker
}

class ColumnScopeImpl : ColumnScope

@Composable
fun MagicScrollCard(
    efficiencyGainFormatted: String,
    ratioLabel: String = "1/24x",
    modifier: Modifier = Modifier
) {
    val scrollShape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
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
                .border(4.dp, CocParchmentBorder, scrollShape)
                .background(CocParchmentBg, scrollShape)
                .padding(vertical = 14.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icon block: wood background with gold lightning bolt symbol
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .border(2.dp, CocWoodCardLight, RoundedCornerShape(8.dp))
                            .background(CocWoodCard, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 22.sp
                        )
                    }

                    Column {
                        Text(
                            text = "TIME",
                            style = TextStyle(
                                color = CocParchmentTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = efficiencyGainFormatted,
                            style = TextStyle(
                                color = CocParchmentTextDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RATIO",
                        style = TextStyle(
                            color = CocParchmentTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = ratioLabel,
                        style = TextStyle(
                            color = CocParchmentTextDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Right
                        )
                    )
                }
            }
        }

        // Floating Top Amber Badge label
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .background(CocParchmentBorder, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            Text(
                text = "CALCULATED VALUE",
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun ClashButton(
    text: String,
    subText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = CocButtonGreen,
    darkColor: Color = CocButtonGreenDark,
    lightHighlight: Color = Color.White.copy(alpha = 0.25f),
    enabled: Boolean = true,
    testTag: String = "clash_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(if (subText != null) 64.dp else 54.dp)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null, // Custom 3D mechanical press feedback
                onClick = onClick
            )
            .drawBehind {
                val shadowOffset = 6.dp.toPx()
                val topPlateOffset = 2.dp.toPx()
                
                // 3D shadow depth at the bottom
                drawRoundRect(
                    color = CocButtonGreenBorder,
                    topLeft = Offset(0f, shadowOffset),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
                // Middle dark layer
                drawRoundRect(
                    color = darkColor,
                    topLeft = Offset(0f, topPlateOffset),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
                // Active foreground plate
                drawRoundRect(
                    color = if (enabled) color else Color.Gray,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height - 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
                // Horizontal glare light bar
                if (enabled) {
                    drawRoundRect(
                        color = lightHighlight,
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width, (size.height - 4f) * 0.35f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                }
            }
            .border(3.dp, CocBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text.uppercase(),
                style = TextStyle(
                    color = if (enabled) CocTextWhite else Color.LightGray,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
            )
            if (subText != null) {
                Text(
                    text = subText.uppercase(),
                    style = TextStyle(
                        color = if (enabled) CocTextWhite.copy(alpha = 0.8f) else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Composable
fun ClashAdjusterButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "adjuster"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .size(46.dp)
            .clickable(onClick = onClick)
            .drawBehind {
                // 3D shadow edge
                drawRoundRect(
                    color = CocBorder,
                    topLeft = Offset(0f, 6f),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
                // Base
                drawRoundRect(
                    color = CocGoldDark,
                    topLeft = Offset(0f, 3f),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
                // Main top face
                drawRoundRect(
                    color = CocGold,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height - 3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
            .border(2.5.dp, CocBorder, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = TextStyle(
                color = CocBorder,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun ClashTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String = "text_field"
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label.uppercase(),
            style = TextStyle(
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        var textFieldValue by remember {
            mutableStateOf(TextFieldValue(text = value))
        }

        LaunchedEffect(value) {
            if (textFieldValue.text != value) {
                textFieldValue = textFieldValue.copy(text = value, selection = TextRange.Zero)
            }
        }

        var isFocused by remember { mutableStateOf(false) }
        var selectAllPending by remember { mutableStateOf(false) }

        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                var updatedValue = newValue
                if (selectAllPending) {
                    selectAllPending = false
                    updatedValue = newValue.copy(selection = TextRange(0, newValue.text.length))
                }
                textFieldValue = updatedValue
                val filteredText = updatedValue.text.filter { it.isDigit() }
                if (filteredText.length <= 4) {
                    onValueChange(filteredText)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                color = CocGold,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(CocGold),
            singleLine = true,
            modifier = Modifier
                .testTag(testTag)
                .fillMaxWidth()
                .height(56.dp)
                .border(2.dp, CocInputBorder, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(CocInputBg)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        if (!isFocused) {
                            isFocused = true
                            selectAllPending = true
                            textFieldValue = textFieldValue.copy(
                                selection = TextRange(0, textFieldValue.text.length)
                            )
                        }
                    } else {
                        isFocused = false
                        selectAllPending = false
                    }
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun ClashProgress(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress = animateFloatAsState(targetValue = progress, label = "progress")
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ACTIVE RESEARCH PROGRESS",
                style = TextStyle(
                    color = CocTextLightGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    color = CocTextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Progress Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .border(3.dp, CocBorder, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(CocInputBg)
                .padding(2.dp)
        ) {
            // Animated Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress.value.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(12.dp))
                    .drawBehind {
                        // Shiny highlights on green progress bar
                        drawRect(CocButtonGreen)
                        drawRect(
                            color = Color.White.copy(alpha = 0.35f),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.4f)
                        )
                    }
                    .border(1.5.dp, CocButtonGreenDark, RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun ClashSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "clash_switch"
) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 24f else 0f,
        label = "switch_thumb"
    )
    val trackColor = if (checked) CocButtonGreen else CocInputBg
    val trackBorderColor = if (checked) CocButtonGreenDark else CocBorder

    Box(
        modifier = modifier
            .testTag(testTag)
            .width(60.dp)
            .height(34.dp)
            .border(2.5.dp, trackBorderColor, RoundedCornerShape(17.dp))
            .background(trackColor, RoundedCornerShape(17.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset.dp)
                .size(22.dp)
                .drawBehind {
                    drawRoundRect(
                        color = CocBorder,
                        topLeft = Offset(0f, 3f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(11.dp.toPx(), 11.dp.toPx())
                    )
                    drawRoundRect(
                        color = CocGoldDark,
                        topLeft = Offset(0f, 1.5f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(11.dp.toPx(), 11.dp.toPx())
                    )
                    drawRoundRect(
                        color = CocGold,
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width, size.height - 1.5f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(11.dp.toPx(), 11.dp.toPx())
                    )
                }
                .border(1.5.dp, CocBorder, RoundedCornerShape(11.dp))
        )
    }
}
