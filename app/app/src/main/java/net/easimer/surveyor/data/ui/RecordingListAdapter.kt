package net.easimer.surveyor.data.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.R
import net.easimer.surveyor.RecordingManager
import net.easimer.surveyor.data.disk.entities.Recording

class RecordingListAdapter(private val ctx: Context, private val rm: RecordingManager)
    : RecyclerView.Adapter<RecordingViewHolder>() {
    private var recordings: List<Recording>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = RecordingView(ctx, rm)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        recordings?.let {
            val cur = it.get(position)
            holder.view.bindTo(cur)
        }
    }

    override fun getItemCount(): Int {
        return recordings?.size ?: 0
    }

    fun setRecordings(l: List<Recording>) {
        recordings = l
        notifyDataSetChanged()
    }
}