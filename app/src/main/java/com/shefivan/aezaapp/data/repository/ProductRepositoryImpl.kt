package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.domain.model.Product
import com.shefivan.aezaapp.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : ProductRepository {
    override suspend fun getProducts(type: String?, group: Long?): List<Product> =
        api.getProducts(type = type, group = group).toDomain()
}
