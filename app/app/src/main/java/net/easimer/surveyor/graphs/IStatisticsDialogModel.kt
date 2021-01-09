package net.easimer.surveyor.graphs

import androidx.lifecycle.LiveData

interface IStatisticsDialogModel {
    val topSpeed: LiveData<String>
    val averageSpeed: LiveData<String>

    fun onCreateDialog(graphProvider: IGraphProvider)
}