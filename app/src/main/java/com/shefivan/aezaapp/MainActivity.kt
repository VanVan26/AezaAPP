package com.shefivan.aezaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.shefivan.aezaapp.domain.error.AppError
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.usecase.auth.ClearApiKeyUseCase
import com.shefivan.aezaapp.presentation.navigation.AppNavGraph
import com.shefivan.aezaapp.presentation.navigation.Screen
import com.shefivan.aezaapp.presentation.theme.AezaAPPTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var errorEmitter: AppErrorEmitter
    @Inject lateinit var clearApiKey: ClearApiKeyUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AezaAPPTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    errorEmitter.errors.collect { error ->
                        val onAuthScreen = navController.currentDestination?.route?.contains("Screen.Auth") == true
                        if (error is AppError.HttpError && error.code == 401 && !onAuthScreen) {
                            clearApiKey()
                            navController.navigate(Screen.Auth) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            snackbarHostState.showSnackbar(error.message)
                        }
                    }
                }

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
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
