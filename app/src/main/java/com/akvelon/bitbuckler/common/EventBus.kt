package com.akvelon.bitbuckler.common

import com.akvelon.bitbuckler.common.model.Event
import com.akvelon.bitbuckler.common.model.empty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

object EventBus {
    private val buffer: MutableStateFlow<Event> by lazy {
        MutableStateFlow(Event.empty())
    }

    fun pushEvent(event: Event) = GlobalScope.launch {
        buffer.emit(event)
    }

    fun subscribeByName(name: String): Flow<Event> {
        return buffer.filter { it.name == name }
    }

    fun subscribeByNames(vararg names: String): Flow<Event> {
        return buffer.filter { names.contains(it.name)  }
    }
}