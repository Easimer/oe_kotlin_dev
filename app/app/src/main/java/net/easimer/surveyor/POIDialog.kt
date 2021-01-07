package net.easimer.surveyor

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import net.easimer.surveyor.databinding.LayoutPoiDialogBinding
import net.easimer.surveyor.trackpointsource.IMapTrackpointSource

class POIDialog(
    private val ctx: Context,
    private val trackPtSrc: IMapTrackpointSource) : DialogFragment() {
    protected val inflater = LayoutInflater.from(ctx)
    protected val binding = LayoutPoiDialogBinding.inflate(inflater)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding.title = ""

            builder
                .setView(binding.root)
                .setPositiveButton(R.string.save) { dialog, id ->
                    binding.title!!.let { title ->
                        trackPtSrc.markPointOfInterest(title)
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("")
    }
}