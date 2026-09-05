package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.MatchTier
import com.example.model.SpectatorMatch
import com.example.ui.theme.CyberPalette

enum class PvpSubTab(val title: String) {
    MATCHMAKING("Đấu Trường"),
    SPECTATOR("Xem Đấu"),
    CHAT_LOBBY("Kênh Chat")
}

@Composable
fun PvpLobbyView(
    eloRating: Int,
    chatMessages: List<ChatMessage>,
    spectatorMatches: List<SpectatorMatch>,
    palette: CyberPalette,
    onStartPvp1v1: () -> Unit,
    onStartPvp2v2: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(PvpSubTab.MATCHMAKING) }
    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("pvp_lobby_view")
    ) {
        // ELO & Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ĐẤU TRƯỜNG PVP REALTIME",
                        color = palette.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Hệ thống xếp hạng mùa giải • Server Ping: 24ms",
                        color = palette.onSurface.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.primary.copy(alpha = 0.15f))
                        .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ELO: $eloRating",
                            color = palette.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "KIM CƯƠNG III",
                            color = palette.accent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PvpSubTab.values().forEach { tab ->
                val isSelected = (tab == activeSubTab)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) palette.primary else palette.surface)
                        .border(1.dp, if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) palette.background else palette.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Contents
        when (activeSubTab) {
            PvpSubTab.MATCHMAKING -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // 1v1 Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = palette.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Đấu Đơn 1v1 Xếp Hạng",
                                        color = palette.onSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "Ghép cặp ngẫu nhiên theo ELO. Thắng nhận +28 ELO, Thua -15 ELO.",
                                    color = palette.onSurface.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                Button(
                                    onClick = onStartPvp1v1,
                                    modifier = Modifier.fillMaxWidth().testTag("btn_pvp_1v1"),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
                                ) {
                                    Text("TÌM TRẬN 1v1 NGAY", color = palette.background, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    item {
                        // 2v2 Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Group, contentDescription = null, tint = palette.accent, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Song Đấu 2v2 Đồng Đội",
                                        color = palette.onSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "Kết hợp cùng đồng đội Bang Hội tạo chuỗi combo sát thương lượng tử.",
                                    color = palette.onSurface.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                Button(
                                    onClick = onStartPvp2v2,
                                    modifier = Modifier.fillMaxWidth().testTag("btn_pvp_2v2"),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.accent)
                                ) {
                                    Text("TÌM ĐỒNG ĐỘI 2v2", color = palette.background, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    item {
                        // Ghost Arena
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Ghost Arena (PVP Bất Đồng Bộ)",
                                    color = palette.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Thách đấu bản sao AI thần kinh của Top 10 Thách Đấu ngay cả khi họ offline.",
                                    color = palette.onSurface.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                Button(
                                    onClick = onStartPvp1v1,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary)
                                ) {
                                    Text("KHIÊU CHIẾN BÓNG MA", color = palette.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            PvpSubTab.SPECTATOR -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(spectatorMatches) { match ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = match.matchTitle,
                                        color = palette.accent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, tint = palette.primary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${match.viewersCount} đang xem", color = palette.primary, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(match.player1Callsign, color = palette.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("VS", color = palette.danger, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    Text(match.player2Callsign, color = palette.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { match.player1HpPercent },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = palette.primary,
                                    trackColor = palette.danger,
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary.copy(alpha = 0.2f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary)
                                ) {
                                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("VÀO XEM TRỰC TIẾP", color = palette.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            PvpSubTab.CHAT_LOBBY -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatMessages) { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (msg.isSystem) palette.primary.copy(alpha = 0.1f) else palette.surface)
                                    .border(1.dp, if (msg.isSyndicate) palette.accent.copy(alpha = 0.4f) else palette.borderGlow.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = msg.senderName,
                                            color = if (msg.isSystem) palette.accent else palette.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "[${msg.rankBadge}]",
                                            color = palette.onSurface.copy(alpha = 0.5f),
                                            fontSize = 9.sp
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(msg.timestamp, color = palette.onSurface.copy(alpha = 0.4f), fontSize = 9.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.message,
                                        color = palette.onSurface,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Nhập tin nhắn sảnh chờ...", color = palette.onSurface.copy(alpha = 0.4f), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("input_chat"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = palette.primary,
                                unfocusedBorderColor = palette.borderGlow.copy(alpha = 0.3f),
                                focusedTextColor = palette.onSurface,
                                unfocusedTextColor = palette.onSurface
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    onSendMessage(chatInput)
                                    chatInput = ""
                                }
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.primary)
                                .size(48.dp)
                                .testTag("btn_send_chat")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gửi", tint = palette.background)
                        }
                    }
                }
            }
        }
    }
}
