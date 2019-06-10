package com.murgupluoglu.simplemusicplayer

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat

class AudioFocusHelper(var context: Context) : AudioManager.OnAudioFocusChangeListener{

    private lateinit var mFocusRequest: AudioFocusRequest
    var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun requestAudioFocus(): Boolean {

        val result: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })

                setAcceptsDelayedFocusGain(false)
                setOnAudioFocusChangeListener({ focusChange -> onAudioFocusChange(focusChange)}, Handler())
                build()
            }

            result = audioManager.requestAudioFocus(mFocusRequest)
        } else {
            result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(mFocusRequest)
        } else {
            audioManager.abandonAudioFocus(this)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        Log.e("onAudioFocusChange", focusChange.toString())
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                sendIntent(Status.Pause)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                sendIntent(Status.Resume)
            }
        }
    }

    fun sendIntent(action : String){
        val serviceIntent = Intent(context, MusicService::class.java)
        serviceIntent.action = action
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}