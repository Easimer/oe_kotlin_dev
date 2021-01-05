package net.easimer.surveyor.data.disk.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true) val poiId: Long,
    val recId: Long,
    val title: String,
    val longitude: Double,
    val latitude: Double
)