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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.shefivan.aezaapp.presentation.theme.NotoColorEmojiFamily

private val Background = Color(0xFFF4F4F4)
private val BorderColor = Color(0xFFE1E1E1)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val SelectedBg = Color(0xFFE8E8E8)

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

private val mainNavItems = listOf(
    NavItem("Дом", Icons.Outlined.Home, "home"),
    NavItem("Мои услуги", Icons.Outlined.Storage, "services"),
    NavItem("Финансы", Icons.Outlined.Payments, "finance"),
    NavItem("Реф. система", Icons.Outlined.Group, "referral"),
)

private val bottomNavItems = listOf(
    NavItem("База знаний", Icons.Outlined.MenuBook, "knowledge"),
    NavItem("Настройки", Icons.Outlined.Settings, "settings"),
)

@Composable
fun AppDrawer(
    currentRoute: String,
    onClose: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateServices: () -> Unit,
    onNavigateSupport: () -> Unit,
    onNavigateAccount: () -> Unit = {},
    onNavigateNotifications: () -> Unit = {},
    onNavigateStock: () -> Unit = {},
    onNavigateSshKeys: () -> Unit = {},
    onNavigateDomains: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    fun openBrowser(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    ModalDrawerSheet(
        modifier = modifier.fillMaxHeight(),
        drawerContainerColor = Background,
        drawerTonalElevation = 0.dp,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
        ) {
            mainNavItems.forEach { item ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        when (item.route) {
                            "home" -> { onNavigateHome(); onClose() }
                            "services" -> { onNavigateServices(); onClose() }
                            "finance" -> openBrowser("https://my.aeza.net/finance")
                            "referral" -> openBrowser("https://my.aeza.net/referral")
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = SelectedBg,
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = TextPrimary,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextPrimary,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Поддержка",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
                selected = currentRoute == "support",
                onClick = { onNavigateSupport(); onClose() },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SelectedBg,
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextPrimary,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Уведомления",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
                selected = currentRoute == "notifications",
                onClick = { onNavigateNotifications(); onClose() },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SelectedBg,
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextPrimary,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Наличие услуг",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
                selected = currentRoute == "stock",
                onClick = { onNavigateStock(); onClose() },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SelectedBg,
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextPrimary,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "SSH-ключи",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Key,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
                selected = currentRoute == "sshkeys",
                onClick = { onNavigateSshKeys(); onClose() },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SelectedBg,
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextPrimary,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Домены",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
                selected = currentRoute == "domains",
                onClick = { onNavigateDomains(); onClose() },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SelectedBg,
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextPrimary,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Аккаунт",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
                selected = currentRoute == "account",
                onClick = { onNavigateAccount(); onClose() },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = SelectedBg,
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextPrimary,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TextPrimary)
                    .clickable { openBrowser("https://my.aeza.net/order"); onClose() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Добавить услугу",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(Modifier.height(4.dp))

            bottomNavItems.forEach { item ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        when (item.route) {
                            "knowledge" -> openBrowser("https://wiki.aeza.net/cabinet/")
                            "settings" -> openBrowser("https://my.aeza.net/settings")
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = SelectedBg,
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = TextPrimary,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextPrimary,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}
