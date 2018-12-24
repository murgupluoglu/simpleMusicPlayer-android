package com.murgupluoglu.simplemusicplayer

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import java.io.IOException
import android.media.AudioAttributes
import android.os.Build


class Player {

    var isPrepared = false
    var mediaPlayer = MediaPlayer()

    var notificationGenerator: NotificationGenerator = NotificationGenerator(MainActivity::class.java)

    fun play(context: Context, song: Song){

        notificationGenerator.showNotification(context, song.title, "", "", song.imageLink)

        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()

        isPrepared = false
        mediaPlayer = MediaPlayer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attr= AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

            mediaPlayer.setAudioAttributes(attr)
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }


        try{
            mediaPlayer.setDataSource(song.streamLink)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { mp ->
                isPrepared = true
                mp.start()
            }
            mediaPlayer.setOnErrorListener { mediaPlayer, i, k ->
                isPrepared = false
                false
            }
        }catch (e : IOException){
            e.printStackTrace()
        }

    }

    fun stop(context: Context){
        if(isPrepared && mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        notificationGenerator.cancelNotification(context)
    }
}