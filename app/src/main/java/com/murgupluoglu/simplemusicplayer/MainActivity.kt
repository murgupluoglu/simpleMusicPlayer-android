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

        songList.add(Song("https://radyo.dogannet.tv/slowturk", "https://i2.cnnturk.com/i/cnnturk/75/0x0/57834bf8f0dc1e53e4fccb9d.jpg", "Slow Türk"))
        songList.add(Song("https://radyo.dogannet.tv/cnnturk", "https://cdn1.kralmuzik.com.tr/media/content/19-05/17/kralfm.png", "Cnn Turk"))
        songList.add(Song("https://radyo.dogannet.tv/radyod", "https://pbs.twimg.com/profile_images/1208471733440831492/rXdme36C_400x400.jpg", "Radyo D"))
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
