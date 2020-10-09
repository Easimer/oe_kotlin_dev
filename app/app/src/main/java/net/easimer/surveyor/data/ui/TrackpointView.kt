package net.easimer.surveyor.data.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint
import net.easimer.surveyor.databinding.LayoutTrackpointListItemBinding

class TrackpointView(private val ctx: Context) : LinearLayout(ctx) {
    protected val inflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    protected val binding = LayoutTrackpointListItemBinding.inflate(inflater)
    protected lateinit var trkPt: Trackpoint

    override fun addView(child : View) {
        child.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        super.addView(child)
    }

    fun bindTo(trkPt: Trackpoint) {
        this.trkPt = trkPt
        binding.trkPt = trkPt
    }

    init {
        addView(binding.root)
    }
}