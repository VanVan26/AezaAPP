package com.shefivan.aezaapp.domain.model

data class FileAsset(
    val id: Long,
    val name: String,
    val url: String,
    val mime: String,
)

data class FileUploadLink(
    val url: String,
    val key: String,
    val contentDisposition: String?,
)

