package net.easimer.surveyor

import net.easimer.surveyor.data.disk.entities.Recording

interface RecordingManager {
    fun update(rec: Recording)
    fun replay(rec: Recording)
    fun delete(rec: Recording)
}