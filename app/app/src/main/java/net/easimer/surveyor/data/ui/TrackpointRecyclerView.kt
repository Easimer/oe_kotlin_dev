package net.easimer.surveyor.data.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class TrackpointRecyclerView(
    val recyclerView: RecyclerView,
    val viewAdapter: TrackpointListAdapter,
    val viewManager: RecyclerView.LayoutManager
) {
    companion object {
        fun createRecyclerView(ctx: Context, recyclerView: RecyclerView): TrackpointRecyclerView {
            val viewManager = LinearLayoutManager(ctx)
            val viewAdapter = TrackpointListAdapter(ctx)

            recyclerView.apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }

            return TrackpointRecyclerView(recyclerView, viewAdapter, viewManager)
        }
    }
}