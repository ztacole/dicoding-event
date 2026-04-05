package com.zetta.dicodingevent.data.remote.response

import com.zetta.dicodingevent.data.local.entity.EventEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(

    @SerialName("summary")
    val summary: String,

    @SerialName("mediaCover")
    val mediaCover: String,

    @SerialName("registrants")
    val registrants: Int,

    @SerialName("imageLogo")
    val imageLogo: String,

    @SerialName("link")
    val link: String,

    @SerialName("description")
    val description: String,

    @SerialName("ownerName")
    val ownerName: String,

    @SerialName("cityName")
    val cityName: String,

    @SerialName("quota")
    val quota: Int,

    @SerialName("name")
    val name: String,

    @SerialName("id")
    val id: Int,

    @SerialName("beginTime")
    val beginTime: String,

    @SerialName("endTime")
    val endTime: String,

    @SerialName("category")
    val category: String
) {
    fun toEntity(active: Int, isFavorite: Boolean, updatedAt: Long = 0) = EventEntity(
        id = id,
        name = name,
        mediaCover = mediaCover,
        beginTime = beginTime,
        active = active,
        isFavorite = isFavorite,
        updatedAt = updatedAt
    )
}
