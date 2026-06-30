package com.shefivan.aezaapp.domain.model

data class Product(
    val id: Long,
    val name: String,
    val typeSlug: String,
    val typeName: String,
    val groupId: Long,
    val groupName: String,
    val isPublic: Boolean,
    val isAvailable: Boolean,
    val tags: List<String>,
)
