package com.zetta.dicodingevent.data.remote.ktor

import com.zetta.dicodingevent.data.remote.response.EventDetailResponse
import com.zetta.dicodingevent.data.remote.response.EventListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class EventService(private val client: HttpClient) {
    companion object {
        const val EVENT_ROUTE = "events"
    }

    suspend fun getEvents(
        active: Int? = null,
        limit: Int? = null,
        query: String? = null,
    ): EventListResponse = client.get(EVENT_ROUTE) {
        parameter("active", active)
        parameter("limit", limit)
        parameter("q", query)
    }.body()

    suspend fun getEventDetail(id: Int): EventDetailResponse =
        client.get("$EVENT_ROUTE/$id").body()
}