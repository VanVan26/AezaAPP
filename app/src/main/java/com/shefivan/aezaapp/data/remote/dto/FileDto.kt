package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileAssetResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val url: String = "",
    val mime: String = "",
)

@Serializable
data class FileUploadLinkResponseDto(
    val url: String = "",
    val key: String = "",
    val contentDisposition: String? = null,
)

@Serializable
data class CreateFileUploadLinkRequestDto(
    val name: String,
)

@Serializable
data class SaveUploadedFileRequestDto(
    val key: String,
    val name: String,
)
