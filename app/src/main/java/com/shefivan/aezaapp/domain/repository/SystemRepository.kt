package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.SystemAlert

interface SystemRepository {
    suspend fun getAlerts(slots: String): List<SystemAlert>

    suspend fun getVersion(): String

    suspend fun getHealth(): String?
}

