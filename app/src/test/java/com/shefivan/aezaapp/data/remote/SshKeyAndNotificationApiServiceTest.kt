package com.shefivan.aezaapp.data.remote

import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.CreateSshKeyRequestDto
import com.shefivan.aezaapp.data.remote.interceptor.ApiKeyInterceptor
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class SshKeyAndNotificationApiServiceTest {
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
    fun getSshKeysSendsQueryAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "items": [
                {
                  "id": 11,
                  "ownerId": 42,
                  "name": "Laptop",
                  "publicKey": "ssh-ed25519 AAA test@host",
                  "autoAssign": true,
                  "createdAt": "2026-06-23T12:00:00Z"
                }
              ],
              "total": 1
            }
            """.trimIndent(),
        )

        val page = createApiService()
            .getSshKeys(offset = 1, limit = 5, sort = "createdAt", filter = "Laptop")
            .toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/services/ssh-keys", request.url.encodedPath)
        assertEquals("1", request.url.queryParameter("offset"))
        assertEquals("5", request.url.queryParameter("limit"))
        assertEquals("createdAt", request.url.queryParameter("sort"))
        assertEquals("Laptop", request.url.queryParameter("filter"))
        assertEquals(1, page.total)
        assertEquals(11L, page.items.first().id)
        assertEquals(true, page.items.first().autoAssign)
    }

    @Test
    fun createSshKeySendsJsonBody() = runTest {
        server.enqueueJson(
            """
            {
              "id": 12,
              "ownerId": 42,
              "name": "Desktop",
              "publicKey": "ssh-ed25519 BBB test@desktop",
              "autoAssign": false,
              "createdAt": "2026-06-23T12:00:00Z"
            }
            """.trimIndent(),
        )

        val sshKey = createApiService()
            .createSshKey(
                CreateSshKeyRequestDto(
                    name = "Desktop",
                    publicKey = "ssh-ed25519 BBB test@desktop",
                    autoAssign = false,
                ),
            )
            .toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/services/ssh-keys", request.url.encodedPath)
        assertEquals("POST", request.method)
        assertEquals("test-key", request.headers["X-API-KEY"])
        assertEquals(12L, sshKey.id)
        assertEquals(false, sshKey.autoAssign)
    }

    @Test
    fun getNotificationsSendsQueryAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "items": [
                {
                  "id": 5,
                  "text": "Server restarted",
                  "isRead": false,
                  "createdAt": "2026-06-23T12:00:00Z"
                }
              ],
              "total": 1
            }
            """.trimIndent(),
        )

        val page = createApiService()
            .getNotifications(offset = 0, limit = 10, sort = "createdAt", filter = "server")
            .toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/notifications", request.url.encodedPath)
        assertEquals("0", request.url.queryParameter("offset"))
        assertEquals("10", request.url.queryParameter("limit"))
        assertEquals("createdAt", request.url.queryParameter("sort"))
        assertEquals("server", request.url.queryParameter("filter"))
        assertEquals(1, page.total)
        assertEquals(5L, page.items.first().id)
        assertEquals(false, page.items.first().isRead)
    }

    @Test
    fun markNotificationAsReadUsesExpectedPath() = runTest {
        server.enqueueNoContent()

        createApiService().markNotificationAsRead(5L)
        val request = server.takeRequest()

        assertEquals("/api/v2/notifications/5/read", request.url.encodedPath)
        assertEquals("POST", request.method)
    }

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

    private fun MockWebServer.enqueueJson(body: String) {
        enqueue(
            MockResponse.Builder()
                .code(200)
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
