package net.easimer.surveyor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object Recorder {
    private val TAG = "Recorder"
    private var serviceIntent: Intent? = null

    @Synchronized
    fun tryStartService(ctx: Context): Boolean {
        var ret = false
        if(serviceIntent == null) {
            Log.d(TAG, "Creating service")
            Intent(ctx, RecorderService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ret = ctx.startForegroundService(it) != null
                } else {
                    ret = ctx.startService(it) != null
                }
                serviceIntent = it
            }
        }

        return ret
    }

    @Synchronized
    fun tryStopService(ctx: Context): Boolean {
        if(serviceIntent != null) {
            Log.d(TAG, "Stopping the camera service")
            ctx.stopService(serviceIntent)
            serviceIntent = null

            return true
        }
        return false
    }
}