package com.shefivan.aezaapp.domain.model

import java.math.BigDecimal

enum class AccountBonusState {
    NOT_USED,
    LOCKED,
    UNLOCKED,
    UNKNOWN,
}

enum class AccountRegion {
    GLOBAL,
    RU,
    UNKNOWN,
}

enum class AccountProfileType {
    LEGAL,
    PERSON,
    UNKNOWN,
}

data class AccountInterfaceSettings(
    val lang: String,
    val currency: String,
    val theme: String,
)

data class AccountProfile(
    val name: String?,
    val names: List<String>,
    val phone: String?,
    val type: AccountProfileType?,
    val phoneConfirmed: Boolean,
)

data class AccountLegal(
    val name: String,
    val ogrn: String,
    val kpp: String,
    val inn: String,
    val address: String,
    val bik: String,
    val account: String,
    val corrAccount: String,
    val comment: String,
)

data class Account(
    val id: Long,
    val email: String,
    val photoUrl: String?,
    val balance: BigDecimal,
    val withdrawBalance: BigDecimal,
    val totalReplenished: BigDecimal,
    val bonusBalance: BigDecimal,
    val referrerProgramId: Long?,
    val bonusState: AccountBonusState?,
    val tfaEnabled: Boolean,
    val interfaceSettings: AccountInterfaceSettings,
    val legal: AccountLegal?,
    val profile: AccountProfile,
    val roles: List<String>,
    val region: AccountRegion?,
    val currency: String,
    val permittedDebt: BigDecimal,
)
