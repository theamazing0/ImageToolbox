package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.draw.DrawMode
import ru.tech.imageresizershrinker.presentation.root.icons.material.Cube
import ru.tech.imageresizershrinker.presentation.root.icons.material.Highlighter
import ru.tech.imageresizershrinker.presentation.root.icons.material.Laser
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.resize_group.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawModeSelector(
    modifier: Modifier,
    value: DrawMode,
    onValueChange: (DrawMode) -> Unit
) {
    val state = rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.draw_mode),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable {
                        state.value = true
                    }
                    .padding(1.dp)
                    .size(
                        with(LocalDensity.current) {
                            LocalTextStyle.current.fontSize.toDp()
                        }
                    )
            )
        }
        Box {
            SingleChoiceSegmentedButtonRow(
                space = max(settingsState.borderWidth, 1.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp, top = 8.dp)
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false
                ) {
                    DrawMode.entries.forEachIndexed { index, item ->
                        val selected by remember(value, item) {
                            derivedStateOf {
                                value::class.isInstance(item)
                            }
                        }
                        val shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = DrawMode.entries.size
                        )
                        SegmentedButton(
                            onClick = { onValueChange(item) },
                            selected = selected,
                            icon = {},
                            border = BorderStroke(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant()
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    6.dp
                                )
                            ),
                            modifier = Modifier.materialShadow(
                                shape = shape,
                                elevation = animateDpAsState(
                                    if (settingsState.borderWidth >= 0.dp || !settingsState.drawButtonShadows) 0.dp
                                    else if (selected) 2.dp
                                    else 1.dp
                                ).value
                            ),
                            shape = shape
                        ) {
                            Icon(
                                imageVector = item.getIcon(),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to MaterialTheme.colorScheme.surfaceContainer,
                            1f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
            )
        }
        AnimatedVisibility(
            visible = value is DrawMode.PathEffect.PrivacyBlur,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BlurRadiusSelector(
                modifier = Modifier.padding(8.dp),
                value = (value as? DrawMode.PathEffect.PrivacyBlur)?.blurRadius ?: 0,
                valueRange = 5f..50f,
                onValueChange = {
                    onValueChange(DrawMode.PathEffect.PrivacyBlur(it))
                }
            )
        }
        AnimatedVisibility(
            visible = value is DrawMode.PathEffect.Pixelation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            PixelSizeSelector(
                modifier = Modifier.padding(8.dp),
                value = (value as? DrawMode.PathEffect.Pixelation)?.pixelSize ?: 0f,
                onValueChange = {
                    onValueChange(DrawMode.PathEffect.Pixelation(it))
                }
            )
        }
    }
    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawMode.entries.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
                                    index,
                                    DrawMode.entries.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = stringResource(item.getTitle()), icon = item.getIcon())
                        Text(
                            text = stringResource(item.getSubtitle()),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = state,
        title = {
            TitleItem(text = stringResource(R.string.draw_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { state.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private fun DrawMode.getSubtitle(): Int = when (this) {
    is DrawMode.Highlighter -> R.string.highlighter_sub
    is DrawMode.Neon -> R.string.neon_sub
    is DrawMode.Pen -> R.string.pen_sub
    is DrawMode.PathEffect.PrivacyBlur -> R.string.privacy_blur_sub
    is DrawMode.PathEffect.Pixelation -> R.string.pixelation_sub
}

private fun DrawMode.getTitle(): Int = when (this) {
    is DrawMode.Highlighter -> R.string.highlighter
    is DrawMode.Neon -> R.string.neon
    is DrawMode.Pen -> R.string.pen
    is DrawMode.PathEffect.PrivacyBlur -> R.string.privacy_blur
    is DrawMode.PathEffect.Pixelation -> R.string.pixelation
}

private fun DrawMode.getIcon(): ImageVector = when (this) {
    is DrawMode.Highlighter -> Icons.Rounded.Highlighter
    is DrawMode.Neon -> Icons.Rounded.Laser
    is DrawMode.Pen -> Icons.Rounded.Brush
    is DrawMode.PathEffect.PrivacyBlur -> Icons.Rounded.BlurCircular
    is DrawMode.PathEffect.Pixelation -> Icons.Rounded.Cube
}