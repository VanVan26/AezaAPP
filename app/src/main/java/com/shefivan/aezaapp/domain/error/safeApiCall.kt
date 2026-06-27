package com.shefivan.aezaapp.domain.error

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> AppErrorEmitter.safeApiCall(block: suspend () -> T): T? = try {
    block()
} catch (e: HttpException) {
    val message = parseHttpErrorBody(e) ?: httpCodeToRussian(e.code())
    emit(AppError.HttpError(code = e.code(), message = message))
    null
} catch (e: IOException) {
    val message = when (e) {
        is UnknownHostException -> "Нет соединения с интернетом"
        is SocketTimeoutException -> "Время ожидания запроса истекло"
        else -> "Ошибка соединения с сервером"
    }
    emit(AppError.NetworkError(message = message))
    null
} catch (e: SerializationException) {
    emit(AppError.SerializationError(message = e.message ?: "Ошибка обработки ответа"))
    null
} catch (e: Exception) {
    emit(AppError.Unknown(message = e.message ?: "Неизвестная ошибка"))
    null
}

private val errorJson = Json { ignoreUnknownKeys = true }

private fun parseHttpErrorBody(e: HttpException): String? = try {
    val body = e.response()?.errorBody()?.string() ?: return null
    val element = errorJson.parseToJsonElement(body)
    when (val msg = element.jsonObject["message"]) {
        is JsonArray -> msg.joinToString(", ") { (it as? JsonPrimitive)?.content.orEmpty() }
            .takeIf { it.isNotBlank() }
        is JsonPrimitive -> msg.content.takeIf { it.isNotBlank() }
        else -> null
    }
} catch (_: Exception) { null }

private fun httpCodeToRussian(code: Int): String = when (code) {
    400 -> "Неверный запрос"
    401 -> "Неверный API-ключ"
    403 -> "Доступ запрещён"
    404 -> "Ресурс не найден"
    422 -> "Ошибка валидации данных"
    429 -> "Слишком много запросов, попробуйте позже"
    in 500..599 -> "Ошибка сервера ($code)"
    else -> "HTTP ошибка $code"
}
