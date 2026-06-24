package com.shefivan.aezaapp.domain.usecase.sshkey

import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.repository.SshKeyRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetSshKeysUseCase @Inject constructor(
    private val repository: SshKeyRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(query: PageQuery = PageQuery()) = errors.safeApiCall { repository.getSshKeys(query) }
}
