package com.akvelon.bitbuckler.common.model

import java.util.*

data class Event(val name: String) {

    val mName = UUID.randomUUID().toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (mName != other.mName) return false

        return true
    }

    override fun hashCode(): Int {
        return mName.hashCode()
    }

    companion object {}
}

fun Event.Companion.empty() = Event("")