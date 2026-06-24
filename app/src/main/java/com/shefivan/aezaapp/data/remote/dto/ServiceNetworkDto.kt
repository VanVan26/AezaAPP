package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Ipv4ListResponseDto(
    val total: Int = 0,
    val items: List<Ipv4ResponseDto> = emptyList(),
)

@Serializable
data class Ipv4ResponseDto(
    val key: String = "",
    val value: String = "",
    val gateway: String = "",
    val mask: String = "",
    val domain: String = "",
)

@Serializable
data class Ipv6ListResponseDto(
    val total: Int = 0,
    val items: List<Ipv6ResponseDto> = emptyList(),
)

@Serializable
data class Ipv6ResponseDto(
    val key: String = "",
    val value: String = "",
    val prefix: Int = 0,
    val gateway: String = "",
    val ips: List<String> = emptyList(),
)

@Serializable
data class ServicesNetworksEditPtrRequestDto(
    val domain: String,
)

