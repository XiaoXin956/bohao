package com.example.dialerclone.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.dialerclone.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DialerApp() {
    var number by remember { mutableStateOf("") }
    var showDeviceInfo by remember { mutableStateOf(false) }
    var holdExpanded by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun appendDigit(digit: String) {
        val next = number + digit
        // MIUI-style secret code: *#06# opens the mobile device identity sheet.
        // Clear the displayed text 0.2s after the code is complete, while the
        // dialpad card stays expanded for 1s before its collapse animation.
        if (next == "*#06#") {
            showDeviceInfo = true
            holdExpanded = true
            scope.launch {
                delay(200)
                number = ""
            }
            scope.launch {
                delay(1000)
                holdExpanded = false
            }
        } else {
            number = next
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val scale = minOf(maxWidth.value / Ref.WIDTH, maxHeight.value / Ref.HEIGHT)
        val offsetX = (maxWidth.value - Ref.WIDTH * scale) / 2f
        val offsetY = (maxHeight.value - Ref.HEIGHT * scale) / 2f

        val miSans = FontFamily(Font(R.font.misans_vf))
        CompositionLocalProvider(
            LocalScale provides scale,
            LocalTextStyle provides TextStyle(fontFamily = miSans)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .offset(x = offsetX.dp, y = offsetY.dp)
            ) {
                when (selectedTab) {
                        0 -> DialerHome(
                            number = number,
                            holdExpanded = holdExpanded,
                            searching = searching,
                            onSearchingChange = { searching = it },
                            onDigit = ::appendDigit,
                            onBackspace = { if (number.isNotEmpty()) number = number.dropLast(1) },
                            onBack = { number = "" },
                            onCall = {
                                Toast.makeText(
                                    context,
                                    if (number.isEmpty()) "请输入号码" else "正在拨打 $number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onShowDeviceInfo = { showDeviceInfo = true },
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )

                        1 -> PlaceholderScreen(
                            title = "联系人",
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )

                        2 -> PlaceholderScreen(
                            title = "营业厅",
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }

                    if (showDeviceInfo) {
                        DeviceInfoDialog(
                            onDismiss = {
                                showDeviceInfo = false
                                holdExpanded = false
                                number = ""
                            }
                        )
                    }
                }
            }
        }
    }
