package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.CreateFileUploadLinkRequestDto
import com.shefivan.aezaapp.data.remote.dto.SaveUploadedFileRequestDto
import com.shefivan.aezaapp.domain.model.FileAsset
import com.shefivan.aezaapp.domain.model.FileUploadLink
import com.shefivan.aezaapp.domain.repository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class FileRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
    @Named("raw") private val rawHttpClient: OkHttpClient,
) : FileRepository {
    override suspend fun createUploadLink(fileName: String): FileUploadLink =
        api.createFileUploadLink(CreateFileUploadLinkRequestDto(fileName)).toDomain()

    override suspend fun uploadToPreSignedUrl(
        url: String,
        bytes: ByteArray,
        contentType: String,
        contentDisposition: String?,
    ) = withContext(Dispatchers.IO) {
        val body = bytes.toRequestBody(contentType.toMediaTypeOrNull())
        val requestBuilder = Request.Builder().url(url).put(body)
        if (contentDisposition != null) requestBuilder.addHeader("Content-Disposition", contentDisposition)
        rawHttpClient.newCall(requestBuilder.build()).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Upload failed: ${response.code}")
        }
    }

    override suspend fun saveUploadedFile(key: String, fileName: String): FileAsset =
        api.saveUploadedFile(SaveUploadedFileRequestDto(key, fileName)).toDomain()
}
