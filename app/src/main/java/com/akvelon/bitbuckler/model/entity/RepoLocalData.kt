package com.akvelon.bitbuckler.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RepoLocalData(
    @PrimaryKey
    val uuid: String,
    val isTracked: Boolean = false,
)
