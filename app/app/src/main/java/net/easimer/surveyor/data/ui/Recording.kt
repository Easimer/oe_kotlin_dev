package net.easimer.surveyor.data.ui

import java.util.*

data class Recording(
    val id: UUID,
    val title: String,
    val location: String,
    val date: Date
)