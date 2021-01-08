package net.easimer.surveyor.graphs

import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

class GraphViewWrapper(private val view: GraphView) : IGraph {
    override fun addSeries(data: List<Pair<Date, Double>>) {
        val series = LineGraphSeries(data.map { DataPoint(it.first, it.second) }.toTypedArray())
        view.addSeries(series)
    }
}