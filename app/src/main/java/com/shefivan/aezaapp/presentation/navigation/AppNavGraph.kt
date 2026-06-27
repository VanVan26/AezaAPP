package com.shefivan.aezaapp.presentation.navigation

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
import com.shefivan.aezaapp.presentation.support.SupportScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth,
        modifier = modifier,
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.Home> {
            HomeScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(Screen.ServiceDetail(serviceId))
                },
                onNavigateServices = {
                    navController.navigate(Screen.Services)
                },
                onNavigateSupport = {
                    navController.navigate(Screen.Support)
                },
                onNavigateAccount = {
                    navController.navigate(Screen.Account)
                },
                onNavigateNotifications = {
                    navController.navigate(Screen.Notifications)
                },
                onNavigateSshKeys = {
                    navController.navigate(Screen.SshKeys)
                },
                onNavigateDomains = {
                    navController.navigate(Screen.Domains)
                },
            )
        }
        composable<Screen.Services> {
            ServicesScreen(
                onNavigateToDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail(serviceId))
                },
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                },
                onNavigateSupport = {
                    navController.navigate(Screen.Support)
                },
                onNavigateAccount = {
                    navController.navigate(Screen.Account)
                },
                onNavigateNotifications = {
                    navController.navigate(Screen.Notifications)
                },
                onNavigateSshKeys = {
                    navController.navigate(Screen.SshKeys)
                },
                onNavigateDomains = {
                    navController.navigate(Screen.Domains)
                },
            )
        }
        composable<Screen.ServiceDetail> {
            ServiceDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable<Screen.Account> {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.Notifications> {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }
        composable<Screen.SshKeys> {
            SshKeysScreen(onBack = { navController.popBackStack() })
        }
        composable<Screen.Domains> {
            DomainsScreen(onBack = { navController.popBackStack() })
        }
        composable<Screen.Support> {
            SupportScreen(onBack = { navController.popBackStack() })
        }
    }
}
