package com.shefivan.aezaapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.edit
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.withResumed
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.domain.error.AppError
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.usecase.auth.ClearApiKeyUseCase
import com.shefivan.aezaapp.notification.AezaNotificationManager
import com.shefivan.aezaapp.notification.BackgroundSyncManager
import com.shefivan.aezaapp.presentation.home.AppDrawer
import com.shefivan.aezaapp.presentation.navigation.AppNavGraph
import com.shefivan.aezaapp.presentation.navigation.Screen
import com.shefivan.aezaapp.presentation.theme.AezaAPPTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var errorEmitter: AppErrorEmitter
    @Inject lateinit var clearApiKey: ClearApiKeyUseCase
    @Inject lateinit var apiKeyProvider: ApiKeyProvider
    @Inject lateinit var backgroundSync: BackgroundSyncManager
    @Inject lateinit var notificationManager: AezaNotificationManager

    private val navTarget = MutableStateFlow<String?>(null)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {  }

    @SuppressLint("FlowOperatorInvokedInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationManager.ensureChannels()
        maybeRequestNotificationPermission()

        val loggedIn = apiKeyProvider.get() != null
        if (loggedIn) {
            backgroundSync.start()
            maybeRequestBatteryExemption()
        }
        val startDestination: Screen = if (loggedIn) Screen.Home else Screen.Auth

        navTarget.value = intent?.getStringExtra(AezaNotificationManager.EXTRA_NAV_TARGET)

        setContent {
            AezaAPPTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val backStackEntry by navController.currentBackStackEntryAsState()
                val destination = backStackEntry?.destination
                val currentRoute = when {
                    destination?.hasRoute(Screen.Home::class) == true -> "home"
                    destination?.hasRoute(Screen.Services::class) == true -> "services"
                    destination?.hasRoute(Screen.Support::class) == true -> "support"
                    destination?.hasRoute(Screen.Account::class) == true -> "account"
                    destination?.hasRoute(Screen.Notifications::class) == true -> "notifications"
                    destination?.hasRoute(Screen.StockWatch::class) == true -> "stock"
                    destination?.hasRoute(Screen.SshKeys::class) == true -> "sshkeys"
                    destination?.hasRoute(Screen.Domains::class) == true -> "domains"
                    else -> ""
                }
                val drawerEnabled = currentRoute == "home" || currentRoute == "services"

                LaunchedEffect(Unit) {
                    errorEmitter.errors.collect { error ->
                        val onAuthScreen = navController.currentDestination?.route?.contains("Screen.Auth") == true
                        if (error is AppError.HttpError && error.code == 401 && !onAuthScreen) {
                            backgroundSync.stop()
                            clearApiKey()
                            navController.navigate(Screen.Auth) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            snackbarHostState.showSnackbar(error.message)
                        }
                    }
                }

                val target by navTarget.asStateFlow().collectAsStateWithLifecycle()
                LaunchedEffect(target) {
                    val value = target ?: return@LaunchedEffect
                    if (apiKeyProvider.get() != null) {
                        val screen = when (value) {
                            AezaNotificationManager.TARGET_SUPPORT -> Screen.Support
                            AezaNotificationManager.TARGET_NOTIFICATIONS -> Screen.Notifications
                            AezaNotificationManager.TARGET_STOCK -> Screen.StockWatch
                            else -> null
                        }
                        if (screen != null) {
                            navController.navigate(screen) { launchSingleTop = true }
                        }
                    }
                    navTarget.value = null
                }

                fun closeDrawer() = scope.launch { drawerState.close() }
                fun navigateFromDrawer(screen: Screen) {
                    navController.navigate(screen) { launchSingleTop = true }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = drawerEnabled || drawerState.isOpen,
                    drawerContent = {
                        AppDrawer(
                            currentRoute = currentRoute,
                            onClose = { closeDrawer() },
                            onNavigateHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateServices = { navigateFromDrawer(Screen.Services) },
                            onNavigateSupport = { navigateFromDrawer(Screen.Support) },
                            onNavigateAccount = { navigateFromDrawer(Screen.Account) },
                            onNavigateNotifications = { navigateFromDrawer(Screen.Notifications) },
                            onNavigateStock = { navigateFromDrawer(Screen.StockWatch) },
                            onNavigateSshKeys = { navigateFromDrawer(Screen.SshKeys) },
                            onNavigateDomains = { navigateFromDrawer(Screen.Domains) },
                        )
                    },
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(snackbarHostState) { data ->
                                Snackbar(snackbarData = data)
                            }
                        },
                    ) { innerPadding ->
                        AppNavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding),
                            onOpenDrawer = {
                                val entry = backStackEntry
                                scope.launch {
                                    val ready = runCatching { entry?.lifecycle?.withResumed { } }.isSuccess
                                    if (ready) drawerState.open()
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navTarget.value = intent.getStringExtra(AezaNotificationManager.EXTRA_NAV_TARGET)
    }

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun maybeRequestBatteryExemption() {
        val powerManager = getSystemService(POWER_SERVICE) as? PowerManager ?: return
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) return

        val prefs = getSharedPreferences(APP_PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_BATTERY_PROMPTED, false)) return
        prefs.edit { putBoolean(KEY_BATTERY_PROMPTED, true) }

        @SuppressLint("BatteryLife")
        val directRequest = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.fromParts("package", packageName, null),
        )
        runCatching { startActivity(directRequest) }.onFailure {
            runCatching { startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }
        }
    }

    private companion object {
        const val APP_PREFS_NAME = "aeza_app_prefs"
        const val KEY_BATTERY_PROMPTED = "battery_exemption_prompted"
    }
}
