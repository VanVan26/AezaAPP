package com.shefivan.aezaapp.domain.usecase.file

import com.shefivan.aezaapp.domain.repository.FileRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class CreateFileUploadLinkUseCase @Inject constructor(
    private val repository: FileRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(fileName: String) = errors.safeApiCall { repository.createUploadLink(fileName) }
}
