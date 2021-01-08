package net.easimer.surveyor.graphs

import java.util.*

interface IGraph {
    fun addSeries(data: List<Pair<Date, Double>>)
}