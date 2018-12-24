package com.murgupluoglu.simplemusicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var playerFocusHelper: AudioFocusHelper? = null

    val serviceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(p0: ComponentName?) {

        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

        }
    }

    val songList = arrayListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerFocusHelper = AudioFocusHelper(this)
        playerFocusHelper!!.requestAudioFocus()


        songList.add(Song("http://37.247.98.16/;", "https://cdn.weber.emrg.me/Radio/be59e071-4eef-4f00-ad1a-67af0ca6cd28.png", "PowerTurk"))
        songList.add(Song("http://46.20.7.126/;stream.mp3", "https://cdn.weber.emrg.me/Radio/d27cd91a-9c7e-451f-a3f3-a6002ca1a01f.png", "Best Fm"))
        songList.add(Song("http://46.20.3.204/;", "https://cdn.weber.emrg.me/Radio/e86c5632-8844-4a1e-bec9-d4681575d46b.png", "Kral Fm"))
        songList.add(Song("http://radyo.dogannet.tv/slowturk", "https://cdn.weber.emrg.me/Radio/dc7b965a-fd0c-4233-b166-9fd22fb51dff.png", "Slow Türk"))
    }

    fun startSong(view : View){
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra(PARAM_SONG_LIST, songList)
        intent.putExtra(PARAM_PLAY_INDEX, 0)
        intent.action = START_SERVICE
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun nextSong(view : View){
        sendIntent(NOTIFY_NEXT)
    }

    fun prevSong(view : View){
        sendIntent(NOTIFY_PREVIOUS)
    }

    fun goToIndex(view : View){
        val serviceIntent = Intent(this, MusicService::class.java)
        serviceIntent.action = NOTIFY_GO_TO_INDEX
        intent.putExtra(PARAM_PLAY_INDEX, 1)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopSong(view : View){
        sendIntent(NOTIFY_STOP)
    }

    fun sendIntent(action : String){
        val serviceIntent = Intent(this, MusicService::class.java)
        serviceIntent.action = action
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun disconnectService(){
        unbindService(serviceConnection)
        playerFocusHelper?.abandonAudioFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectService()

    }
}
