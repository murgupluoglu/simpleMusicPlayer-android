package com.murgupluoglu.simplemusicplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder

object Status{
    const val Pause = "NOTIFY_PAUSE"
    const val Resume = "NOTIFY_RESUME"
    const val Stop = "NOTIFY_STOP"
    const val Next = "NOTIFY_NEXT"
    const val Previous = "NOTIFY_PREVIOUS"
    const val Start = "START_SERVICE"
    const val GoToIndex = "NOTIFY_GO_TO_INDEX"
}

const val PARAM_SONG_LIST = "PARAM_SONG_LIST"
const val PARAM_PLAY_INDEX = "PARAM_PLAY_INDEX"

class MusicService : Service() {

    var playerFocusHelper: AudioFocusHelper? = null
    var notificationGenerator: NotificationGenerator = NotificationGenerator(MainActivity::class.java)

    lateinit var songList: ArrayList<Song>
    var currentIndex = 0

    var player = Player()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            when (action) {
                Status.Start -> {
                    songList = intent.getParcelableArrayListExtra<Song>(PARAM_SONG_LIST)
                    currentIndex = intent.getIntExtra(PARAM_PLAY_INDEX, 0)
                    startService()
                }
                Status.Previous -> {
                    prev()
                }
                Status.Next -> {
                    next()
                }
                Status.GoToIndex -> {
                    currentIndex = intent.getIntExtra(PARAM_PLAY_INDEX, 0)
                    goToIndex()
                }
                Status.Stop -> {
                    stop()
                }
                Status.Pause -> {
                    pause()
                }
                Status.Resume -> {
                    resume()
                }
            }
        }
        return START_NOT_STICKY
    }

    fun startService() {
        playerFocusHelper = AudioFocusHelper(this)
        playerFocusHelper!!.requestAudioFocus()

        val song = getSong()
        startForeground(NOTIFICATION_ID, notificationGenerator.getNotification(this, song.title, "", "", song.imageLink, true))

        play()
    }

    fun next() {
        currentIndex++
        fixCurrentIndex()
        play()
    }

    fun play() {
        val song = getSong()
        player.play(song)
        notificationGenerator.updateNotification(this, notificationGenerator.getNotification(this, song.title, "", "", song.imageLink, true))
    }

    fun pause(){
        player.pause()
        val song = getSong()
        notificationGenerator.updateNotification(this, notificationGenerator.getNotification(this, song.title, "", "", song.imageLink, false))
    }

    fun resume(){
        player.resume()
        val song = getSong()
        notificationGenerator.updateNotification(this, notificationGenerator.getNotification(this, song.title, "", "", song.imageLink, true))
    }

    fun prev() {
        currentIndex--
        fixCurrentIndex()
        play()
    }

    fun goToIndex() {
        fixCurrentIndex()
        play()
    }

    fun stop() {
        player.stop()
        playerFocusHelper?.abandonAudioFocus()
        stopSelf()
    }

    fun getSong(): Song {
        return songList[currentIndex]
    }

    fun fixCurrentIndex() {
        currentIndex %= songList.size
        if (currentIndex < 0) currentIndex += songList.size
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stop()
        return super.onUnbind(intent)
    }
}