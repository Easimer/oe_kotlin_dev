package net.easimer.surveyor.data.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.RecordingManager

data class RecordingRecyclerView(
    val recyclerView: RecyclerView,
    val viewAdapter: RecordingListAdapter,
    val viewManager: RecyclerView.LayoutManager
) {
    companion object {
        fun createRecyclerView(ctx: Context, recyclerView: RecyclerView, rm: RecordingManager): RecordingRecyclerView {
            val viewManager = LinearLayoutManager(ctx)
            val viewAdapter = RecordingListAdapter(ctx, rm)

            recyclerView.apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }

            return RecordingRecyclerView(recyclerView, viewAdapter, viewManager)
        }
    }
}