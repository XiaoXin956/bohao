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
    162 to 8, 174 to 4, 186 to 12, 206 to 8, 222 to 4, 242 to 4, 250 to 16,
    270 to 8, 282 to 8, 294 to 4, 302 to 16, 322 to 12, 338 to 8, 354 to 4,
    362 to 12, 382 to 4, 390 to 12, 410 to 8, 426 to 4, 442 to 8, 462 to 4,
    470 to 8, 482 to 12, 502 to 4, 514 to 8, 534 to 4, 542 to 12, 558 to 4,
    566 to 8, 586 to 12, 602 to 4, 610 to 12, 626 to 16, 646 to 8, 662 to 8,
    674 to 8, 690 to 4, 702 to 4, 714 to 8, 734 to 4, 742 to 16, 762 to 12,
    778 to 12, 798 to 4, 806 to 8, 822 to 4, 834 to 8, 846 to 12, 866 to 8,
    886 to 12, 902 to 4, 910 to 8
)

private val IMEI1_BARS = arrayOf(
    205 to 10, 220 to 5, 235 to 15, 260 to 20, 285 to 5, 300 to 5, 315 to 5,
    330 to 10, 360 to 5, 370 to 10, 385 to 10, 405 to 10, 425 to 5, 440 to 10,
    465 to 5, 480 to 5, 495 to 5, 505 to 10, 535 to 10, 560 to 10, 575 to 10,
    590 to 5, 605 to 5, 630 to 10, 645 to 5, 655 to 20, 680 to 15, 700 to 5,
    715 to 15, 735 to 10, 755 to 10, 775 to 5, 790 to 15, 810 to 10, 835 to 15,
    855 to 5, 865 to 10
)

private val IMEI2_BARS = arrayOf(
    205 to 10, 220 to 5, 235 to 15, 260 to 20, 285 to 5, 300 to 5, 315 to 5,
    330 to 10, 360 to 5, 370 to 10, 385 to 10, 405 to 10, 425 to 5, 440 to 10,
    465 to 5, 480 to 5, 495 to 5, 505 to 10, 535 to 10, 560 to 10, 575 to 10,
    590 to 5, 615 to 5, 625 to 10, 645 to 5, 655 to 20, 680 to 15, 700 to 15,
    720 to 5, 735 to 10, 755 to 5, 765 to 15, 785 to 20, 810 to 10, 835 to 15,
    855 to 5, 865 to 10
)

@Composable
fun DeviceInfoDialog(onDismiss: () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
    }
    val slideY = 925f + (Ref.HEIGHT - 925f) * (1f - progress.value)
    Box(
        Modifier
            .size(Ref.WIDTH.sdp, Ref.HEIGHT.sdp)
            .background(DialerColors.DialogScrim.copy(alpha = 0.2f * progress.value))
            .clickable { onDismiss() }
    ) {
        Box(
            Modifier
                .offset(x = 32.sdp, y = slideY.sdp)
                .size(1016.sdp, 1325.sdp)
                .background(Color.White, RoundedCornerShape(80.sdp))
                .clickable { /* consume clicks inside the dialog */ }
        ) {
            Text(
                "移动设备识别码",
                Modifier.offset(x = 208.sdp, y = 1020.sdp - 925.sdp).width(600.sdp),
                fontSize = 40.ssp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                "SN: 61913/F5RK01039",
                Modifier.offset(x = 118.sdp, y = 1176.sdp - 925.sdp).width(780.sdp),
                fontSize = 36.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Barcode(SN_BARS, 1237f - 925f, 164f, -32f)

            Text(
                "IMEI1: 867200076532660",
                Modifier.offset(x = 98.sdp, y = 1495.sdp - 925.sdp).width(820.sdp),
                fontSize = 36.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Barcode(IMEI1_BARS, 1556f - 925f, 164f, -32f)

            Text(
                "IMEI2: 867200076532678",
                Modifier.offset(x = 98.sdp, y = 1814.sdp - 925.sdp).width(820.sdp),
                fontSize = 36.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Barcode(IMEI2_BARS, 1875f - 925f, 164f, -32f)

            Box(
                Modifier
                    .offset(x = 72.sdp, y = 2070.sdp - 925.sdp)
                    .size(872.sdp, 100.sdp)
                    .background(DialerColors.ConfirmBg, RoundedCornerShape(60.sdp))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "确定",
                    fontSize = 38.ssp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
