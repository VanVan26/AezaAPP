package com.shefivan.aezaapp.domain.usecase.sshkey

import com.shefivan.aezaapp.domain.model.EditSshKeyInput
import com.shefivan.aezaapp.domain.repository.SshKeyRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class EditSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(id: Long, input: EditSshKeyInput) = errors.safeApiCall { repository.editSshKey(id, input) }
}
