package net.easimer.surveyor.data.disk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Trackpoint

@Dao
interface PointsOfInterest {
    @Insert
    fun insertPoint(poi: PointOfInterest)

    @Query("DELETE FROM PointOfInterest WHERE recId = :recId")
    fun deletePointsOfInterest(recId: Long)
}