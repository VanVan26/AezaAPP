package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.ServicesNetworksEditPtrRequestDto
import com.shefivan.aezaapp.domain.model.Ipv4Address
import com.shefivan.aezaapp.domain.model.Ipv6Address
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.repository.ServiceNetworkRepository
import javax.inject.Inject

class ServiceNetworkRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : ServiceNetworkRepository {
    override suspend fun getIpv4Addresses(serviceId: Long): Page<Ipv4Address> = api.getIpv4Addresses(serviceId).toDomain()

    override suspend fun getIpv6Addresses(serviceId: Long): Page<Ipv6Address> = api.getIpv6Addresses(serviceId).toDomain()

    override suspend fun editIpv4Ptr(serviceId: Long, externalId: String, domain: String) {
        api.editIpv4Ptr(
            serviceId = serviceId,
            externalId = externalId,
            body = ServicesNetworksEditPtrRequestDto(domain = domain),
        )
    }

    override suspend fun makeMainIpv4(serviceId: Long, externalId: String) {
        api.makeMainIpv4(
            serviceId = serviceId,
            externalId = externalId,
        )
    }
}
