package net.easimer.surveyor.data.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RecordingListAdapter(private val ctx: Context)
    : ListAdapter<Recording, RecyclerView.ViewHolder>(
    RecordingDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = RecordingView(ctx)
        setLayout(view)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as RecordingView).bindTo(getItem(position))
    }

    private fun setLayout(cv : LinearLayout) {
        cv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}