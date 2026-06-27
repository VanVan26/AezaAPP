package com.shefivan.aezaapp.domain.usecase.file

import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import com.shefivan.aezaapp.domain.model.FileAsset
import com.shefivan.aezaapp.domain.repository.FileRepository
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val repository: FileRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(bytes: ByteArray, fileName: String, contentType: String): FileAsset? =
        errors.safeApiCall {
            val link = repository.createUploadLink(fileName)
            repository.uploadToPreSignedUrl(link.url, bytes, contentType, link.contentDisposition)
            repository.saveUploadedFile(link.key, fileName)
        }
}
