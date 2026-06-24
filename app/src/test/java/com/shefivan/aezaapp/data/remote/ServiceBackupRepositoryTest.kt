package com.shefivan.aezaapp.data.remote

import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.interceptor.ApiKeyInterceptor
import com.shefivan.aezaapp.data.repository.ServiceBackupRepositoryImpl
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.ServiceBackupSchedule
import com.shefivan.aezaapp.domain.model.ServiceBackupScheduleType
import com.shefivan.aezaapp.domain.model.ServiceBackupSource
import com.shefivan.aezaapp.domain.model.ServiceBackupStatus
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class ServiceBackupRepositoryTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun getBackupsSendsQueryAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "items": [
                {
                  "id": 1001,
                  "name": "before-update",
                  "size": 12,
                  "createdAt": "2026-06-23T12:00:00Z",
                  "source": "manual",
                  "status": "active"
                }
              ],
              "total": 1
            }
            """.trimIndent(),
        )

        val page = createRepository().getBackups(
            serviceId = 77L,
            query = PageQuery(offset = 5, limit = 10, sort = "createdAt", filter = "manual"),
        )
        val request = server.takeRequest()

        assertEquals("/api/v2/services/77/backups", request.url.encodedPath)
        assertEquals("5", request.url.queryParameter("offset"))
        assertEquals("10", request.url.queryParameter("limit"))
        assertEquals("createdAt", request.url.queryParameter("sort"))
        assertEquals("manual", request.url.queryParameter("filter"))
        assertEquals("test-key", request.headers["X-API-KEY"])
        assertEquals(1, page.total)
        assertEquals(1001L, page.items.first().id)
        assertEquals(ServiceBackupSource.MANUAL, page.items.first().source)
        assertEquals(ServiceBackupStatus.ACTIVE, page.items.first().status)
    }

    @Test
    fun createBackupSendsNameAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "id": 1002,
              "name": "nightly",
              "size": null,
              "createdAt": "2026-06-23T12:00:00Z",
              "source": "schedule",
              "status": "creating"
            }
            """.trimIndent(),
            code = 201,
        )

        val backup = createRepository().createBackup(serviceId = 77L, name = "nightly")
        val request = server.takeRequest()
        val body = request.body!!.utf8()

        assertEquals("/api/v2/services/77/backups", request.url.encodedPath)
        assertEquals("POST", request.method)
        assertTrue(body.contains("\"name\":\"nightly\""))
        assertEquals(1002L, backup.id)
        assertEquals(ServiceBackupSource.SCHEDULE, backup.source)
        assertEquals(ServiceBackupStatus.CREATING, backup.status)
    }

    @Test
    fun backupActionsUseExpectedPaths() = runTest {
        server.enqueueNoContent()
        server.enqueueNoContent()
        server.enqueueNoContent()

        val repository = createRepository()
        repository.deleteBackup(serviceId = 77L, backupId = 1001L)
        repository.restoreBackup(serviceId = 77L, backupId = 1001L)
        repository.deleteSchedule(serviceId = 77L)

        val deleteRequest = server.takeRequest()
        val restoreRequest = server.takeRequest()
        val deleteScheduleRequest = server.takeRequest()

        assertEquals("/api/v2/services/77/backups/1001", deleteRequest.url.encodedPath)
        assertEquals("DELETE", deleteRequest.method)
        assertEquals("/api/v2/services/77/backups/1001/restore", restoreRequest.url.encodedPath)
        assertEquals("POST", restoreRequest.method)
        assertEquals("/api/v2/services/77/backups/schedule", deleteScheduleRequest.url.encodedPath)
        assertEquals("DELETE", deleteScheduleRequest.method)
    }

    @Test
    fun setScheduleSendsExpectedBody() = runTest {
        server.enqueueNoContent()

        createRepository().setSchedule(
            serviceId = 77L,
            schedule = ServiceBackupSchedule(
                limit = 3,
                type = ServiceBackupScheduleType.WEEKLY,
                weekDay = 2,
            ),
        )
        val request = server.takeRequest()
        val body = request.body!!.utf8()

        assertEquals("/api/v2/services/77/backups/schedule", request.url.encodedPath)
        assertEquals("POST", request.method)
        assertTrue(body.contains("\"limit\":3"))
        assertTrue(body.contains("\"type\":\"weekly\""))
        assertTrue(body.contains("\"weekDay\":2"))
    }

    private fun createRepository(): ServiceBackupRepositoryImpl = ServiceBackupRepositoryImpl(createApiService())

    private fun createApiService(): AezaApiService {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(FakeApiKeyProvider("test-key")))
            .build()

        return Retrofit.Builder()
            .baseUrl(server.url("/api/v2/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AezaApiService::class.java)
    }

    private fun MockWebServer.enqueueJson(body: String, code: Int = 200) {
        enqueue(
            MockResponse.Builder()
                .code(code)
                .addHeader("Content-Type", "application/json")
                .body(body)
                .build(),
        )
    }

    private fun MockWebServer.enqueueNoContent() {
        enqueue(MockResponse.Builder().code(204).build())
    }

    private class FakeApiKeyProvider(
        private val apiKey: String?,
    ) : ApiKeyProvider {
        override fun get(): String? = apiKey
    }
}
