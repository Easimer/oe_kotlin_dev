package net.easimer.surveyor.data.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import net.easimer.surveyor.databinding.LayoutRecordingListItemBinding
import net.easimer.surveyor.data.disk.entities.Recording

class RecordingView(private val ctx: Context) : LinearLayout(ctx) {
    protected val inflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    protected val binding = LayoutRecordingListItemBinding.inflate(inflater)
    protected lateinit var recording: Recording

    override fun addView(child : View) {
        child.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        super.addView(child)
    }

    fun bindTo(recording: Recording) {
        this.recording = recording
        binding.recording = recording
    }

    init {
        addView(binding.root)
    }
}
