package com.shefivan.aezaapp.domain.usecase.servicenetwork

import com.shefivan.aezaapp.domain.repository.ServiceNetworkRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class MakeMainIpv4UseCase @Inject constructor(
    private val repository: ServiceNetworkRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(serviceId: Long, externalId: String) = errors.safeApiCall { repository.makeMainIpv4(serviceId, externalId) }
}
