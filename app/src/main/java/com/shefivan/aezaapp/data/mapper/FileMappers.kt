package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.FileAssetResponseDto
import com.shefivan.aezaapp.data.remote.dto.FileUploadLinkResponseDto
import com.shefivan.aezaapp.domain.model.FileAsset
import com.shefivan.aezaapp.domain.model.FileUploadLink

internal fun FileAssetResponseDto.toDomain(): FileAsset = FileAsset(
    id = id,
    name = name,
    url = url,
    mime = mime,
)

internal fun FileUploadLinkResponseDto.toDomain(): FileUploadLink = FileUploadLink(
    url = url,
    key = key,
    contentDisposition = contentDisposition,
)
