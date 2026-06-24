package com.shefivan.aezaapp.domain.error

sealed class AppError {
    abstract val message: String

    data class HttpError(val code: Int, override val message: String) : AppError()
    data class NetworkError(override val message: String) : AppError()
    data class SerializationError(override val message: String) : AppError()
    data class Unknown(override val message: String) : AppError()
}
