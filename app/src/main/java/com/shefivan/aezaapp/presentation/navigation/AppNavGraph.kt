package com.shefivan.aezaapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.shefivan.aezaapp.presentation.account.AccountScreen
import com.shefivan.aezaapp.presentation.auth.AuthScreen
import com.shefivan.aezaapp.presentation.domains.DomainsScreen
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
                onNavigateToServices = {
                    navController.navigate(Screen.Services) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.Services> {
            ServicesScreen(
                onNavigateToDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail(serviceId))
                }
            )
        }
        composable<Screen.ServiceDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ServiceDetail>()
            ServiceDetailScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
            )
        }
        composable<Screen.Account> {
            AccountScreen()
        }
        composable<Screen.Notifications> {
            NotificationsScreen()
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
