package net.easimer.surveyor.data.ui

import androidx.recyclerview.widget.DiffUtil

class RecordingDiffCallback : DiffUtil.ItemCallback<Recording>() {
    override fun areItemsTheSame(oldItem: Recording, newItem: Recording): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Recording, newItem: Recording): Boolean {
        return oldItem == newItem
    }
}