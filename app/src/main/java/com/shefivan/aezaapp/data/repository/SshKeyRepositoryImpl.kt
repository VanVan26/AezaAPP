package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.mapper.toDto
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.domain.model.CreateSshKeyInput
import com.shefivan.aezaapp.domain.model.EditSshKeyInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.SshKey
import com.shefivan.aezaapp.domain.repository.SshKeyRepository
import javax.inject.Inject

class SshKeyRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : SshKeyRepository {
    override suspend fun getSshKeys(query: PageQuery): Page<SshKey> = api.getSshKeys(
        offset = query.offset,
        limit = query.limit,
        sort = query.sort,
        filter = query.filter,
    ).toDomain()

    override suspend fun createSshKey(input: CreateSshKeyInput): SshKey = api.createSshKey(input.toDto()).toDomain()

    override suspend fun getSshKey(id: Long): SshKey = api.getSshKey(id).toDomain()

    override suspend fun editSshKey(id: Long, input: EditSshKeyInput): SshKey = api.editSshKey(id, input.toDto()).toDomain()

    override suspend fun deleteSshKey(id: Long) {
        api.deleteSshKey(id)
    }
}
