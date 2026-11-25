package com.example.dialerclone.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    Box(Modifier.fillMaxSize()) {
        if (SHOW_REFERENCE_BACKGROUND) {
            Image(
                painter = painterResource(
                    if (inputMode) R.drawable.ref_dialer_input else R.drawable.ref_dialer_idle
                ),
                contentDescription = null,
                modifier = Modifier.size(Ref.WIDTH.sdp, Ref.HEIGHT.sdp),
                contentScale = ContentScale.FillBounds
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
                    .referenceClickable(circleSize = 60.sdp) { onShowDeviceInfo() }
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

        // Search bar / action shortcuts
        when {
            searching -> SearchField()
            number.isNotEmpty() -> ActionShortcuts {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
            else -> SearchBar(onClick = { onSearchingChange(true) })
        }

        DialpadCard(number, onDigit, onBackspace, onCall)

        // Empty call history state — drawn after the card so the text is not
        // covered where it slightly overlaps the card's top edge (as in the reference).
        if (!inputMode) {
            Icon(
                Icons.Filled.Call,
                contentDescription = null,
                tint = DialerColors.EmptyIcon,
                modifier = Modifier.offset(x = 455.sdp, y = 950.sdp).size(170.sdp)
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
        Icon(
            Icons.Filled.Search,
            contentDescription = "搜索",
            tint = DialerColors.Placeholder,
            modifier = Modifier.offset(x = 41.sdp, y = 36.sdp).size(48.sdp)
        )
        Text(
            "搜索联系人",
            Modifier.offset(x = 124.sdp, y = 35.sdp),
            fontSize = 48.ssp,
            color = DialerColors.Placeholder
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
        Icon(
            Icons.Filled.Search,
            contentDescription = "搜索",
            tint = DialerColors.Placeholder,
            modifier = Modifier.offset(x = 41.sdp, y = 36.sdp).size(48.sdp)
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
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onCall: () -> Unit
) {
    val cardTop = if (number.isNotEmpty()) 1002f else 1185f
    val cardHeight = 2110f - cardTop

    Box(
        Modifier
            .offset(x = 65.sdp, y = cardTop.sdp)
            .size(950.sdp, cardHeight.sdp)
            .shadow(
                elevation = 16.sdp,
                shape = RoundedCornerShape(80.sdp),
                ambientColor = DialerColors.CardShadow,
                spotColor = DialerColors.CardShadow
            )
            .background(Color.White, RoundedCornerShape(80.sdp))
            .border(1.sdp, Color(0xFFF0F0F0), RoundedCornerShape(80.sdp))
    )

    if (number.isNotEmpty()) {
        Text(
            number,
            Modifier.offset(x = 350.sdp, y = 1078.sdp).size(380.sdp, 100.sdp),
            fontSize = 76.ssp,
            color = DialerColors.Green,
            textAlign = TextAlign.Center
        )
        Icon(
            Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "删除",
            tint = DialerColors.Backspace,
            modifier = Modifier
                .offset(x = 884.sdp, y = 1096.sdp)
                .size(62.sdp)
                .clickable { onBackspace() }
        )
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

        Text(
            key.digit,
            Modifier.offset(x = (x - 50).sdp, y = (y - 53).sdp).size(100.sdp, 120.sdp),
            fontSize = 92.ssp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        if (key.letters.isNotEmpty()) {
            Text(
                key.letters,
                Modifier.offset(x = (x - 70).sdp, y = (LETTER_Y[key.row] - 15).sdp).size(140.sdp, 40.sdp),
                fontSize = if (key.digit == "1") 42.ssp else 27.ssp,
                color = DialerColors.Letter,
                textAlign = TextAlign.Center
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
            .offset(x = 108.sdp, y = 2175.sdp)
            .size(684.sdp, 180.sdp)
            .shadow(
                elevation = 10.sdp,
                shape = RoundedCornerShape(68.sdp),
                ambientColor = DialerColors.CardShadow,
                spotColor = DialerColors.CardShadow
            )
            .background(Color.White, RoundedCornerShape(68.sdp))
    ) {
        Box(
            Modifier
                .offset(x = 4.sdp, y = 4.sdp)
                .size(244.sdp, 172.sdp)
                .background(DialerColors.NavPill, RoundedCornerShape(64.sdp))
        )

        val labels = listOf("通话", "联系人", "营业厅")
        val icons = listOf(Icons.Filled.Call, Icons.Filled.AccountCircle, Icons.Filled.ShoppingBag)
        val centers = listOf(124f, 337f, 552f)

        labels.forEachIndexed { index, label ->
            Box(
                Modifier
                    .offset(x = (centers[index] - 50).sdp, y = 0.sdp)
                    .size(100.sdp, 180.sdp)
                    .referenceClickable(circleSize = 120.sdp) { onTabSelected(index) }
            ) {
                Icon(
                    icons[index],
                    contentDescription = label,
                    tint = if (selectedTab == index) Color.Black else Color(0xFF333333),
                    modifier = Modifier.offset(x = 15.sdp, y = 27.sdp).size(70.sdp)
                )
                Text(
                    label,
                    Modifier.offset(x = 0.sdp, y = 103.sdp).width(100.sdp),
                    fontSize = 32.ssp,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Grid button on the right of the bottom navigation bar.
    Box(
        Modifier
            .offset(x = 824.sdp, y = 2175.sdp)
            .size(160.sdp, 180.sdp)
            .border(1.sdp, Color(0xFFEEEEEE), RoundedCornerShape(90.sdp))
            .background(Color.White, RoundedCornerShape(90.sdp))
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
            Modifier.offset(x = 45.sdp, y = 53.sdp).size(60.sdp, 60.sdp)
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
