package com.murgupluoglu.simplemusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget


const val NOTIFICATION_ID = 99

class NotificationGenerator(var notificationIntentClass: Class<*>) {

    private var manager: NotificationManager? = null
    private var channel: NotificationChannel? = null

    private val channelId = "com.murgupluoglu.notificationdemo"
    private val channelName = "Test notification"

    private val title = "Music Player"
    private val content = "Control Audio"
    private val ticker = "Music Player Playing Now."
    lateinit var notificationTargetSmall : NotificationTarget
    lateinit var notificationTargetBig : NotificationTarget


    fun getNotification(context: Context, songTitle: String = "Song Title", artistName: String = "Artist Name", albumName: String = "Album Name", albumLink : String, isPlaying : Boolean = false) : Notification {

        // Using RemoteViews to bind custom layouts into Notification
        val smallView = RemoteViews(context.packageName, R.layout.status_bar)
        val bigView = RemoteViews(context.packageName, R.layout.status_bar_expanded)


        // showing default album image
        smallView.setImageViewBitmap(R.id.status_bar_icon, BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        bigView.setImageViewBitmap(R.id.status_bar_album_art, BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        setListeners(bigView, smallView, context, songTitle, artistName, albumName, isPlaying)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(context,
            title,
            content,
            R.mipmap.ic_launcher,
            ticker)

        // Notification through notification manager
        lateinit var notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nBuilder.setCustomBigContentView(bigView)
            nBuilder.setCustomContentView(smallView)
            notification = nBuilder.build()
        } else {
            notification = nBuilder.build()
            notification.contentView = smallView
            notification.bigContentView = bigView
        }

        notificationTargetSmall = NotificationTarget(
            context,
            R.id.status_bar_icon,
            smallView,
            notification,
            NOTIFICATION_ID
        )
        notificationTargetBig = NotificationTarget(
            context,
            R.id.status_bar_album_art,
            bigView,
            notification,
            NOTIFICATION_ID
        )

        loadArtistImage(context, albumLink)

        // Notification through notification manager
        notification.flags = Notification.FLAG_ONGOING_EVENT or Notification.FLAG_ONLY_ALERT_ONCE

        return notification
    }

    private fun loadArtistImage(context: Context, artistLink : String){
        val options = RequestOptions()
           // transforms(CenterCrop(), RoundedCorners(32))
            .error(R.mipmap.ic_launcher)

        Glide.with(context)
            .asBitmap()
            .load(artistLink)
            .apply(options)
            .into(notificationTargetSmall)

        Glide.with(context)
            .asBitmap()
            .load(artistLink)
            .apply(options)
            .into(notificationTargetBig)
    }

    fun cancelNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun updateNotification(context: Context, notification : Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun setListeners(bigView: RemoteViews, smallView: RemoteViews, context: Context, songTitle: String, artistName: String, albumName: String, isPlaying: Boolean) {

        bigView.setOnClickPendingIntent(R.id.status_bar_prev, createPendingIntent(context, Status.Previous))
        smallView.setOnClickPendingIntent(R.id.status_bar_prev, createPendingIntent(context, Status.Previous))

        //bigView.setOnClickPendingIntent(R.id.status_bar_collapse, createPendingIntent(context, NOTIFY_STOP))
        //smallView.setOnClickPendingIntent(R.id.status_bar_collapse, createPendingIntent(context, NOTIFY_STOP))

        bigView.setOnClickPendingIntent(R.id.status_bar_next, createPendingIntent(context, Status.Next))
        smallView.setOnClickPendingIntent(R.id.status_bar_next, createPendingIntent(context, Status.Next))


        if(isPlaying){
            bigView.setInt(R.id.status_bar_play, "setImageResource", R.drawable.ic_pause)
            smallView.setInt(R.id.status_bar_play, "setImageResource", R.drawable.ic_pause)

            bigView.setOnClickPendingIntent(R.id.status_bar_play, createPendingIntent(context, Status.Pause))
            smallView.setOnClickPendingIntent(R.id.status_bar_play, createPendingIntent(context, Status.Pause))
        }else{
            bigView.setInt(R.id.status_bar_play, "setImageResource", R.drawable.ic_play)
            smallView.setInt(R.id.status_bar_play, "setImageResource", R.drawable.ic_play)

            bigView.setOnClickPendingIntent(R.id.status_bar_play, createPendingIntent(context, Status.Resume))
            smallView.setOnClickPendingIntent(R.id.status_bar_play, createPendingIntent(context, Status.Resume))
        }

        bigView.setTextViewText(R.id.status_bar_track_name, songTitle)
        smallView.setTextViewText(R.id.status_bar_track_name, songTitle)

        bigView.setTextViewText(R.id.status_bar_artist_name, artistName)
        smallView.setTextViewText(R.id.status_bar_artist_name, artistName)

        bigView.setTextViewText(R.id.status_bar_album_name, albumName)
    }

    private fun createPendingIntent(context: Context, action: String): PendingIntent {
        val intentPlay = Intent(context, MusicService::class.java)
        intentPlay.action = action
        return PendingIntent.getService(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Initialize the notification manager and channel Id.
     * The notification builder has the basic initialization:
     *     - AutoCancel=true
     *     - LargeIcon = SmallIcon
     * @param [context] application context for associate the notification with.
     * @param [notificationTitle] notification title.
     * @param [notificationText] notification text.
     * @param [notificationIconId] notification icon id from application resource.
     * @param [notificationTicker] notification ticker text for accessibility.
     * @return the PendingIntent to be used on this notification.
     */
    private fun getNotificationBuilder(context: Context,
                                       notificationTitle: String,
                                       notificationText: String,
                                       notificationIconId: Int,
                                       notificationTicker: String): NotificationCompat.Builder {
        // Define the notification channel for newest Android versions
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = getPendingIntent(context)
        val builder = NotificationCompat.Builder(context, channelId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (null == channel) {
                channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
                channel?.apply {
                    enableLights(true)
                    lightColor = Color.GREEN
                    enableVibration(false)
                    manager?.createNotificationChannel(this)
                }

            }
        }
        // Build the content of the notification
        builder.setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(notificationIconId)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, notificationIconId))
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setTicker(notificationTicker)
            .setOnlyAlertOnce(true)

        // Restricts the notification information when the screen is blocked.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PRIVATE)
        }
        return builder
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val resultIntent = Intent(context, notificationIntentClass)
        resultIntent.action = Intent.ACTION_MAIN
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        return PendingIntent.getActivity(context, 0, resultIntent, 0)
    }
}