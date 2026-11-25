package com.example.dialerclone.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SN_BARS = arrayOf(
    162 to 8, 174 to 4, 186 to 12, 206 to 8, 222 to 4, 242 to 4, 250 to 8, 262 to 8,
    274 to 16, 294 to 4, 302 to 16, 322 to 12, 338 to 4, 350 to 12, 370 to 8, 382 to 4,
    390 to 12, 410 to 8, 426 to 4, 442 to 8, 462 to 4, 470 to 8, 482 to 12, 502 to 4,
    514 to 4, 522 to 12, 546 to 8, 558 to 4, 566 to 12, 582 to 8, 602 to 4, 610 to 12,
    626 to 16, 646 to 8, 658 to 8, 674 to 8, 690 to 4, 702 to 12, 718 to 8, 734 to 4,
    742 to 16, 762 to 12, 778 to 12, 794 to 4, 806 to 8, 822 to 4, 830 to 12, 850 to 8,
    866 to 8, 886 to 12, 902 to 4, 910 to 8
)

private val IMEI1_BARS = arrayOf(
    205 to 10, 220 to 5, 235 to 15, 260 to 20, 285 to 5, 300 to 5, 315 to 15, 345 to 5,
    355 to 10, 370 to 5, 395 to 5, 405 to 10, 425 to 5, 440 to 10, 465 to 5, 480 to 5,
    490 to 20, 520 to 5, 535 to 5, 545 to 20, 575 to 5, 590 to 15, 610 to 10, 635 to 5,
    645 to 5, 655 to 20, 680 to 15, 700 to 10, 715 to 15, 740 to 5, 755 to 15, 785 to 10,
    800 to 5, 810 to 10, 835 to 15, 855 to 5, 865 to 10
)

private val IMEI2_BARS = arrayOf(
    205 to 10, 220 to 5, 235 to 15, 260 to 20, 285 to 5, 300 to 5, 315 to 15, 345 to 5,
    355 to 10, 370 to 5, 395 to 5, 405 to 10, 425 to 5, 440 to 10, 465 to 5, 480 to 5,
    490 to 20, 520 to 5, 535 to 5, 545 to 20, 575 to 5, 590 to 15, 620 to 10, 635 to 5,
    645 to 5, 655 to 20, 680 to 15, 700 to 10, 720 to 5, 730 to 15, 755 to 15, 775 to 15,
    795 to 10, 810 to 10, 835 to 15, 855 to 5, 865 to 10
)

@Composable
fun DeviceInfoDialog(onDismiss: () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
    }
    val slideY = 961f + (Ref.HEIGHT - 961f) * (1f - progress.value)
    Box(
        Modifier
            .size(Ref.WIDTH.sdp, Ref.HEIGHT.sdp)
            .background(DialerColors.DialogScrim.copy(alpha = 0.2f * progress.value))
            .clickable { onDismiss() }
    ) {
        Box(
            Modifier
                .offset(x = 32.sdp, y = slideY.sdp)
                .size(1016.sdp, 1360.sdp)
                .background(Color.White, RoundedCornerShape(96.sdp))
                .clickable { /* consume clicks inside the dialog */ }
        ) {
            Text(
                "移动设备识别码",
                Modifier.offset(x = 168.sdp, y = 1029.sdp - 961.sdp).width(680.sdp),
                fontSize = 56.ssp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                "SN: 61891/F5NM00168",
                Modifier.offset(x = 107.sdp, y = 1184.sdp - 961.sdp).width(800.sdp),
                fontSize = 53.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Barcode(SN_BARS, 1248f - 961f, 164f, -32f)

            Text(
                "IMEI1: 865667078383585",
                Modifier.offset(x = 87.sdp, y = 1503.sdp - 961.sdp).width(840.sdp),
                fontSize = 53.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Barcode(IMEI1_BARS, 1567f - 961f, 164f, -32f)

            Text(
                "IMEI2: 865667078383593",
                Modifier.offset(x = 88.sdp, y = 1822.sdp - 961.sdp).width(840.sdp),
                fontSize = 53.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Barcode(IMEI2_BARS, 1886f - 961f, 164f, -32f)

            Box(
                Modifier
                    .offset(x = 67.sdp, y = 2118.sdp - 961.sdp)
                    .size(880.sdp, 137.sdp)
                    .background(DialerColors.ConfirmBg, RoundedCornerShape(69.sdp))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "确定",
                    fontSize = 50.ssp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
