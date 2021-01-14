package net.easimer.surveyor

import net.easimer.surveyor.data.disk.entities.Recording

/**
 * An interface through which the UI executes operations related to recordings like
 * updating their title, deleting them, etc.
 */
interface IRecordingManager {
    /**
     * Update a recording on the disk.
     *
     * Used by the UI to rename recordings.
     * @param rec Recording
     */
    fun update(rec: Recording)

    /**
     * Replay a recording. This usually means to start a [MapActivity] and display the recording on
     * it.
     * @param rec Recording
     */
    fun replay(rec: Recording)

    /**
     * Delete a recording.
     * @param rec Recording
     */
    fun delete(rec: Recording)
}