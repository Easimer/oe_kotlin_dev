package net.easimer.surveyor.data.disk.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Trackpoint(
    @PrimaryKey(autoGenerate = true) val waypointId: Int,
    val recId: Int,
    val longitude: Double,
    val latitude: Double,
    val date: Date
)