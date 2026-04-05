package com.zetta.dicodingevent.data.remote.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventListResponse(

    @SerialName("listEvents")
	val events: List<Event> = listOf(),

    @SerialName("error")
	val error: Boolean,

    @SerialName("message")
	val message: String
)
