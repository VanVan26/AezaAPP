package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toChangePasswordRequestDto
import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.mapper.toDto
import com.shefivan.aezaapp.domain.model.ServiceTransaction
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.EditServiceRequestDto
import com.shefivan.aezaapp.data.remote.dto.ServiceSuspendRequestDto
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.ReinstallServiceRequest
import com.shefivan.aezaapp.domain.model.RemoteVncSession
import com.shefivan.aezaapp.domain.model.Service
import com.shefivan.aezaapp.domain.model.ServiceStats
import com.shefivan.aezaapp.domain.model.ServiceStatsRequest
import com.shefivan.aezaapp.domain.repository.ServiceRepository
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : ServiceRepository {
    override suspend fun getServices(query: PageQuery): Page<Service> = api.getServices(
        offset = query.offset,
        limit = query.limit,
        sort = query.sort,
        filter = query.filter,
    ).toDomain()

    override suspend fun getService(id: Long): Service = api.getService(id).toDomain()

    override suspend fun requestDeletion(id: Long) {
        api.requestServiceDeletion(id)
    }

    override suspend fun setAutoProlong(id: Long, enabled: Boolean) {
        api.editService(id, EditServiceRequestDto(autoProlong = enabled))
    }

    override suspend fun resume(id: Long) {
        api.resumeService(id)
    }

    override suspend fun suspend(id: Long, force: Boolean) {
        api.suspendService(id, ServiceSuspendRequestDto(force = force))
    }

    override suspend fun restart(id: Long) {
        api.restartService(id)
    }

    override suspend fun enterRescueMode(id: Long) {
        api.enterRescueMode(id)
    }

    override suspend fun leaveRescueMode(id: Long) {
        api.leaveRescueMode(id)
    }

    override suspend fun connectRemoteVnc(id: Long): RemoteVncSession = api.connectRemoteVnc(id).toDomain()

    override suspend fun changePassword(id: Long, password: String) {
        api.changeServicePassword(id, password.toChangePasswordRequestDto())
    }

    override suspend fun reinstall(id: Long, request: ReinstallServiceRequest) {
        api.reinstallService(id, request.toDto())
    }

    override suspend fun getStats(id: Long, request: ServiceStatsRequest): ServiceStats = api.getServiceStats(
        id = id,
        statType = request.statType,
        resolution = request.resolution,
        fromDate = request.fromDate.toString(),
        toDate = request.toDate.toString(),
    ).toDomain()

    override suspend fun getTransactions(serviceId: Long): Page<ServiceTransaction> =
        api.getServiceTransactions(
            sort = "id DESC",
            limit = 1000,
            offset = 0,
            filter = "[[\"serviceId\",$serviceId]]",
        ).toDomain()
}
