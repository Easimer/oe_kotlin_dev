package net.easimer.surveyor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * A class that manages the UI notification for the service.
 *
 * @param ctx The service
 */
class RecorderNotification(private val ctx: Service) {
    /**
     * Stop intent: this is sent to [MainActivity] when the user presses the stop button on the
     * notification.
     */
    private val stopIntent = Intent(ctx, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        .let {
            it.putExtra(MainActivity.EXTRA_REQUEST, MainActivity.REQUEST_STOP_RECORDING)
            PendingIntent.getActivity(
                ctx,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    private val actTerminate =
        NotificationCompat.Action.Builder(R.drawable.stop, ctx.getText(R.string.stop_recording), stopIntent)
            .build()
    private val notificationBuilderTemplate = makeNotificationBuilder()
        .setContentTitle(ctx.getText(R.string.notification_recording))
        .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: temp icon
        .addAction(actTerminate)
        .setOngoing(true)

    private fun makeNotificationBuilder(): NotificationCompat.Builder {
        val chanId = "net.easimer.surveyor.notifychan.recorder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifyMan = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            makeNotificationChannel(notifyMan, chanId)
            return NotificationCompat.Builder(ctx, chanId)
        } else {
            return NotificationCompat.Builder(ctx)
        }
    }

    /**
     * Creates and shows the notification.
     */
    fun create() {
        val builder = notificationBuilderTemplate
        ctx.startForeground(1, builder.build())
    }

    /**
     * Removes the notification.
     */
    fun remove() {
        ctx.stopForeground(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannel(notifyMan: NotificationManager, chanId: String) {
        // We need to create a notification channel on newer Android systems.
        val maybeChannel = notifyMan.getNotificationChannel(chanId)
        if(maybeChannel == null) {
            val channel =
                NotificationChannel(chanId, chanId, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Surveyor Service Channel"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            notifyMan.createNotificationChannel(channel)
        }
    }
}
