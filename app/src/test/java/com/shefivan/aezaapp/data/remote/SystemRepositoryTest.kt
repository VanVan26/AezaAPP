package com.shefivan.aezaapp.data.remote

import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.interceptor.ApiKeyInterceptor
import com.shefivan.aezaapp.data.repository.SystemRepositoryImpl
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

class SystemRepositoryTest {
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
    fun getAlertsSendsSlotsAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            [
              {
                "id": 12,
                "slot": "main",
                "title": "Maintenance",
                "body": "Planned works",
                "metadata": { "severity": "warning", "priority": 2 },
                "createdAt": "2026-06-23T12:00:00Z"
              }
            ]
            """.trimIndent(),
        )

        val alerts = createRepository().getAlerts(slots = "main,banner")
        val request = server.takeRequest()

        assertEquals("/api/v2/system/alerts", request.url.encodedPath)
        assertEquals("main,banner", request.url.queryParameter("slots"))
        assertEquals("test-key", request.headers["X-API-KEY"])
        assertEquals(1, alerts.size)
        assertEquals(12L, alerts.first().id)
        assertEquals("Maintenance", alerts.first().title)
        assertEquals("warning", alerts.first().metadata?.get("severity"))
        assertEquals(2L, alerts.first().metadata?.get("priority"))
    }

    @Test
    fun getVersionUsesExpectedPath() = runTest {
        server.enqueueJson(""""35eb0e5f"""")

        val version = createRepository().getVersion()
        val request = server.takeRequest()

        assertEquals("/api/v2/version", request.url.encodedPath)
        assertEquals("35eb0e5f", version)
    }

    @Test
    fun getHealthReturnsBodyText() = runTest {
        server.enqueueText("ok")

        val health = createRepository().getHealth()
        val request = server.takeRequest()

        assertEquals("/api/v2/health", request.url.encodedPath)
        assertEquals("ok", health)
    }

    private fun createRepository(): SystemRepositoryImpl = SystemRepositoryImpl(createApiService())

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

    private fun MockWebServer.enqueueText(body: String) {
        enqueue(
            MockResponse.Builder()
                .code(200)
                .addHeader("Content-Type", "text/plain")
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
