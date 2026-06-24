package com.shefivan.aezaapp.domain.usecase.file

import com.shefivan.aezaapp.domain.repository.FileRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class SaveUploadedFileUseCase @Inject constructor(
    private val repository: FileRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(key: String, fileName: String) = errors.safeApiCall { repository.saveUploadedFile(key, fileName) }
}
