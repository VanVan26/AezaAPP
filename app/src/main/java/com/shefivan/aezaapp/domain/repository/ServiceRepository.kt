package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.ReinstallServiceRequest
import com.shefivan.aezaapp.domain.model.RemoteVncSession
import com.shefivan.aezaapp.domain.model.Service
import com.shefivan.aezaapp.domain.model.ServiceStats
import com.shefivan.aezaapp.domain.model.ServiceStatsRequest

interface ServiceRepository {
    suspend fun getServices(query: PageQuery = PageQuery()): Page<Service>

    suspend fun getService(id: Long): Service

    suspend fun requestDeletion(id: Long)

    suspend fun resume(id: Long)

    suspend fun suspend(id: Long, force: Boolean = false)

    suspend fun restart(id: Long)

    suspend fun enterRescueMode(id: Long)

    suspend fun leaveRescueMode(id: Long)

    suspend fun connectRemoteVnc(id: Long): RemoteVncSession

    suspend fun changePassword(id: Long, password: String)

    suspend fun reinstall(id: Long, request: ReinstallServiceRequest)

    suspend fun getStats(id: Long, request: ServiceStatsRequest): ServiceStats
}

