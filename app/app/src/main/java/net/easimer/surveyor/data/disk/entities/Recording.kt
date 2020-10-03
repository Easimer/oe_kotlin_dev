package net.easimer.surveyor.data.disk.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Recording(
    @PrimaryKey(autoGenerate = true) val recId: Int,
    val title: String,
    val startDate: Date,
    val endDate: Date?,
    val startLongitude: Double,
    val startLatitude: Double
)