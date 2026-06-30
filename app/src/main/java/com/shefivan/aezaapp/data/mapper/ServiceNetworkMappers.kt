package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.Ipv4ListResponseDto
import com.shefivan.aezaapp.data.remote.dto.Ipv4ResponseDto
import com.shefivan.aezaapp.data.remote.dto.Ipv6ListResponseDto
import com.shefivan.aezaapp.data.remote.dto.Ipv6ResponseDto
import com.shefivan.aezaapp.domain.model.Ipv4Address
import com.shefivan.aezaapp.domain.model.Ipv6Address
import com.shefivan.aezaapp.domain.model.Page

internal fun Ipv4ListResponseDto.toDomain(): Page<Ipv4Address> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

private fun Ipv4ResponseDto.toDomain(): Ipv4Address = Ipv4Address(
    key = key,
    value = value,
    gateway = gateway,
    mask = mask,
    domain = domain,
)

internal fun Ipv6ListResponseDto.toDomain(): Page<Ipv6Address> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

private fun Ipv6ResponseDto.toDomain(): Ipv6Address = Ipv6Address(
    key = key,
    value = value,
    prefix = prefix,
    gateway = gateway,
    ips = ips,
)
