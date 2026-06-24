package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.AccountInterfaceResponseDto
import com.shefivan.aezaapp.data.remote.dto.AccountLegalResponseDto
import com.shefivan.aezaapp.data.remote.dto.AccountProfileResponseDto
import com.shefivan.aezaapp.data.remote.dto.AccountResponseDto
import com.shefivan.aezaapp.domain.model.Account
import com.shefivan.aezaapp.domain.model.AccountBonusState
import com.shefivan.aezaapp.domain.model.AccountInterfaceSettings
import com.shefivan.aezaapp.domain.model.AccountLegal
import com.shefivan.aezaapp.domain.model.AccountProfile
import com.shefivan.aezaapp.domain.model.AccountProfileType
import com.shefivan.aezaapp.domain.model.AccountRegion

internal fun AccountResponseDto.toDomain(): Account = Account(
    id = id,
    email = email,
    photoUrl = photo,
    balance = balance.toBigDecimalValue(),
    withdrawBalance = withdrawBalance.toBigDecimalValue(),
    totalReplenished = totalReplenished.toBigDecimalValue(),
    bonusBalance = bonusBalance.toBigDecimalValue(),
    referrerProgramId = referrerProgramId,
    bonusState = bonusState.toAccountBonusState(),
    tfaEnabled = tfaEnabled,
    interfaceSettings = interfaceSettings.toDomain(),
    legal = legal?.toDomain(),
    profile = profile.toDomain(),
    roles = roles,
    region = region.toAccountRegion(),
    currency = currency,
    permittedDebt = permittedDebt.toBigDecimalValue(),
)

private fun AccountInterfaceResponseDto.toDomain(): AccountInterfaceSettings = AccountInterfaceSettings(
    lang = lang,
    currency = currency,
    theme = theme,
)

private fun AccountProfileResponseDto.toDomain(): AccountProfile = AccountProfile(
    name = name,
    names = names,
    phone = phone,
    type = type.toAccountProfileType(),
    phoneConfirmed = phoneConfirmed,
)

private fun AccountLegalResponseDto.toDomain(): AccountLegal = AccountLegal(
    name = name,
    ogrn = ogrn,
    kpp = kpp,
    inn = inn,
    address = address,
    bik = bik,
    account = account,
    corrAccount = corrAccount,
    comment = comment,
)

private fun String?.toAccountBonusState(): AccountBonusState = when (this) {
    "not_used" -> AccountBonusState.NOT_USED
    "locked" -> AccountBonusState.LOCKED
    "unlocked" -> AccountBonusState.UNLOCKED
    else -> AccountBonusState.UNKNOWN
}

private fun String?.toAccountRegion(): AccountRegion = when (this) {
    "global" -> AccountRegion.GLOBAL
    "ru" -> AccountRegion.RU
    else -> AccountRegion.UNKNOWN
}

private fun String?.toAccountProfileType(): AccountProfileType? = when (this) {
    null -> null
    "legal" -> AccountProfileType.LEGAL
    "person" -> AccountProfileType.PERSON
    else -> AccountProfileType.UNKNOWN
}
