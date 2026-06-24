package com.shefivan.aezaapp.domain.error

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> AppErrorEmitter.safeApiCall(block: suspend () -> T): T? = try {
    block()
} catch (e: HttpException) {
    emit(AppError.HttpError(code = e.code(), message = e.message()))
    null
} catch (e: IOException) {
    emit(AppError.NetworkError(message = e.message ?: "Network error"))
    null
} catch (e: SerializationException) {
    emit(AppError.SerializationError(message = e.message ?: "Serialization error"))
    null
} catch (e: Exception) {
    emit(AppError.Unknown(message = e.message ?: "Unknown error"))
    null
}
