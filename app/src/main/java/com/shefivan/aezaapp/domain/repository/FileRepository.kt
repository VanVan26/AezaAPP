package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.FileAsset
import com.shefivan.aezaapp.domain.model.FileUploadLink

interface FileRepository {
    suspend fun createUploadLink(fileName: String): FileUploadLink

    suspend fun uploadToPreSignedUrl(url: String, bytes: ByteArray, contentType: String, contentDisposition: String?)

    suspend fun saveUploadedFile(key: String, fileName: String): FileAsset
}

