package net.easimer.surveyor.graphs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import net.easimer.surveyor.R
import net.easimer.surveyor.databinding.LayoutStatsDialogBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Statistics dialog UI.
 */
class StatisticsDialog(
    private val ctx: Context,
    private val model: IStatisticsDialogModel
    ) : DialogFragment(), IGraphProvider {
    protected val inflater = LayoutInflater.from(ctx)
    protected val binding = LayoutStatsDialogBinding.inflate(inflater)
    override lateinit var graphSpeed: IGraph
    override lateinit var graphAltitude: IGraph

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.model = model
        binding.lifecycleOwner = this
        val dlg = activity?.let {
            val builder = AlertDialog.Builder(it)

            builder
                .setView(binding.root)
                .setNeutralButton(R.string.ok) { dialog, id ->
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("")

        graphSpeed = binding.root.findGraphAndWrapIt(R.id.graph_speed)
        graphAltitude = binding.root.findGraphAndWrapIt(R.id.graph_altitude)
        model.onCreateDialog(this)

        return dlg
    }

    /**
     * Finds a graph widget and wraps it into an [IGraph].
     * @param id View identifier.
     * @return The created wrapper
     */
    private fun View.findGraphAndWrapIt(id: Int): IGraph {
        val view = findViewById<GraphView>(id)
        val fmt = object : DefaultLabelFormatter() {
            private val fmt = SimpleDateFormat("H:mm")
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if(isValueX) {
                    return fmt.format(Date(value.toLong()))
                } else {
                    return super.formatLabel(value, isValueX)
                }
            }
        }
        view.gridLabelRenderer.labelFormatter = fmt
        view.gridLabelRenderer.numHorizontalLabels = 3
        return GraphViewWrapper(view)
    }
}