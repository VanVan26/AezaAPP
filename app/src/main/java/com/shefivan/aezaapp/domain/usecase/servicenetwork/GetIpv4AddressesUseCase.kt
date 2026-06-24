package com.shefivan.aezaapp.domain.usecase.servicenetwork

import com.shefivan.aezaapp.domain.repository.ServiceNetworkRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetIpv4AddressesUseCase @Inject constructor(
    private val repository: ServiceNetworkRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(serviceId: Long) = errors.safeApiCall { repository.getIpv4Addresses(serviceId) }
}
