package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponseDto(
    val items: List<ProductResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class ProductResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val typeSlug: String = "",
    val typeName: String = "",
    val groupId: Long = 0L,
    val group: ProductGroupNestedDto? = null,
    val isPublic: Boolean = false,
    val isAvailable: Boolean = false,
    val tags: List<String> = emptyList(),
)

@Serializable
data class ProductGroupNestedDto(
    val id: Long = 0L,
    val name: String = "",
)
