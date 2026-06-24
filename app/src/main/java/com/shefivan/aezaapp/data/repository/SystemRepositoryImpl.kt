package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.domain.model.SystemAlert
import com.shefivan.aezaapp.domain.repository.SystemRepository
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : SystemRepository {
    override suspend fun getAlerts(slots: String): List<SystemAlert> = api.getSystemAlerts(slots).map { it.toDomain() }

    override suspend fun getVersion(): String = api.getVersion()

    override suspend fun getHealth(): String? = api.getHealth()
        .string()
        .takeIf { it.isNotBlank() }
}
