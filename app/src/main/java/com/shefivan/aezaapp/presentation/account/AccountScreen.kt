package com.shefivan.aezaapp.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val Background = Color(0xFFF4F4F4)
private val BorderColor = Color(0xFFE1E1E1)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val CardShape = RoundedCornerShape(16.dp)
private val AccentGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBack: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AccountViewModel.UiEvent.NavigateToAuth -> onNavigateToAuth()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        AccountTopBar(onBack = onBack)
        HorizontalDivider(color = BorderColor)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
            return@Column
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { if (!uiState.isRefreshing) viewModel.processCommand(AccountViewModel.Command.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { AvatarCard(uiState) }
                item { BalanceCard(uiState) }
                item { ProfileCard(uiState) }
                item { SettingsCard(uiState) }
                if (uiState.roles.isNotEmpty()) {
                    item { RolesCard(uiState.roles) }
                }
                item {
                    Button(
                        onClick = { viewModel.processCommand(AccountViewModel.Command.Logout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(text = "Выйти", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountTopBar(onBack: () -> Unit) {
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
            text = "Аккаунт",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AvatarCard(uiState: AccountViewModel.UiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(TextPrimary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = uiState.initials,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = uiState.email,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            if (uiState.profileName.isNotBlank()) {
                Text(text = uiState.profileName, fontSize = 13.sp, color = TextSecondary)
            }
            if (uiState.profileType.isNotBlank() && uiState.profileType != "—") {
                Text(text = uiState.profileType, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun BalanceCard(uiState: AccountViewModel.UiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Баланс", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Основной", value = uiState.balance)
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Бонусный", value = uiState.bonusBalance)
        if (uiState.bonusState.isNotBlank() && uiState.bonusState != "—") {
            HorizontalDivider(color = BorderColor)
            InfoRow(label = "Статус бонусов", value = uiState.bonusState)
        }
    }
}

@Composable
private fun ProfileCard(uiState: AccountViewModel.UiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Профиль", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        HorizontalDivider(color = BorderColor)
        if (uiState.phone.isNotBlank()) {
            InfoRow(label = "Телефон", value = uiState.phone)
            HorizontalDivider(color = BorderColor)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Двухфакторная аутентификация", fontSize = 13.sp, color = TextSecondary)
            Text(
                text = if (uiState.tfaEnabled) "включена" else "выключена",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (uiState.tfaEnabled) AccentGreen else TextSecondary,
            )
        }
        if (uiState.region.isNotBlank() && uiState.region != "—") {
            HorizontalDivider(color = BorderColor)
            InfoRow(label = "Регион", value = uiState.region)
        }
    }
}

@Composable
private fun SettingsCard(uiState: AccountViewModel.UiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Настройки интерфейса", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Язык", value = uiState.lang)
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Валюта", value = uiState.currency)
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Тема", value = uiState.theme)
    }
}

@Composable
private fun RolesCard(roles: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Роли", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        HorizontalDivider(color = BorderColor)
        roles.forEachIndexed { index, role ->
            Text(text = role, fontSize = 13.sp, color = TextPrimary)
            if (index < roles.lastIndex) HorizontalDivider(color = BorderColor)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
