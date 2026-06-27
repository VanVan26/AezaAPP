package com.shefivan.aezaapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shefivan.aezaapp.presentation.theme.NotoColorEmojiFamily

private val CardShape = RoundedCornerShape(16.dp)
private val BorderColor = Color(0xFFE1E1E1)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val StatusActive = Color(0xFF4CAF50)
private val StatusInactive = Color(0xFFFF5722)

@Composable
fun ServiceCard(
    item: HomeViewModel.ServiceUiItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = item.flag,
            fontSize = 28.sp,
            fontFamily = NotoColorEmojiFamily,
            modifier = Modifier.padding(top = 2.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text(
                    text = item.typeLabel,
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }

            Text(
                text = item.planName,
                fontSize = 13.sp,
                color = TextSecondary,
            )

            if (item.ip.isNotBlank()) {
                Text(
                    text = item.ip,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                    )
                    if (item.priceTerm.isNotBlank()) {
                        Text(
                            text = " ${item.priceTerm}",
                            fontSize = 13.sp,
                            color = TextSecondary,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (item.isActive) StatusActive else StatusInactive),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = item.statusLabel,
                        fontSize = 13.sp,
                        color = if (item.isActive) StatusActive else StatusInactive,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = item.expiresDate,
                        fontSize = 13.sp,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}
