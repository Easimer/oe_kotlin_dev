package net.easimer.surveyor

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.databinding.LayoutRecordingDetailsBinding

/**
 * Dialog where the user can rename, delete or replay a recording.
 */
class RecordingDetailsDialog(private val ctx: Context, private val rm: IRecordingManager, private val rec: Recording) : DialogFragment() {
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
                .setNegativeButton(R.string.open_map) { dialog, id ->
                    val intent = Intent(ctx, MapActivity::class.java).apply {
                        putExtra(MapActivity.KIND, MapActivity.KIND_STATIC)
                        putExtra(MapActivity.REC_ID, rec.recId)
                    }
                    startActivity(intent)
                }
                .setNeutralButton(R.string.delete) { dialog, id ->
                    rm.delete(rec)
                }

            builder.create()
        } ?: throw IllegalStateException("")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }
}