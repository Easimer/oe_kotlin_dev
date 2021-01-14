package net.easimer.surveyor.graphs

import androidx.lifecycle.LiveData

/**
 * Model of a stats dialog.
 */
interface IStatisticsDialogModel {
    val topSpeed: LiveData<String>
    val averageSpeed: LiveData<String>
    val totalDistance: LiveData<String>
    val timeTaken: LiveData<String>

    /**
     * Should be called by the dialog UI when it is done being created.
     * @param graphProvider A reference that provides access to the graphs.
     *
     * TODO: UI references the model and the model references the UI. Really bad!
     */
    fun onCreateDialog(graphProvider: IGraphProvider)
}