package com.murgupluoglu.simplemusicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    val songList = arrayListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songList.add(Song("http://46.20.7.126/;stream.mp3", "https://pbs.twimg.com/profile_images/684004364927811585/yoDiKjFL.png", "Best Fm"))
        songList.add(Song("http://37.247.98.16/;", "https://cdn.powergroup.com.tr/powerapp/channels/big/powerTurk.png", "PowerTurk"))
        songList.add(Song("http://46.20.3.204/;", "https://cdn1.kralmuzik.com.tr/media/content/19-05/17/kralfm.png", "Kral Fm"))
        songList.add(Song("http://radyo.dogannet.tv/slowturk", "https://www.canliradyodinle.fm/wp-content/uploads/radyo-slow-turk.jpg", "Slow Türk"))
    }

    fun startSong(view : View){
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra(PARAM_SONG_LIST, songList)
        intent.putExtra(PARAM_PLAY_INDEX, 0)
        intent.action = Status.Start
        ContextCompat.startForegroundService(this, intent)
    }

    fun nextSong(view : View){
        sendIntent(Status.Next)
    }

    fun prevSong(view : View){
        sendIntent(Status.Previous)
    }

    fun goToIndex(view : View){
        val serviceIntent = Intent(this, MusicService::class.java)
        serviceIntent.action = Status.GoToIndex
        intent.putExtra(PARAM_PLAY_INDEX, 1)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopSong(view : View){
        sendIntent(Status.Stop)
    }

    fun sendIntent(action : String){
        val serviceIntent = Intent(this, MusicService::class.java)
        serviceIntent.action = action
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun disconnectService(){
        sendIntent(Status.Stop)
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectService()
    }
}
