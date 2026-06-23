package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.CreateSshKeyInput
import com.shefivan.aezaapp.domain.model.EditSshKeyInput
import com.shefivan.aezaapp.domain.model.SshKey

interface SshKeyRepository {
    suspend fun getSshKeys(query: PageQuery = PageQuery()): Page<SshKey>

    suspend fun createSshKey(input: CreateSshKeyInput): SshKey

    suspend fun getSshKey(id: Long): SshKey

    suspend fun editSshKey(id: Long, input: EditSshKeyInput): SshKey

    suspend fun deleteSshKey(id: Long)
}
