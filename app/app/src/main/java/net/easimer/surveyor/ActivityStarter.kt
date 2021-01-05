package net.easimer.surveyor

/**
 * An interface through which components may start specific types of activities.
 */
interface ActivityStarter {
    /**
     * Starts a [MapActivity] activity that replays a specific recording.
     * @param recId Identifier of the recording to be played back.
     */
    fun startMapActivity(recId: Long)

    /**
     * Starts a [MapActivity] activity for a new recording.
     * 
     * This will in turn start a new [RecorderService] service.
     */
    fun startMapActivity()
}