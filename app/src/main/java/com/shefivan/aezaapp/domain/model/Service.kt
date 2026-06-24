package com.shefivan.aezaapp.domain.model

import java.math.BigDecimal
import java.time.Instant

enum class ServiceTerm {
    HOUR,
    HALF_DAY,
    DAY,
    WEEK,
    MONTH,
    QUARTER_YEAR,
    HALF_YEAR,
    YEAR,
    ETERNAL,
    UNKNOWN,
}

enum class ServiceStatus {
    ACTIVATION_WAIT,
    ACTIVE,
    SUSPENDED,
    PROLONG_WAIT,
    DELETED,
    BLOCKED,
    RESCUE,
    UNKNOWN,
}

enum class ServiceTaskStatus {
    QUEUED,
    RUNNING,
    FAILED,
    SUCCESS,
    CANCELLED,
    WAIT_CHILD,
    MANUAL,
    UNKNOWN,
}

enum class ServiceCapability(val apiValue: String) {
    ORDER_MANY("order_many"),
    CONTROL("ctl"),
    RESTART("ctl.restart"),
    FORCE_OFF("ctl.force_off"),
    IP("ip"),
    MANUAL_DELETE("manual_delete"),
    UPGRADE("upgrade"),
    CHARTS("charts"),
    RENAME("rename"),
    CHANGE_PASSWORD("change_password"),
    REINSTALL("reinstall"),
    BACKUPS("backups"),
    CLONE("clone"),
    REMOTE("remote"),
    VNC("remote.vnc"),
    RESCUE("rescue"),
    GOTO("goto"),
    SSH_KEYS("ssh_keys"),
}

data class ServiceProduct(
    val id: Long,
    val name: String,
    val typeSlug: String,
    val typeName: String,
    val groupId: Long,
    val payload: DynamicMap,
    val localizedPayload: DynamicMap,
)

data class ServiceTask(
    val id: String,
    val slug: String,
    val name: String,
    val createdAt: Instant,
    val status: ServiceTaskStatus,
)

data class Service(
    val id: Long,
    val name: String,
    val ip: String,
    val payload: DynamicMap,
    val price: BigDecimal,
    val paymentTerm: ServiceTerm,
    val autoProlong: Boolean,
    val createdAt: Instant,
    val expiresAt: Instant,
    val status: ServiceStatus,
    val typeSlug: String,
    val productName: String,
    val product: ServiceProduct,
    val locationCode: String?,
    val currentTask: ServiceTask?,
    val capabilities: Set<ServiceCapability>,
    val ownerId: Long?,
    val productId: Long?,
    val backupsEnabled: Boolean?,
    val schedule: DynamicMap,
    val parameters: DynamicMap,
    val secureParameters: DynamicMap,
)

data class RemoteVncSession(
    val address: String,
    val password: String,
)

data class ReinstallServiceRequest(
    val os: String,
    val recipe: String,
    val password: String,
    val sshKeyIds: List<Long> = emptyList(),
)

data class ServiceStatsRequest(
    val statType: String,
    val resolution: Int,
    val fromDate: Instant,
    val toDate: Instant,
)

data class ServiceStats(
    val data: List<String>,
)
