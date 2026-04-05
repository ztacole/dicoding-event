package com.zetta.dicodingevent.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailResponse(

    @SerialName("event")
    val event: Event,

    @SerialName("error")
    val error: Boolean,

    @SerialName("message")
    val message: String
)
