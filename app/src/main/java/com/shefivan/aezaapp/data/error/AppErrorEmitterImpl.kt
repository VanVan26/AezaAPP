package com.shefivan.aezaapp.data.error

import com.shefivan.aezaapp.domain.error.AppError
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppErrorEmitterImpl @Inject constructor() : AppErrorEmitter {
    private val _errors = MutableSharedFlow<AppError>(
        replay = 1,
        extraBufferCapacity = 15,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )
    override val errors: SharedFlow<AppError> = _errors.asSharedFlow()

    override suspend fun emit(error: AppError) {
        _errors.emit(error)
    }
}
