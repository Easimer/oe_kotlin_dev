package net.easimer.surveyor.data.ui

import androidx.recyclerview.widget.DiffUtil

class RecordingDiffCallback(
    private val oldList: List<net.easimer.surveyor.data.disk.entities.Recording>,
    private val newList: List<net.easimer.surveyor.data.disk.entities.Recording>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int { return oldList.size }

    override fun getNewListSize(): Int { return newList.size }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].recId == newList[newItemPosition].recId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}