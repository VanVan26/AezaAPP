package com.shefivan.aezaapp.domain.model

data class Ipv4Address(
    val key: String,
    val value: String,
    val gateway: String,
    val mask: String,
    val domain: String,
)

data class Ipv6Address(
    val key: String,
    val value: String,
    val prefix: Int,
    val gateway: String,
    val ips: List<String>,
)

