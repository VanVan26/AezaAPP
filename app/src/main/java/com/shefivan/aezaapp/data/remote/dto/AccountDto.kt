package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponseDto(
    val id: Long = 0L,
    val email: String = "",
    val photo: String? = null,
    val balance: Double = 0.0,
    val withdrawBalance: Double = 0.0,
    val totalReplenished: Double = 0.0,
    val bonusBalance: Double = 0.0,
    val referrerProgramId: Long? = null,
    val bonusState: String? = null,
    val tfaEnabled: Boolean = false,
    @SerialName("interface") val interfaceSettings: AccountInterfaceResponseDto = AccountInterfaceResponseDto(),
    val legal: AccountLegalResponseDto? = null,
    val permittedDebt: Double = 0.0,
    val profile: AccountProfileResponseDto = AccountProfileResponseDto(),
    val roles: List<String> = emptyList(),
    val region: String? = null,
    val currency: String = "",
)

@Serializable
data class AccountInterfaceResponseDto(
    val lang: String = "",
    val currency: String = "",
    val theme: String = "",
)

@Serializable
data class AccountProfileResponseDto(
    val name: String? = null,
    val names: List<String> = emptyList(),
    val phone: String? = null,
    val type: String? = null,
    val phoneConfirmed: Boolean = false,
)

@Serializable
data class AccountLegalResponseDto(
    val name: String = "",
    val ogrn: String = "",
    val kpp: String = "",
    val inn: String = "",
    val address: String = "",
    val bik: String = "",
    val account: String = "",
    val corrAccount: String = "",
    val comment: String = "",
)
