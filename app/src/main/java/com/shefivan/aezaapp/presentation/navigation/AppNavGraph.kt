package com.shefivan.aezaapp.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

private const val TRANSITION_DURATION = 240

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screen = Screen.Auth,
    onOpenDrawer: () -> Unit = {},
) {
    fun goBack() {
        navController.popBackStack()
    }

    fun navigate(screen: Screen) {
        navController.navigate(screen) {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(SlideDirection.Left, tween(TRANSITION_DURATION)) +
                fadeIn(tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(SlideDirection.Left, tween(TRANSITION_DURATION)) +
                fadeOut(tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(SlideDirection.Right, tween(TRANSITION_DURATION)) +
                fadeIn(tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(SlideDirection.Right, tween(TRANSITION_DURATION)) +
                fadeOut(tween(TRANSITION_DURATION))
        },
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Auth) { inclusive = true }
                        launchSingleTop = true
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
                    navController.navigate(Screen.Auth) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
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
