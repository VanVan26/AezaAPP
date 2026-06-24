package com.shefivan.aezaapp.di

import com.shefivan.aezaapp.data.error.AppErrorEmitterImpl
import com.shefivan.aezaapp.data.repository.AccountRepositoryImpl
import com.shefivan.aezaapp.data.repository.AuthRepositoryImpl
import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.local.ApiKeyStorage
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.data.repository.DomainRepositoryImpl
import com.shefivan.aezaapp.data.repository.FileRepositoryImpl
import com.shefivan.aezaapp.data.repository.NotificationRepositoryImpl
import com.shefivan.aezaapp.data.repository.ServiceBackupRepositoryImpl
import com.shefivan.aezaapp.data.repository.ServiceNetworkRepositoryImpl
import com.shefivan.aezaapp.data.repository.ServiceRepositoryImpl
import com.shefivan.aezaapp.data.repository.SshKeyRepositoryImpl
import com.shefivan.aezaapp.data.repository.SupportRepositoryImpl
import com.shefivan.aezaapp.data.repository.SystemRepositoryImpl
import com.shefivan.aezaapp.domain.repository.AccountRepository
import com.shefivan.aezaapp.domain.repository.AuthRepository
import com.shefivan.aezaapp.domain.repository.DomainRepository
import com.shefivan.aezaapp.domain.repository.FileRepository
import com.shefivan.aezaapp.domain.repository.NotificationRepository
import com.shefivan.aezaapp.domain.repository.ServiceBackupRepository
import com.shefivan.aezaapp.domain.repository.ServiceNetworkRepository
import com.shefivan.aezaapp.domain.repository.ServiceRepository
import com.shefivan.aezaapp.domain.repository.SshKeyRepository
import com.shefivan.aezaapp.domain.repository.SupportRepository
import com.shefivan.aezaapp.domain.repository.SystemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindAppErrorEmitter(impl: AppErrorEmitterImpl): AppErrorEmitter

    @Binds
    @Singleton
    fun bindApiKeyProvider(impl: ApiKeyStorage): ApiKeyProvider

    @Binds
    @Singleton
    fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindDomainRepository(impl: DomainRepositoryImpl): DomainRepository

    @Binds
    @Singleton
    fun bindFileRepository(impl: FileRepositoryImpl): FileRepository

    @Binds
    @Singleton
    fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    fun bindServiceBackupRepository(impl: ServiceBackupRepositoryImpl): ServiceBackupRepository

    @Binds
    @Singleton
    fun bindServiceNetworkRepository(impl: ServiceNetworkRepositoryImpl): ServiceNetworkRepository

    @Binds
    @Singleton
    fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository

    @Binds
    @Singleton
    fun bindSshKeyRepository(impl: SshKeyRepositoryImpl): SshKeyRepository

    @Binds
    @Singleton
    fun bindSupportRepository(impl: SupportRepositoryImpl): SupportRepository

    @Binds
    @Singleton
    fun bindSystemRepository(impl: SystemRepositoryImpl): SystemRepository
}
