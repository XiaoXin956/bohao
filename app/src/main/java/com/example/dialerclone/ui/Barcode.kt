package com.example.dialerclone.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Draws the exact bar pattern sampled from the reference screenshot.
 * Each entry is (x position in the 1080px reference canvas, bar width).
 */
@Composable
fun Barcode(bars: Array<Pair<Int, Int>>, y: Float, height: Float, x: Float = 0f) {
    Canvas(
        Modifier
            .offset(x = x.sdp, y = y.sdp)
            .size(Ref.WIDTH.sdp, height.sdp)
    ) {
        bars.forEach { (x, width) ->
            drawRect(
                color = Color.Black,
                topLeft = Offset(x.toFloat(), 0f),
                size = Size(width.toFloat(), height)
            )
        }
    }
}
