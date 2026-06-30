package com.shefivan.aezaapp.domain.usecase.product

import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import com.shefivan.aezaapp.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(type: String? = null, group: Long? = null) =
        errors.safeApiCall { repository.getProducts(type, group) }
}
