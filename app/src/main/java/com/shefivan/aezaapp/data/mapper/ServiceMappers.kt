package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.ChangePasswordRequestDto
import com.shefivan.aezaapp.data.remote.dto.ProductNestedResponseDto
import com.shefivan.aezaapp.data.remote.dto.ReinstallServiceRequestDto
import com.shefivan.aezaapp.data.remote.dto.RemoteVncResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceSmallResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceStatsResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceTaskListResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServicesListResponseDto
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.ReinstallServiceRequest
import com.shefivan.aezaapp.domain.model.RemoteVncSession
import com.shefivan.aezaapp.domain.model.Service
import com.shefivan.aezaapp.domain.model.ServiceCapability
import com.shefivan.aezaapp.domain.model.ServiceProduct
import com.shefivan.aezaapp.domain.model.ServiceStats
import com.shefivan.aezaapp.domain.model.ServiceStatus
import com.shefivan.aezaapp.domain.model.ServiceTask
import com.shefivan.aezaapp.domain.model.ServiceTaskStatus
import com.shefivan.aezaapp.domain.model.ServiceTerm
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

internal fun ServicesListResponseDto.toDomain(): Page<Service> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun ServiceResponseDto.toDomain(): Service = Service(
    id = id,
    name = name,
    ip = ip,
    payload = payload.toDynamicMap(),
    price = price.toBigDecimalValue(),
    paymentTerm = paymentTerm.toServiceTerm(),
    autoProlong = autoProlong,
    createdAt = createdAt.toInstantOrEpoch(),
    expiresAt = expiresAt.toInstantOrEpoch(),
    status = status.toServiceStatus(),
    typeSlug = typeSlug,
    productName = productName,
    product = product.toDomain(),
    locationCode = locationCode,
    currentTask = currentTask?.toDomain(),
    capabilities = capabilities.toServiceCapabilities(),
    ownerId = ownerId,
    productId = productId,
    backupsEnabled = backups,
    schedule = schedule.toDynamicMap(),
    parameters = parameters.toDynamicMap(),
    secureParameters = secureParameters.toDynamicMap(),
)

private fun ServiceSmallResponseDto.toDomain(): Service = Service(
    id = id,
    name = name,
    ip = ip,
    payload = payload.toDynamicMap(),
    price = price.toBigDecimalValue(),
    paymentTerm = paymentTerm.toServiceTerm(),
    autoProlong = autoProlong,
    createdAt = createdAt.toInstantOrEpoch(),
    expiresAt = expiresAt.toInstantOrEpoch(),
    status = status.toServiceStatus(),
    typeSlug = typeSlug,
    productName = productName,
    product = product.toDomain(),
    locationCode = locationCode,
    currentTask = currentTask?.toDomain(),
    capabilities = capabilities.toServiceCapabilities(),
    ownerId = null,
    productId = product.id,
    backupsEnabled = null,
    schedule = emptyMap(),
    parameters = emptyMap(),
    secureParameters = emptyMap(),
)

private fun ProductNestedResponseDto.toDomain(): ServiceProduct = ServiceProduct(
    id = id,
    name = name,
    typeSlug = typeSlug,
    typeName = typeName,
    groupId = groupId,
    payload = payload.toDynamicMap(),
    localizedPayload = localedPayload.toDynamicMap(),
)

private fun com.shefivan.aezaapp.data.remote.dto.ServiceTaskResponseDto.toDomain(): ServiceTask = ServiceTask(
    id = id,
    slug = slug,
    name = name,
    createdAt = createdAt.toInstantOrEpoch(),
    status = status.toServiceTaskStatus(),
)

internal fun RemoteVncResponseDto.toDomain(): RemoteVncSession = RemoteVncSession(
    address = address,
    password = password,
)

internal fun ServiceStatsResponseDto.toDomain(): ServiceStats = ServiceStats(data = data)

internal fun ServiceTaskListResponseDto.toDomain(): Page<ServiceTask> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun String.toChangePasswordRequestDto(): ChangePasswordRequestDto = ChangePasswordRequestDto(password = this)

internal fun ReinstallServiceRequest.toDto(): ReinstallServiceRequestDto = ReinstallServiceRequestDto(
    os = os,
    recipe = recipe,
    password = password,
    sshKeyIds = sshKeyIds,
)

private fun String.toServiceTerm(): ServiceTerm = when (this) {
    "hour" -> ServiceTerm.HOUR
    "half_day" -> ServiceTerm.HALF_DAY
    "day" -> ServiceTerm.DAY
    "week" -> ServiceTerm.WEEK
    "month" -> ServiceTerm.MONTH
    "quarter_year" -> ServiceTerm.QUARTER_YEAR
    "half_year" -> ServiceTerm.HALF_YEAR
    "year" -> ServiceTerm.YEAR
    "eternal" -> ServiceTerm.ETERNAL
    else -> ServiceTerm.UNKNOWN
}

private fun String.toServiceStatus(): ServiceStatus = when (this) {
    "activation_wait" -> ServiceStatus.ACTIVATION_WAIT
    "active" -> ServiceStatus.ACTIVE
    "suspended" -> ServiceStatus.SUSPENDED
    "prolong_wait" -> ServiceStatus.PROLONG_WAIT
    "deleted" -> ServiceStatus.DELETED
    "blocked" -> ServiceStatus.BLOCKED
    "rescue" -> ServiceStatus.RESCUE
    else -> ServiceStatus.UNKNOWN
}

private fun String.toServiceTaskStatus(): ServiceTaskStatus = when (this) {
    "queued" -> ServiceTaskStatus.QUEUED
    "running" -> ServiceTaskStatus.RUNNING
    "failed" -> ServiceTaskStatus.FAILED
    "success" -> ServiceTaskStatus.SUCCESS
    "cancelled" -> ServiceTaskStatus.CANCELLED
    "wait_child" -> ServiceTaskStatus.WAIT_CHILD
    "manual" -> ServiceTaskStatus.MANUAL
    else -> ServiceTaskStatus.UNKNOWN
}

private fun JsonElement?.toServiceCapabilities(): Set<ServiceCapability> = when (this) {
    is JsonArray -> mapNotNull { (it as? JsonPrimitive)?.content?.toServiceCapabilityOrNull() }.toSet()
    is JsonPrimitive -> content.toServiceCapabilityOrNull()?.let(::setOf).orEmpty()
    else -> emptySet()
}

private fun String.toServiceCapabilityOrNull(): ServiceCapability? = ServiceCapability.entries
    .firstOrNull { it.apiValue == this }
