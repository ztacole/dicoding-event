package com.zetta.dicodingevent.data.remote.ktor

import android.accounts.NetworkErrorException
import android.util.Log
import com.zetta.dicodingevent.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

class ApiConfig {
    companion object {
        fun provideClient(): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    })
                }
                defaultRequest {
                    url(BuildConfig.BASE_URL)
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                }

                expectSuccess = true
                HttpResponseValidator {
                    validateResponse { response ->
                        when (response.status.value) {
                            in 400..499 -> throw Exception("Client error: ${response.status.description}")
                            in 500..599 -> throw Exception("Server is currently down")
                        }
                    }
                    handleResponseExceptionWithRequest { cause, _->
                        when (cause) {
                            is HttpRequestTimeoutException -> throw Exception("Request timed out")
                            is UnresolvedAddressException -> throw Exception("Cannot resolve host, check your connection")
                            is NetworkErrorException -> throw Exception("Check your internet connection")
                            is IOException -> throw Exception("Network error occurred")
                            else -> throw Exception("Unexpected error occurred")
                        }
                    }
                }

                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = 1)
                    retryOnExceptionIf(maxRetries = 1) { _, cause ->
                        cause is IOException
                    }
                    exponentialDelay()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 10000
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                }

                install(HttpCache)

                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.d("KtorClient", message)
                        }

                    }
                    level = LogLevel.BODY
                }
            }
        }
    }
}