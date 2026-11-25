package com.example.dialerclone.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dialerclone.R

private data class DialKey(val digit: String, val letters: String, val col: Int, val row: Int)

private val KEYS = listOf(
    DialKey("1", "∞", 0, 0), DialKey("2", "ABC", 1, 0), DialKey("3", "DEF", 2, 0),
    DialKey("4", "GHI", 0, 1), DialKey("5", "JKL", 1, 1), DialKey("6", "MNO", 2, 1),
    DialKey("7", "PQRS", 0, 2), DialKey("8", "TUV", 1, 2), DialKey("9", "WXYZ", 2, 2),
    DialKey("*", ",", 0, 3), DialKey("0", "+", 1, 3), DialKey("#", "", 2, 3)
)

private val COL_X = listOf(224f, 540f, 856f)
private val DIGIT_Y = listOf(1302f, 1471f, 1640f, 1809f)
private val LETTER_Y = listOf(1358f, 1527f, 1696f, 1867f)

private data class KeyAsset(
    val res: Int, val x: Float, val y: Float, val w: Float, val h: Float
)

private val KEY_ASSETS = mapOf(
    "1" to KeyAsset(R.drawable.key_1, 213f, 1272f, 22f, 63f),
    "2" to KeyAsset(R.drawable.key_2, 519f, 1271f, 43f, 64f),
    "3" to KeyAsset(R.drawable.key_3, 836f, 1271f, 42f, 64f),
    "4" to KeyAsset(R.drawable.key_4, 200f, 1441f, 48f, 63f),
    "5" to KeyAsset(R.drawable.key_5, 519f, 1441f, 44f, 63f),
    "6" to KeyAsset(R.drawable.key_6, 834f, 1440f, 44f, 64f),
    "7" to KeyAsset(R.drawable.key_7, 205f, 1610f, 41f, 63f),
    "8" to KeyAsset(R.drawable.key_8, 519f, 1609f, 43f, 64f),
    "9" to KeyAsset(R.drawable.key_9, 835f, 1609f, 43f, 65f),
    "0" to KeyAsset(R.drawable.key_0, 518f, 1778f, 45f, 64f),
    "*" to KeyAsset(R.drawable.key_star, 205f, 1789f, 39f, 42f),
    "#" to KeyAsset(R.drawable.key_hash, 839f, 1786f, 35f, 42f)
)

private val LETTER_ASSETS = mapOf(
    "1" to KeyAsset(R.drawable.letter_1, 203f, 1351f, 41f, 18f),
    "2" to KeyAsset(R.drawable.letter_2, 513f, 1348f, 53f, 22f),
    "3" to KeyAsset(R.drawable.letter_3, 832f, 1348f, 48f, 21f),
    "4" to KeyAsset(R.drawable.letter_4, 202f, 1517f, 43f, 22f),
    "5" to KeyAsset(R.drawable.letter_5, 518f, 1517f, 43f, 22f),
    "6" to KeyAsset(R.drawable.letter_6, 826f, 1517f, 60f, 22f),
    "7" to KeyAsset(R.drawable.letter_7, 190f, 1686f, 68f, 22f),
    "8" to KeyAsset(R.drawable.letter_8, 514f, 1686f, 53f, 22f),
    "9" to KeyAsset(R.drawable.letter_9, 817f, 1686f, 77f, 21f),
    "0" to KeyAsset(R.drawable.letter_0, 532f, 1865f, 16f, 16f)
)

@Composable
fun DialerHome(
    number: String,
    searching: Boolean,
    onSearchingChange: (Boolean) -> Unit,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onBack: () -> Unit,
    onCall: () -> Unit,
    onShowDeviceInfo: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val inputMode = number.isNotEmpty() || searching
    // 0 = idle dialpad, 1 = number-input state. Drives the upward expansion
    // of the dialpad card and the fade/slide-in of the number row, as in the video.
    val inputProgress by animateFloatAsState(
        targetValue = if (inputMode) 1f else 0f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "inputProgress"
    )
    var hideReference by remember { mutableStateOf(false) }
    var showReference by remember { mutableStateOf(true) }
    val referenceVisible = SHOW_REFERENCE_BACKGROUND && showReference && !hideReference

    Box(Modifier.fillMaxSize()) {
        if (referenceVisible) {
            // Fixed idle screenshot: never swapped during state changes, so the
            // background cannot flash or ghost while the input row expands.
            Image(
                painter = painterResource(R.drawable.ref_dialer_idle),
                contentDescription = null,
                modifier = Modifier.size(Ref.WIDTH.sdp, Ref.HEIGHT.sdp),
                contentScale = ContentScale.FillBounds
            )
        }

        // Reference-image toggle (debug aid).
        Box(
            Modifier
                .offset(x = 40.sdp, y = 100.sdp)
                .size(110.sdp, 50.sdp)
                .clickable { showReference = !showReference }
        ) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                val trackColor = if (showReference) Color(0xFF6750A4) else Color(0xFFBDBDBD)
                drawRoundRect(
                    color = trackColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(25f, 25f),
                    size = androidx.compose.ui.geometry.Size(110f, 50f)
                )
                val thumbX = if (showReference) 82f else 28f
                drawCircle(Color.White, 20f, androidx.compose.ui.geometry.Offset(thumbX, 25f))
            }
        }

        // When the fixed idle reference is shown, hide the two regions that
        // differ in the input state while keeping the stable lower half intact.
        if (referenceVisible && inputMode) {
            Box(
                Modifier
                    .offset(x = 0.sdp, y = 280.sdp)
                    .size(Ref.WIDTH.sdp, 140.sdp)
                    .background(Color.White)
            )
            Box(
                Modifier
                    .offset(x = 0.sdp, y = 940.sdp)
                    .size(Ref.WIDTH.sdp, 340.sdp)
                    .background(Color.White)
            )
        }

        // Header
        if (inputMode) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val stem = with(density) { 4.sdp.toPx() }
            val top = with(density) { androidx.compose.ui.geometry.Offset(42.sdp.toPx(), 16.sdp.toPx()) }
            val mid = with(density) { androidx.compose.ui.geometry.Offset(16.sdp.toPx(), 41.sdp.toPx()) }
            val bottom = with(density) { androidx.compose.ui.geometry.Offset(42.sdp.toPx(), 68.sdp.toPx()) }
            Box(
                Modifier
                    .offset(x = 60.sdp, y = 155.sdp)
                    .size(64.sdp, 88.sdp)
                    .clickable {
                        if (searching) onSearchingChange(false) else onBack()
                    }
            ) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    drawLine(Color.Black, top, mid, strokeWidth = stem, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(Color.Black, mid, bottom, strokeWidth = stem, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
            }
        } else {
            Text(
                "通话",
                Modifier.offset(x = 494.sdp, y = 169.sdp).size(92.sdp, 64.sdp),
                fontSize = 50.ssp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            val menuDensity = androidx.compose.ui.platform.LocalDensity.current
            val menuDotRadius = with(menuDensity) { 4.sdp.toPx() }
            val menuDots = with(menuDensity) {
                listOf(
                    androidx.compose.ui.geometry.Offset(26.sdp.toPx(), 16.sdp.toPx()),
                    androidx.compose.ui.geometry.Offset(26.sdp.toPx(), 37.sdp.toPx()),
                    androidx.compose.ui.geometry.Offset(26.sdp.toPx(), 58.sdp.toPx())
                )
            }
            Box(
                Modifier
                    .offset(x = 960.sdp, y = 160.sdp)
                    .size(52.sdp, 70.sdp)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            hideReference = true
                            val up = waitForUpOrCancellation()
                            hideReference = false
                            if (up != null) onShowDeviceInfo()
                        }
                    }
            ) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    menuDots.forEach { drawCircle(Color.Black, menuDotRadius, it) }
                }
            }
        }
        if (inputMode) {
            Text(
                "通话",
                Modifier.offset(x = 494.sdp, y = 169.sdp).size(92.sdp, 64.sdp),
                fontSize = 50.ssp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        // Search bar / action shortcuts switch directly; only the number row expands.
        when {
            searching -> SearchField()
            number.isNotEmpty() -> ActionShortcuts {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
            else -> SearchBar(onClick = { onSearchingChange(true) })
        }

        DialpadCard(number, inputProgress, onDigit, onBackspace, onCall)

        // Empty call history state — drawn after the card so the text is not
        // covered where it slightly overlaps the card's top edge (as in the reference).
        if (!inputMode) {
            Image(
                painter = painterResource(R.drawable.empty_phone),
                contentDescription = null,
                modifier = Modifier.offset(x = 470.sdp, y = 966.sdp).size(140.sdp, 138.sdp)
            )
            Text(
                "没有通话记录",
                Modifier.offset(x = 290.sdp, y = 1157.sdp).size(500.sdp, 100.sdp),
                fontSize = 48.ssp,
                color = DialerColors.EmptyText,
                textAlign = TextAlign.Center
            )
        }

        BottomNav(selectedTab, onTabSelected)
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Box(
        Modifier
            .offset(x = 33.sdp, y = 286.sdp)
            .size(1014.sdp, 120.sdp)
            .background(DialerColors.SearchBg, RoundedCornerShape(60.sdp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(R.drawable.search_icon),
            contentDescription = null,
            modifier = Modifier.offset(x = 39.sdp, y = 32.sdp).size(40.sdp, 44.sdp)
        )
        Image(
            painter = painterResource(R.drawable.search_placeholder),
            contentDescription = null,
            modifier = Modifier.offset(x = 121.sdp, y = 40.sdp).size(208.sdp, 41.sdp)
        )
    }
}

@Composable
private fun SearchField() {
    var query by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    Box(
        Modifier
            .offset(x = 33.sdp, y = 286.sdp)
            .size(1014.sdp, 120.sdp)
            .background(DialerColors.SearchBg, RoundedCornerShape(60.sdp))
    ) {
        Image(
            painter = painterResource(R.drawable.search_icon),
            contentDescription = null,
            modifier = Modifier.offset(x = 39.sdp, y = 32.sdp).size(40.sdp, 44.sdp)
        )
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 36.ssp, color = Color.Black),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("搜索联系人", fontSize = 36.ssp, color = DialerColors.Placeholder)
                }
                inner()
            },
            modifier = Modifier.offset(x = 124.sdp, y = 32.sdp).size(800.sdp, 56.sdp)
        )
    }
}

@Composable
private fun ActionShortcuts(onAction: (String) -> Unit) {
    val labels = listOf("新建联系人", "添加到联系人", "发起视频通话", "发送信息")
    val ys = listOf(333f, 479f, 625f, 771f)
    labels.forEachIndexed { index, label ->
        Text(
            label,
            Modifier
                .offset(x = 79.sdp, y = ys[index].sdp)
                .clickable { onAction(label) },
            fontSize = 47.ssp,
            color = DialerColors.Blue
        )
    }
}

@Composable
private fun DialpadCard(
    number: String,
    progress: Float,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onCall: () -> Unit
) {
    val cardTop = 1214f + (1040f - 1214f) * progress
    val cardHeight = 2150f - cardTop

    Box(
        Modifier
            .offset(x = 65.sdp, y = cardTop.sdp)
            .size(950.sdp, cardHeight.sdp)
            .shadow(
                elevation = 48.sdp,
                shape = RoundedCornerShape(80.sdp),
                ambientColor = Color(0xFF000000),
                spotColor = Color(0x55000000)
            )
            .background(Color.White, RoundedCornerShape(80.sdp))
            .border(1.sdp, Color(0xFFF0F0F0), RoundedCornerShape(80.sdp))
    )

    val hasNumber = number.isNotEmpty()
    // The result is already visible as soon as a key is pressed; the animation
    // only expands the row's height. On collapse it moves down and fades away.
    val trailingAlpha = if (hasNumber) 1f else progress
    val trailingDrop = if (hasNumber) 0f else 36f * (1f - progress)
    if (hasNumber || progress > 0.001f) {
        Box(Modifier.graphicsLayer { alpha = trailingAlpha }) {
            Text(
                number,
                Modifier.offset(x = 350.sdp, y = (1080f + trailingDrop).sdp).size(380.sdp, 100.sdp),
                fontSize = 76.ssp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF24C93F),
                textAlign = TextAlign.Center
            )
            Icon(
                Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "删除",
                tint = DialerColors.Backspace,
                modifier = Modifier
                    .offset(x = 884.sdp, y = (1096f + trailingDrop).sdp)
                    .size(62.sdp)
                    .clickable(
                        enabled = hasNumber,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBackspace
                    )
            )
        }
    }

    KEYS.forEach { key ->
        val x = COL_X[key.col]
        val y = DIGIT_Y[key.row]

        Box(
            Modifier
                .offset(x = (x - 110).sdp, y = (y - 70).sdp)
                .size(220.sdp, 150.sdp)
                .referenceClickable(circleSize = 130.sdp) { onDigit(key.digit) }
        )

        val asset = KEY_ASSETS.getValue(key.digit)
        Image(
            painter = painterResource(asset.res),
            contentDescription = null,
            modifier = Modifier
                .offset(x = asset.x.sdp, y = asset.y.sdp)
                .size(asset.w.sdp, asset.h.sdp)
        )
        LETTER_ASSETS[key.digit]?.let { letter ->
            Image(
                painter = painterResource(letter.res),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = letter.x.sdp, y = letter.y.sdp)
                    .size(letter.w.sdp, letter.h.sdp)
            )
        }
    }

    Box(
        Modifier
            .offset(x = 456.sdp, y = 1936.sdp)
            .size(166.sdp)
            .shadow(elevation = 8.sdp, shape = CircleShape, spotColor = DialerColors.CardShadow)
            .background(DialerColors.Green, CircleShape)
            .clickable { onCall() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Call,
            contentDescription = "拨打",
            tint = Color.White,
            modifier = Modifier.size(78.sdp)
        )
    }
}

@Composable
fun BottomNav(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        Modifier
            .offset(x = 108.sdp, y = 2182.sdp)
            .size(684.sdp, 152.sdp)
            .shadow(
                elevation = 10.sdp,
                shape = RoundedCornerShape(68.sdp),
                ambientColor = DialerColors.CardShadow,
                spotColor = DialerColors.CardShadow
            )
            .background(Color.White, RoundedCornerShape(76.sdp))
    ) {
        Box(
            Modifier
                .offset(x = 4.sdp, y = 8.sdp)
                .size(244.sdp, 136.sdp)
                .background(DialerColors.NavPill, RoundedCornerShape(68.sdp))
        )

        val labels = listOf("通话", "联系人", "营业厅")
        val iconPainters = listOf(
            painterResource(R.drawable.nav_phone),
            painterResource(R.drawable.nav_person),
            painterResource(R.drawable.nav_shop)
        )
        val iconX = listOf(24f, 23f, 27f)
        val iconY = listOf(29f, 28f, 30f)
        val iconW = listOf(53f, 55f, 47f)
        val iconH = listOf(53f, 54f, 51f)
        val labelPainters = listOf(
            painterResource(R.drawable.nav_label_call),
            painterResource(R.drawable.nav_label_contacts),
            painterResource(R.drawable.nav_label_shop)
        )
        val labelX = listOf(23f, 9f, 8f)
        val labelY = listOf(103f, 103f, 102f)
        val labelW = listOf(56f, 86f, 86f)
        val labelH = listOf(26f, 26f, 27f)
        val centers = listOf(124f, 337f, 552f)

        labels.indices.forEach { index ->
            Box(
                Modifier
                    .offset(x = (centers[index] - 50).sdp, y = 0.sdp)
                    .size(100.sdp, 152.sdp)
                    .referenceClickable(circleSize = 120.sdp) { onTabSelected(index) }
            ) {
                Image(
                    painter = iconPainters[index],
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = iconX[index].sdp, y = iconY[index].sdp)
                        .size(iconW[index].sdp, iconH[index].sdp)
                )
                Image(
                    painter = labelPainters[index],
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = labelX[index].sdp, y = labelY[index].sdp)
                        .size(labelW[index].sdp, labelH[index].sdp)
                )
            }
        }
    }

    // Grid button on the right of the bottom navigation bar.
    Box(
        Modifier
            .offset(x = 824.sdp, y = 2182.sdp)
            .size(160.sdp, 152.sdp)
            .border(1.sdp, Color(0xFFEEEEEE), RoundedCornerShape(76.sdp))
            .background(Color.White, RoundedCornerShape(76.sdp))
            .referenceClickable(circleSize = 100.sdp) { /* dialpad is already visible */ }
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val dotRadius = with(density) { 5.sdp.toPx() }
        val dotCenters = (0..2).flatMap { row ->
            (0..2).map { col ->
                with(density) {
                    androidx.compose.ui.geometry.Offset(
                        (10 + col * 20).sdp.toPx(),
                        (10 + row * 20).sdp.toPx()
                    )
                }
            }
        }
        androidx.compose.foundation.Canvas(
            Modifier.offset(x = 45.sdp, y = 46.sdp).size(60.sdp, 60.sdp)
        ) {
            dotCenters.forEach { center ->
                drawCircle(color = Color.Black, radius = dotRadius, center = center)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text(
            title,
            Modifier.offset(x = 400.sdp, y = 170.sdp).size(280.sdp, 60.sdp),
            fontSize = 50.ssp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            "暂无数据",
            Modifier.offset(x = 390.sdp, y = 1150.sdp).size(300.sdp, 60.sdp),
            fontSize = 38.ssp,
            color = DialerColors.EmptyText,
            textAlign = TextAlign.Center
        )
        BottomNav(selectedTab, onTabSelected)
    }
}
