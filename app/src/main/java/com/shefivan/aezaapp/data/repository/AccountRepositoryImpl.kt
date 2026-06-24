package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.domain.model.Account
import com.shefivan.aezaapp.domain.repository.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : AccountRepository {
    override suspend fun getAccount(): Account = api.getAccount().toDomain()
}
