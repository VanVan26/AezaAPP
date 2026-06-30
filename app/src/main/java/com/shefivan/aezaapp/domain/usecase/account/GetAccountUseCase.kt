package com.shefivan.aezaapp.domain.usecase.account

import com.shefivan.aezaapp.domain.repository.AccountRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke() = errors.safeApiCall { repository.getAccount() }
}
