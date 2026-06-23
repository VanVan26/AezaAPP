package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Ipv4Address
import com.shefivan.aezaapp.domain.model.Ipv6Address
import com.shefivan.aezaapp.domain.model.Page

interface ServiceNetworkRepository {
    suspend fun getIpv4Addresses(serviceId: Long): Page<Ipv4Address>

    suspend fun getIpv6Addresses(serviceId: Long): Page<Ipv6Address>

    suspend fun editIpv4Ptr(serviceId: Long, externalId: String, domain: String)

    suspend fun makeMainIpv4(serviceId: Long, externalId: String)
}

