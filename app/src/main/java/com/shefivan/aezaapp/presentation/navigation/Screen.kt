package com.shefivan.aezaapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Auth : Screen
    @Serializable data object Home : Screen
    @Serializable data object Services : Screen
    @Serializable data class ServiceDetail(val serviceId: Long) : Screen
    @Serializable data object Account : Screen
    @Serializable data object Notifications : Screen
    @Serializable data object SshKeys : Screen
    @Serializable data object Domains : Screen
    @Serializable data object Support : Screen
    @Serializable data object StockWatch : Screen
}
