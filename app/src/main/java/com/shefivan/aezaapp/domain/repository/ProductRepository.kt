package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(type: String? = null, group: Long? = null): List<Product>
}
