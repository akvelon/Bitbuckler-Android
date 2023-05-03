package com.akvelon.bitbuckler.model.entity

enum class LoadableDataState {
    LOADING,
    NETWORK_ERROR,
    UNKNOWN_ERROR,
    LOADED_UNCHANGED,
    LOADED_CHANGED,
    NO_DATA
}
