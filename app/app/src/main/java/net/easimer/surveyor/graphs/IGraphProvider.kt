package net.easimer.surveyor.graphs

/**
 * Gives access to specific kinds of graphs.
 */
interface IGraphProvider {
    val graphSpeed: IGraph
    val graphAltitude: IGraph
}