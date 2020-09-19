package net.easimer.surveyor.data.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class RecordingRecyclerView(
    val recyclerView: RecyclerView,
    val viewAdapter: ListAdapter<Recording, RecyclerView.ViewHolder>,
    val viewManager: RecyclerView.LayoutManager
) {
    companion object {
        fun createRecyclerView(ctx: Context, recyclerView: RecyclerView): RecordingRecyclerView {
            val viewManager = LinearLayoutManager(ctx)
            val viewAdapter = RecordingListAdapter(ctx)

            recyclerView.apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }

            return RecordingRecyclerView(recyclerView, viewAdapter, viewManager)
        }
    }
}