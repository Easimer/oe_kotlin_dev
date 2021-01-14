package net.easimer.surveyor.graphs

import java.util.*

/**
 * Interface to a graph widget.
 */
interface IGraph {
    fun addSeries(data: List<Pair<Date, Double>>)
}