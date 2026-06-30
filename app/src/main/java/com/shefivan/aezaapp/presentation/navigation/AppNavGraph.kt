package com.shefivan.aezaapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shefivan.aezaapp.presentation.account.AccountScreen
import com.shefivan.aezaapp.presentation.auth.AuthScreen
import com.shefivan.aezaapp.presentation.domains.DomainsScreen
import com.shefivan.aezaapp.presentation.home.HomeScreen
import com.shefivan.aezaapp.presentation.notifications.NotificationsScreen
import com.shefivan.aezaapp.presentation.servicedetail.ServiceDetailScreen
import com.shefivan.aezaapp.presentation.services.ServicesScreen
import com.shefivan.aezaapp.presentation.sshkeys.SshKeysScreen
import com.shefivan.aezaapp.presentation.stock.StockWatchScreen
import com.shefivan.aezaapp.presentation.support.SupportScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit = {},
) {
    fun settled(): Boolean =
        navController.currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true

    fun goBack() {
        if (settled()) navController.popBackStack()
    }

    fun navigate(screen: Screen) {
        if (settled()) navController.navigate(screen)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Auth,
        modifier = modifier,
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                onNavigateToHome = {
                    if (settled()) {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Auth) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable<Screen.Home> {
            HomeScreen(
                onServiceClick = { serviceId -> navigate(Screen.ServiceDetail(serviceId)) },
                onNavigateAccount = { navigate(Screen.Account) },
                onNavigateNotifications = { navigate(Screen.Notifications) },
                onOpenDrawer = onOpenDrawer,
            )
        }
        composable<Screen.Services> {
            ServicesScreen(
                onNavigateToDetail = { serviceId -> navigate(Screen.ServiceDetail(serviceId)) },
                onBack = { goBack() },
                onOpenDrawer = onOpenDrawer,
            )
        }
        composable<Screen.ServiceDetail> {
            ServiceDetailScreen(
                onBack = { goBack() },
            )
        }
        composable<Screen.Account> {
            AccountScreen(
                onBack = { goBack() },
                onNavigateToAuth = {
                    if (settled()) {
                        navController.navigate(Screen.Auth) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }
        composable<Screen.Notifications> {
            NotificationsScreen(onBack = { goBack() })
        }
        composable<Screen.SshKeys> {
            SshKeysScreen(onBack = { goBack() })
        }
        composable<Screen.Domains> {
            DomainsScreen(onBack = { goBack() })
        }
        composable<Screen.Support> {
            SupportScreen(onBack = { goBack() })
        }
        composable<Screen.StockWatch> {
            StockWatchScreen(onBack = { goBack() })
        }
    }
}
