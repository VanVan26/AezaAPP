package com.shefivan.aezaapp.presentation.home

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.shefivan.aezaapp.presentation.ui.components.HomeTopBar

private val Background = Color(0xFFF4F4F4)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val BorderColor = Color(0xFFE1E1E1)
private val TitleColor = Color(0x80868686)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (Long) -> Unit = {},
    onNavigateAccount: () -> Unit = {},
    onNavigateNotifications: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        HomeTopBar(
            userInitials = uiState.userInitials,
            balance = uiState.balance,
            onMenuClick = onOpenDrawer,
            onUserClick = onNavigateAccount,
            onBellClick = onNavigateNotifications,
            onTopUpClick = {},
            modifier = Modifier.fillMaxWidth(),
        )

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { if (!uiState.isLoading && !uiState.isRefreshing) viewModel.processCommand(HomeViewModel.Command.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(bottom = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator(color = TextPrimary) }
                    }
                } else {
                    item {
                        Text(
                            text = "дом",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = TitleColor,
                            fontStyle = FontStyle.Italic,
                        )
                    }

                    item {
                        Text(
                            text = "мои услуги",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    if (uiState.servicesLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.Center,
                            ) { CircularProgressIndicator(color = TextPrimary) }
                        }
                    } else {
                        items(uiState.services, key = { it.id }) { service ->
                            ServiceCard(
                                item = service,
                                onClick = { onServiceClick(service.id) },
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Заказать услугу",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    val tileRows = viewModel.productTiles.chunked(2)
                    items(tileRows) { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            row.forEach { tile ->
                                ProductTileCard(
                                    tile = tile,
                                    onClick = {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            "https://my.aeza.net${tile.orderPath}".toUri(),
                                        )
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductTileCard(
    tile: HomeViewModel.ProductTile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = tile.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            lineHeight = 18.sp,
        )
        Text(
            text = tile.description,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp,
        )
    }
}
