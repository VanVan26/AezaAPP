package com.shefivan.aezaapp.data.remote

import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.interceptor.ApiKeyInterceptor
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AezaApiServiceTest {
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
    fun getAccountAddsApiKeyHeaderAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "id": 42,
              "email": "user@example.com",
              "photo": null,
              "balance": 123.45,
              "withdrawBalance": 10.0,
              "totalReplenished": 200.0,
              "bonusBalance": 5.0,
              "referrerProgramId": null,
              "bonusState": "unlocked",
              "tfaEnabled": true,
              "interface": { "lang": "ru", "currency": "eur", "theme": "light" },
              "legal": null,
              "permittedDebt": 0.0,
              "profile": {
                "name": "Ivan",
                "names": ["Ivan"],
                "phone": "+10000000000",
                "type": "person",
                "phoneConfirmed": true
              },
              "roles": ["user"],
              "region": "global",
              "currency": "eur"
            }
            """.trimIndent(),
        )

        val account = createApiService(apiKey = "test-key").getAccount().toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/accounts/me", request.url.encodedPath)
        assertEquals("test-key", request.headers["X-API-KEY"])
        assertEquals(42L, account.id)
        assertEquals("user@example.com", account.email)
        assertEquals("Ivan", account.profile.name)
        assertEquals(true, account.tfaEnabled)
    }

    @Test
    fun getServicesMapsItemsAndQuery() = runTest {
        server.enqueueJson(
            """
            {
              "items": [
                {
                  "id": 7,
                  "name": "Test VPS",
                  "ip": "127.0.0.1",
                  "payload": { "cpu": 2 },
                  "price": 9.99,
                  "paymentTerm": "month",
                  "autoProlong": false,
                  "createdAt": "2026-06-23T12:00:00Z",
                  "expiresAt": "2026-07-23T12:00:00Z",
                  "status": "active",
                  "typeSlug": "vps",
                  "productName": "VPS Basic",
                  "product": {
                    "id": 3,
                    "name": "VPS Basic",
                    "typeSlug": "vps",
                    "typeName": "VPS",
                    "groupId": 1,
                    "payload": {},
                    "localedPayload": {}
                  },
                  "locationCode": "de",
                  "currentTask": null,
                  "capabilities": ["ctl", "ctl.restart", "change_password"]
                }
              ],
              "total": 1
            }
            """.trimIndent(),
        )

        val page = createApiService(apiKey = "test-key")
            .getServices(offset = 10, limit = 20, sort = "id", filter = "active")
            .toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/services", request.url.encodedPath)
        assertEquals("10", request.url.queryParameter("offset"))
        assertEquals("20", request.url.queryParameter("limit"))
        assertEquals("id", request.url.queryParameter("sort"))
        assertEquals("active", request.url.queryParameter("filter"))
        assertEquals(1, page.total)
        assertEquals(7L, page.items.first().id)
        assertEquals("Test VPS", page.items.first().name)
        assertEquals(3, page.items.first().capabilities.size)
    }

    @Test
    fun requestWithoutApiKeyDoesNotAddApiKeyHeader() = runTest {
        server.enqueueJson(
            """
            {
              "id": 1,
              "email": "user@example.com",
              "photo": null,
              "balance": 0.0,
              "withdrawBalance": 0.0,
              "totalReplenished": 0.0,
              "bonusBalance": 0.0,
              "referrerProgramId": null,
              "bonusState": "not_used",
              "tfaEnabled": false,
              "interface": { "lang": "en", "currency": "eur", "theme": "light" },
              "legal": null,
              "permittedDebt": 0.0,
              "profile": { "phoneConfirmed": false },
              "roles": [],
              "region": "global",
              "currency": "eur"
            }
            """.trimIndent(),
        )

        createApiService(apiKey = null).getAccount()
        val request = server.takeRequest()

        assertNull(request.headers["X-API-KEY"])
    }

    private fun createApiService(apiKey: String?): AezaApiService {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(FakeApiKeyProvider(apiKey)))
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

    private class FakeApiKeyProvider(
        private val apiKey: String?,
    ) : ApiKeyProvider {
        override fun get(): String? = apiKey
    }
}

