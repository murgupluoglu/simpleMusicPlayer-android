package com.murgupluoglu.simplemusicplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build


class Player {

    var isPrepared = false
    var mediaPlayer = MediaPlayer()


    fun play(song: Song){
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
            mediaPlayer.setDataSource(song.streamLink.trim())
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { mp ->
                isPrepared = true
                mp.start()
            }
            mediaPlayer.setOnErrorListener { mediaPlayer, i, k ->
                isPrepared = false
                false
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    fun stop(){
        if(isPrepared && mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }

    fun pause(){
        if(isPrepared && mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }
    }

    fun resume(){
        if(isPrepared && !mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }
}