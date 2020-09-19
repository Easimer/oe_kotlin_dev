package net.easimer.surveyor.data.ui

import java.util.Date

data class Recording(
    val id: Int,
    val title: String,
    val location: String,
    val date: Date
)