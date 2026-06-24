package com.shefivan.aezaapp.domain.usecase.sshkey

import com.shefivan.aezaapp.domain.model.CreateSshKeyInput
import com.shefivan.aezaapp.domain.repository.SshKeyRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class CreateSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(input: CreateSshKeyInput) = errors.safeApiCall { repository.createSshKey(input) }
}
