package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.mapper.toDto
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.CreateDomainRequestDto
import com.shefivan.aezaapp.domain.model.CreateDomainRecordInput
import com.shefivan.aezaapp.domain.model.Domain
import com.shefivan.aezaapp.domain.model.DomainRecord
import com.shefivan.aezaapp.domain.model.DomainRecordType
import com.shefivan.aezaapp.domain.model.EditDomainRecordInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.repository.DomainRepository
import javax.inject.Inject

class DomainRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : DomainRepository {
    override suspend fun getDomains(query: PageQuery): Page<Domain> = api.getDomains(
        offset = query.offset,
        limit = query.limit,
        sort = query.sort,
        filter = query.filter,
    ).toDomain()

    override suspend fun createDomain(name: String): Domain =
        api.createDomain(CreateDomainRequestDto(name)).toDomain()

    override suspend fun getDomain(id: Long): Domain = api.getDomain(id).toDomain()

    override suspend fun getExpectedNameservers(): List<String> = api.getExpectedNameservers()

    override suspend fun getRecordTypes(): List<DomainRecordType> =
        api.getRecordTypes().map { it.toDomain() }

    override suspend fun getRecords(domainId: Long, query: PageQuery): Page<DomainRecord> =
        api.getDomainRecords(
            domainId = domainId,
            offset = query.offset,
            limit = query.limit,
            sort = query.sort,
            filter = query.filter,
        ).toDomain()

    override suspend fun createRecord(domainId: Long, input: CreateDomainRecordInput): DomainRecord =
        api.createDomainRecord(domainId, input.toDto()).toDomain()

    override suspend fun editRecord(domainId: Long, recordId: Long, input: EditDomainRecordInput): DomainRecord =
        api.editDomainRecord(domainId, recordId, input.toDto()).toDomain()

    override suspend fun deleteRecord(domainId: Long, recordId: Long) {
        api.deleteDomainRecord(domainId, recordId)
    }
}
