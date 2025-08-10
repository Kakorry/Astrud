package com.github.korblu.astrud.data.common

interface PlayableForDao<T: Playable<T>> {
    suspend fun getInfoFromId(id: Long): T?
    suspend fun getPlayedList(limit: Int) : List<T>
    suspend fun insertOrUpdate(item: T)
}