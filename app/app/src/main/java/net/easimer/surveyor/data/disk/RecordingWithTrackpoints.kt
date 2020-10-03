package net.easimer.surveyor.data.disk

import androidx.room.Embedded
import androidx.room.Relation
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint

data class RecordingWithTrackpoints(
    @Embedded val recording: Recording,
    @Relation(
        parentColumn = "recId",
        entityColumn = "recId"
    )
    val trackpoints: List<Trackpoint>
)