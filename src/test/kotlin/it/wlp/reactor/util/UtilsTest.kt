package it.wlp.reactor.util

import org.mockito.Mockito

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> anyObject(t : T): T {
        Mockito.any<T>()
        return uninitialized(t)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(t : T): T = null as T

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T = null as T
}