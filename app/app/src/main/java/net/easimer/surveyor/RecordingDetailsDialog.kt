package net.easimer.surveyor

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.databinding.LayoutRecordingDetailsBinding

class RecordingDetailsDialog(ctx: Context, private val rm: RecordingManager, private val rec: Recording) : DialogFragment() {
    protected val inflater = LayoutInflater.from(ctx)
    protected val binding = LayoutRecordingDetailsBinding.inflate(inflater)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding.recording = rec

            builder
                .setView(binding.root)
                .setPositiveButton(R.string.save, { dialog, id ->
                    rm.update(rec)
                })
                .setNegativeButton(R.string.open_map, { dialog, id ->

                })

            builder.create()
        } ?: throw IllegalStateException("")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }
}