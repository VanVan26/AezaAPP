package com.shefivan.aezaapp.presentation.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val Background = Color(0xFFF4F4F4)
private val BorderColor = Color(0xFFE1E1E1)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val UnreadDot = Color(0xFF2196F3)
private val CardShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val hasUnread = uiState.notifications.any { !it.isRead }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        NotificationsTopBar(
            onBack = onBack,
            showMarkAll = hasUnread && !uiState.markingAllRead,
            onMarkAll = { viewModel.processCommand(NotificationsViewModel.Command.MarkAllRead) },
        )
        HorizontalDivider(color = BorderColor)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
            return@Column
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { if (!uiState.isRefreshing) viewModel.processCommand(NotificationsViewModel.Command.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет уведомлений", fontSize = 15.sp, color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.notifications, key = { it.id }) { notification ->
                        val isExpanded = uiState.expandedIds.contains(notification.id)
                        val isLoadingDetail = uiState.loadingDetailIds.contains(notification.id)
                        val fullText = uiState.detailTexts[notification.id]
                        NotificationCard(
                            item = notification,
                            isExpanded = isExpanded,
                            isLoadingDetail = isLoadingDetail,
                            fullText = fullText,
                            onClick = { viewModel.processCommand(NotificationsViewModel.Command.ToggleExpand(notification.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(
    onBack: () -> Unit,
    showMarkAll: Boolean,
    onMarkAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
        }
        Text(
            text = "Уведомления",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        if (showMarkAll) {
            TextButton(onClick = onMarkAll) {
                Text("Прочитать все", fontSize = 13.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationsViewModel.NotificationUiItem,
    isExpanded: Boolean,
    isLoadingDetail: Boolean,
    fullText: String?,
    onClick: () -> Unit,
) {
    val chevronAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "chevron")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(if (item.isRead) Color.White else Color(0xFFF0F7FF))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(UnreadDot),
                )
            } else {
                Box(modifier = Modifier.size(8.dp).padding(top = 5.dp))
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.text,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                )
                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                tint = TextSecondary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
                    .rotate(chevronAngle),
            )
        }

        AnimatedVisibility(visible = isExpanded && (isLoadingDetail || (fullText != null && fullText != item.text))) {
            Column {
                HorizontalDivider(color = BorderColor)
                if (isLoadingDetail) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = TextPrimary)
                    }
                } else if (fullText != null && fullText != item.text) {
                    Text(
                        text = fullText,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}
