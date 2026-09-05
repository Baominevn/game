package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketplaceItem
import com.example.ui.theme.CyberPalette

@Composable
fun MarketplaceView(
    listings: List<MarketplaceItem>,
    playerCredits: Int,
    selectedMomoItem: MarketplaceItem?,
    palette: CyberPalette,
    onBuyWithCredits: (MarketplaceItem) -> Unit,
    onInitiateMomo: (MarketplaceItem) -> Unit,
    onConfirmMomo: (MarketplaceItem) -> Unit,
    onDismissMomo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("marketplace_view")
    ) {
        // Marketplace Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CHỢ ĐEN CÔNG NGHỆ P2P",
                            color = palette.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Giao dịch vũ khí & chip giữa các đặc vụ • Tích hợp MoMo",
                            color = palette.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.primary.copy(alpha = 0.15f))
                            .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$playerCredits Credits",
                            color = palette.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // MoMo Payment Gateway Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD82D8B).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFD82D8B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFFD82D8B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cổng Thanh Toán MoMo Trực Tiếp: 0909120918",
                            color = Color(0xFFFF72B3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Listings List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(listings) { listing ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surface)
                        .border(
                            1.dp,
                            if (listing.isFeatured) palette.accent else palette.borderGlow.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = listing.item.name,
                                    color = palette.primary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Người bán: ${listing.sellerCallsign} • ${listing.item.rarity.name}",
                                    color = palette.onSurface.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }

                            if (listing.isFeatured) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(palette.accent.copy(alpha = 0.2f))
                                        .border(1.dp, palette.accent, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("HOT", color = palette.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = listing.item.description,
                            color = palette.onSurface.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Buy with Credits
                            Button(
                                onClick = { onBuyWithCredits(listing) },
                                enabled = playerCredits >= listing.priceCredits,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary)
                            ) {
                                Text(
                                    "${listing.priceCredits} Credits",
                                    color = palette.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Buy with MoMo
                            Button(
                                onClick = { onInitiateMomo(listing) },
                                modifier = Modifier.weight(1f).testTag("btn_momo_${listing.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD82D8B))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${listing.priceVndMomo / 1000}k VNĐ MoMo",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // MoMo Payment Dialog
    if (selectedMomoItem != null) {
        AlertDialog(
            onDismissRequest = onDismissMomo,
            containerColor = palette.surface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFFD82D8B), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THANH TOÁN MOMO P2P",
                        color = palette.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Vật phẩm: ${selectedMomoItem.item.name}",
                        color = palette.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Số tiền thanh toán: ${selectedMomoItem.priceVndMomo} VNĐ",
                        color = palette.accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFD82D8B).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFFD82D8B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("SỐ ĐIỆN THOẠI MOMO:", color = Color(0xFFFF72B3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = selectedMomoItem.momoPhoneTarget, // 0909120918
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("NỘI DUNG CHUYỂN:", color = Color(0xFFFF72B3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("CYBER ${selectedMomoItem.id.uppercase()} BUY", color = palette.primary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sau khi quét mã hoặc chuyển tiền đến 0909120918, nhấn nút xác nhận bên dưới để hệ thống nhận diện và trao trả vật phẩm vào kho đồ ngay tức thì.",
                        color = palette.onSurface.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirmMomo(selectedMomoItem) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD82D8B)),
                    modifier = Modifier.testTag("btn_confirm_momo_paid")
                ) {
                    Text("XÁC NHẬN ĐÃ CHUYỂN TIỀN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissMomo) {
                    Text("ĐÓNG", color = palette.onSurface.copy(alpha = 0.6f))
                }
            }
        )
    }
}
