package com.github.korblu.astrud.data.common

interface Playable<T : Playable<T>> {
    val id: Long
    val played: Int
    fun withPlayedCount(count: Int): T
}