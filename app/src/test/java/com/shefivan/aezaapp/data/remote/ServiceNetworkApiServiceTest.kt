package com.shefivan.aezaapp.data.remote

import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.ServicesNetworksEditPtrRequestDto
import com.shefivan.aezaapp.data.remote.interceptor.ApiKeyInterceptor
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

class ServiceNetworkApiServiceTest {
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
    fun getIpv4AddressesUsesExpectedPathAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "total": 1,
              "items": [
                {
                  "key": "main-ipv4",
                  "value": "203.0.113.10",
                  "gateway": "203.0.113.1",
                  "mask": "255.255.255.0",
                  "domain": "example.com"
                }
              ]
            }
            """.trimIndent(),
        )

        val page = createApiService().getIpv4Addresses(serviceId = 77L).toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/services/77/networks/ipv4", request.url.encodedPath)
        assertEquals("test-key", request.headers["X-API-KEY"])
        assertEquals(1, page.total)
        assertEquals("203.0.113.10", page.items.first().value)
        assertEquals("example.com", page.items.first().domain)
    }

    @Test
    fun getIpv6AddressesUsesExpectedPathAndMapsResponse() = runTest {
        server.enqueueJson(
            """
            {
              "total": 1,
              "items": [
                {
                  "key": "ipv6-net",
                  "value": "2001:db8::",
                  "prefix": 64,
                  "gateway": "2001:db8::1",
                  "ips": ["2001:db8::10", "2001:db8::11"]
                }
              ]
            }
            """.trimIndent(),
        )

        val page = createApiService().getIpv6Addresses(serviceId = 77L).toDomain()
        val request = server.takeRequest()

        assertEquals("/api/v2/services/77/networks/ipv6", request.url.encodedPath)
        assertEquals(1, page.total)
        assertEquals(64, page.items.first().prefix)
        assertEquals(2, page.items.first().ips.size)
    }

    @Test
    fun editIpv4PtrSendsExpectedPostBody() = runTest {
        server.enqueueNoContent()

        createApiService().editIpv4Ptr(
            serviceId = 77L,
            externalId = "main-ipv4",
            body = ServicesNetworksEditPtrRequestDto(domain = "host.example.com"),
        )
        val request = server.takeRequest()
        val body = request.body!!.utf8()

        assertEquals("/api/v2/services/77/networks/ipv4/main-ipv4/edit-ptr", request.url.encodedPath)
        assertEquals("POST", request.method)
        assertTrue(body.contains("\"domain\":\"host.example.com\""))
    }

    @Test
    fun makeMainIpv4UsesExpectedPath() = runTest {
        server.enqueueNoContent()

        createApiService().makeMainIpv4(serviceId = 77L, externalId = "main-ipv4")
        val request = server.takeRequest()

        assertEquals("/api/v2/services/77/networks/ipv4/main-ipv4/make-main", request.url.encodedPath)
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


