package com.example.dialerclone.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Reference pixels converted to density-independent pixels on this device. */
val Int.sdp: Dp @Composable get() = (this * LocalScale.current).dp
val Float.sdp: Dp @Composable get() = (this * LocalScale.current).dp
val Int.ssp: TextUnit @Composable get() = (this * LocalScale.current).sp
val Float.ssp: TextUnit @Composable get() = (this * LocalScale.current).sp

/**
 * MIUI-style press feedback: a soft gray circle fades in behind the control
 * instead of Material's rectangular ripple.
 */
@Composable
fun Modifier.referenceClickable(
    circleSize: Dp = Dp.Unspecified,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val circle = if (circleSize == Dp.Unspecified) 150.sdp else circleSize
    val color by animateColorAsState(
        targetValue = if (pressed) Color(0xFFFFFFFF) else Color(0x00000000),
        animationSpec = tween(120),
        label = "press"
    )
    return this
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        .drawBehind {
            val a = color.alpha
            if (a > 0.01f) {
                drawCircle(
                    color = Color(0xFFBBBBBB).copy(alpha = a * 0.6f),
                    radius = circle.toPx() / 2f,
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            }
        }
}

/**
 * All coordinates in this project are expressed in the reference canvas of the
 * supplied screenshots: 1080 x 2400 px. The whole canvas is uniformly scaled to
 * fit the physical screen, which keeps the layout 1:1 at any resolution
 * (including 1440 x 3200 and 3200 x 1440 panels).
 */
object Ref {
    const val WIDTH = 1080f
    const val HEIGHT = 2400f
}

/** Set to false to hide the reference overlays and ship a clean UI. */
const val SHOW_REFERENCE_BACKGROUND = true

/** Uniform scale factor from the 1080x2400 reference canvas to the device. */
val LocalScale = compositionLocalOf { 1f }

object DialerColors {
    val Green = Color(0xFF1DCD3A)
    val Blue = Color(0xFF3281FF)
    val SearchBg = Color(0xFFF0F0F0)
    val Placeholder = Color(0xFFA8A8A8)
    val EmptyText = Color(0xFF666666)
    val EmptyIcon = Color(0xFFD8D8D8)
    val Letter = Color(0xFFBCBCBC)
    val NavPill = Color(0xFFEEEEEE)
    val ConfirmBg = Color(0xFFF0F0F0)
    val Backspace = Color(0xFFB6B6B6)
    val DialogScrim = Color(0x33000000)
    val CardShadow = Color(0x2A000000)
}
