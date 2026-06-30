package com.shefivan.aezaapp.domain.model

typealias DynamicMap = Map<String, Any?>

data class Page<T>(
    val items: List<T>,
    val total: Int,
)

data class PageQuery(
    val offset: Int? = null,
    val limit: Int? = null,
    val sort: String? = null,
    val filter: String? = null,
)
