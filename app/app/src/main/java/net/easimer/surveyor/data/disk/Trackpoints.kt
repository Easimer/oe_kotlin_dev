package net.easimer.surveyor.data.disk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.easimer.surveyor.data.disk.entities.Trackpoint

@Dao
interface Trackpoints {
    @Insert
    fun insertTrackpoint(tp: Trackpoint)
    @Query("DELETE FROM Trackpoint WHERE recId = :recId")
    fun deleteTrackpoints(recId: Long)
}