package net.easimer.surveyor.graphs

interface IStatisticsDialogModel {
    val topSpeed: String
    val averageSpeed: String

    fun onCreateDialog(graphProvider: IGraphProvider)
}