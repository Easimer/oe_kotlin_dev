package net.easimer.surveyor.data.disk

import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.TypeConverters
import net.easimer.surveyor.data.disk.Recordings
import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint

@Database(entities = arrayOf(Recording::class, Trackpoint::class, PointOfInterest::class), version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun recordings() : Recordings
    abstract fun trackpoints() : Trackpoints
}