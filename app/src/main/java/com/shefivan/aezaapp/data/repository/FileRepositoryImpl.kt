package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.CreateFileUploadLinkRequestDto
import com.shefivan.aezaapp.data.remote.dto.SaveUploadedFileRequestDto
import com.shefivan.aezaapp.domain.model.FileAsset
import com.shefivan.aezaapp.domain.model.FileUploadLink
import com.shefivan.aezaapp.domain.repository.FileRepository
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : FileRepository {
    override suspend fun createUploadLink(fileName: String): FileUploadLink =
        api.createFileUploadLink(CreateFileUploadLinkRequestDto(fileName)).toDomain()

    override suspend fun saveUploadedFile(key: String, fileName: String): FileAsset =
        api.saveUploadedFile(SaveUploadedFileRequestDto(key, fileName)).toDomain()
}
