package net.easimer.surveyor.data.ui

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.data.disk.entities.Trackpoint

class TrackpointListAdapter(private val ctx: Context)
    : RecyclerView.Adapter<TrackpointViewHolder>() {
    private var trackpoints: List<Trackpoint>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackpointViewHolder {
        val view = TrackpointView(ctx)
        return TrackpointViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackpointViewHolder, position: Int) {
        trackpoints?.let {
            val cur = it.get(position)
            holder.view.bindTo(cur)
        }
    }

    override fun getItemCount(): Int {
        return trackpoints?.size ?: 0
    }

    fun setTrackpoints(l: List<Trackpoint>) {
        trackpoints = l
        notifyDataSetChanged()
    }
}