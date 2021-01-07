package net.easimer.surveyor.trackpointsource

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import net.easimer.surveyor.data.RecordingRepository

class MapTrackpointSourceFactory {
    companion object {
        fun make(ctx: Context): IMapTrackpointSource {
            return LiveMapTrackpointSource(ctx)
        }

        fun make(owner: LifecycleOwner, repo: RecordingRepository, recID: Long): IMapTrackpointSource {
            return ReplayMapTrackpointSource(owner, repo, recID)
        }
    }
}