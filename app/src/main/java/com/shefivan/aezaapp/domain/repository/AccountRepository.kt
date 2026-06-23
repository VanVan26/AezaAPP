package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Account

interface AccountRepository {
    suspend fun getAccount(): Account
}

