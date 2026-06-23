package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Domain
import com.shefivan.aezaapp.domain.model.CreateDomainRecordInput
import com.shefivan.aezaapp.domain.model.DomainRecord
import com.shefivan.aezaapp.domain.model.DomainRecordType
import com.shefivan.aezaapp.domain.model.EditDomainRecordInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery

interface DomainRepository {
    suspend fun getDomains(query: PageQuery = PageQuery()): Page<Domain>

    suspend fun createDomain(name: String): Domain

    suspend fun getDomain(id: Long): Domain

    suspend fun getExpectedNameservers(): List<String>

    suspend fun getRecordTypes(): List<DomainRecordType>

    suspend fun getRecords(domainId: Long, query: PageQuery = PageQuery()): Page<DomainRecord>

    suspend fun createRecord(domainId: Long, input: CreateDomainRecordInput): DomainRecord

    suspend fun editRecord(domainId: Long, recordId: Long, input: EditDomainRecordInput): DomainRecord

    suspend fun deleteRecord(domainId: Long, recordId: Long)
}
