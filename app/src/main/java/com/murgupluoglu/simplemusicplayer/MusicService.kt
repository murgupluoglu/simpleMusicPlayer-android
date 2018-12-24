package com.murgupluoglu.simplemusicplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder

const val NOTIFY_PAUSE = "NOTIFY_PAUSE"
const val NOTIFY_RESUME = "NOTIFY_RESUME"
const val NOTIFY_STOP = "NOTIFY_STOP"
const val NOTIFY_NEXT = "NOTIFY_NEXT"
const val NOTIFY_PREVIOUS = "NOTIFY_PREVIOUS"
const val START_SERVICE = "START_SERVICE"
const val NOTIFY_GO_TO_INDEX = "NOTIFY_GO_TO_INDEX"

const val PARAM_SONG_LIST = "PARAM_SONG_LIST"
const val PARAM_PLAY_INDEX = "PARAM_PLAY_INDEX"

class MusicService : Service() {

    var notificationGenerator: NotificationGenerator = NotificationGenerator(MainActivity::class.java)

    lateinit var songList : ArrayList<Song>
    var currentIndex = 0

    var player = Player()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            when (action) {
                START_SERVICE   -> {
                    songList = intent.getParcelableArrayListExtra<Song>(PARAM_SONG_LIST)
                    currentIndex = intent.getIntExtra(PARAM_PLAY_INDEX, 0)
                    startService()
                }
                NOTIFY_PREVIOUS -> {
                    prev()
                }
                NOTIFY_NEXT     -> {
                    next()
                }
                NOTIFY_GO_TO_INDEX -> {
                    currentIndex = intent.getIntExtra(PARAM_PLAY_INDEX, 0)
                    goToIndex()
                }
                NOTIFY_STOP     -> {
                    stop()
                }
            }
        }
        return START_NOT_STICKY
    }

    fun startService(){
        val song = getSong()
        startForeground(NOTIFICATION_ID, notificationGenerator.getNotification(this, song.title, "", "", song.imageLink))

        play()
    }

    fun next(){
        currentIndex++
        fixCurrentIndex()
        play()
    }

    fun play(){
        val song = getSong()
        player.play(song)
        notificationGenerator.updateNotification(this, notificationGenerator.getNotification(this, song.title, "", "", song.imageLink))
    }

    fun prev(){
        currentIndex--
        fixCurrentIndex()
        play()
    }

    fun goToIndex(){
        fixCurrentIndex()
        play()
    }

    fun stop(){
        player.stop()
        stopForeground(true)
    }

    fun getSong() : Song{
        return songList[currentIndex]
    }

    fun fixCurrentIndex(){
        currentIndex %= songList.size
        if (currentIndex < 0) currentIndex += songList.size
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stop()
        return super.onUnbind(intent)
    }
}