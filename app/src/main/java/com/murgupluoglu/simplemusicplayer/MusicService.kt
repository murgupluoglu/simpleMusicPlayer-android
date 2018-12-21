package com.murgupluoglu.simplemusicplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

const val NOTIFY_STOP = "NOTIFY_STOP"
const val NOTIFY_NEXT = "NOTIFY_NEXT"
const val NOTIFY_PREVIOUS = "NOTIFY_PREVIOUS"
const val START_SERVICE = "START_SERVICE"
const val NOTIFY_GO_TO_INDEX = "NOTIFY_GO_TO_INDEX"

const val PARAM_SONG_LIST = "PARAM_SONG_LIST"
const val PARAM_PLAY_INDEX = "PARAM_PLAY_INDEX"

class MusicService : Service() {

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
                    initService()
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

    fun initService(){
        player.play(this, getSong())
    }

    fun next(){
        currentIndex++
        fixCurrentIndex()
        Log.e("CURRENT INDEX", "$currentIndex")
        player.play(this, getSong())
    }

    fun prev(){
        currentIndex--
        fixCurrentIndex()
        Log.e("CURRENT INDEX", "$currentIndex")
        player.play(this, getSong())
    }

    fun goToIndex(){
        fixCurrentIndex()
        Log.e("CURRENT INDEX", "$currentIndex")
        player.play(this, getSong())
    }

    fun stop(){
        player.stop(this)
    }

    fun getSong() : Song{
        return songList[currentIndex]
    }

    fun fixCurrentIndex(){
        currentIndex %= songList.size
        if (currentIndex < 0) currentIndex += songList.size
    }

    override fun onUnbind(intent: Intent?): Boolean {
        player.stop(this)
        return super.onUnbind(intent)
    }
}