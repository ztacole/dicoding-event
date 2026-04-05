package com.zetta.dicodingevent.data.repository

import com.zetta.dicodingevent.data.local.entity.EventEntity
import com.zetta.dicodingevent.data.local.room.EventDao
import com.zetta.dicodingevent.data.remote.ktor.EventService
import com.zetta.dicodingevent.data.remote.response.EventDetailResponse
import com.zetta.dicodingevent.data.remote.response.EventListResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EventRepository(
    private val eventService: EventService,
    private val eventDao: EventDao,
    private val dispatcher: CoroutineDispatcher
) {
    fun getEvents(active: Int, limit: Int = 40): Flow<List<EventEntity>> =
        eventDao.getAll(active, limit)

    suspend fun refreshEvents(active: Int) = withContext(dispatcher) {
        val events = eventService.getEvents(active)
        val oldEvents = eventDao.getFavoritesSync().associateBy { it.id }
        val entities = events.events.map {
            val oldEvent = oldEvents[it.id]
            it.toEntity(
                active,
                oldEvent?.isFavorite ?: false,
                oldEvent?.updatedAt ?: 0
            )
        }

        eventDao.clearCache(active)
        eventDao.insert(entities)
    }

    fun searchEvents(active: Int, query: String): Flow<List<EventEntity>> =
        flow {
            val response = eventService.getEvents(active, query = query)

            val eventFlow = eventDao.getFavoriteIds().map { favoriteIds ->
                val favSet = favoriteIds.toSet()
                response.events.map { event ->
                    event.toEntity(active, event.id in favSet)
                }
            }
            emitAll(eventFlow)
        }.flowOn(dispatcher)

    suspend fun getEventDetail(id: Int): EventDetailResponse = withContext(dispatcher) {
        eventService.getEventDetail(id)
    }

    suspend fun toggleFavorite(event: EventEntity) = withContext(dispatcher) {
        val exists = eventDao.any(event.id)
        val updatedEvent = event.copy(
            isFavorite = !event.isFavorite,
            updatedAt = System.currentTimeMillis()
        )

        if (!exists) eventDao.insert(listOf(updatedEvent))
        else eventDao.update(updatedEvent)
    }

    fun getFavoriteEvents(): Flow<List<EventEntity>> = eventDao.getFavorites()

    fun getFavoriteEvent(id: Int): Flow<EventEntity?> = eventDao.get(id)

    suspend fun getNearestEvent(): EventListResponse = withContext(dispatcher) {
        eventService.getEvents(active = -1, limit = 1)
    }
}