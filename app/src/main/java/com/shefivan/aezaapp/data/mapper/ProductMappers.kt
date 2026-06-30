package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.ProductResponseDto
import com.shefivan.aezaapp.data.remote.dto.ProductsResponseDto
import com.shefivan.aezaapp.domain.model.Product

internal fun ProductsResponseDto.toDomain(): List<Product> = items.map { it.toDomain() }

internal fun ProductResponseDto.toDomain(): Product = Product(
    id = id,
    name = name,
    typeSlug = typeSlug,
    typeName = typeName,
    groupId = groupId,
    groupName = group?.name.orEmpty(),
    isPublic = isPublic,
    isAvailable = isAvailable,
    tags = tags,
)
