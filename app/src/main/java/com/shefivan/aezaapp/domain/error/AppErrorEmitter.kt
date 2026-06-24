package com.shefivan.aezaapp.domain.error

import kotlinx.coroutines.flow.SharedFlow

interface AppErrorEmitter {
    val errors: SharedFlow<AppError>
    suspend fun emit(error: AppError)
}
