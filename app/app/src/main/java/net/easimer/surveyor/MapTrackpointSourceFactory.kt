package net.easimer.surveyor

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import net.easimer.surveyor.data.RecordingRepository

class MapTrackpointSourceFactory {
    companion object {
        fun make(ctx: Context): MapTrackpointSource {
            return LiveMapTrackpointSource(ctx)
        }

        fun make(owner: LifecycleOwner, repo: RecordingRepository, recID: Long): MapTrackpointSource {
            return ReplayMapTrackpointSource(owner, repo, recID)
        }
    }
}