package net.easimer.surveyor.data.ui

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_ID
import net.easimer.surveyor.IRecordingManager
import net.easimer.surveyor.data.disk.entities.Recording

class RecordingListAdapter(private val ctx: Context, private val rm: IRecordingManager)
    : RecyclerView.Adapter<RecordingViewHolder>() {
    private var recordings: List<Recording>? = null

    init {
        setHasStableIds(true)
    }

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

    override fun getItemId(position: Int): Long {
        return recordings?.let {
            if(position < it.size) {
                it[position].recId
            } else {
                NO_ID
            }
        } ?: NO_ID
    }

    fun setRecordings(l: List<Recording>) {
        val oldList = recordings
        recordings = l
        if(oldList != null) {
            val diffResult = DiffUtil.calculateDiff(RecordingDiffCallback(oldList, l))
            diffResult.dispatchUpdatesTo(this)
        } else {
            notifyDataSetChanged()
        }
    }
}