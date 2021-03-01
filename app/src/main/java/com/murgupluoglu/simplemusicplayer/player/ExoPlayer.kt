package com.murgupluoglu.simplemusicplayer.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/*
*  Created by Mustafa Ürgüplüoğlu on 11.02.2021.
*  Copyright © 2021 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class ExoPlayer(val context: Context) : BasePlayer() {

    private val exoPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
    }

    override fun play(song: Song) {
        with(exoPlayer) {
            setMediaSource(buildHlsMediaSource(song.streamLink),
                    true
            )
            prepare()
            playWhenReady = true
        }
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun resume() {
        exoPlayer.playWhenReady = true
    }

    /**
     * Build HLS Media Source
     */
    private fun buildHlsMediaSource(url: String): MediaSource {
        val mediaDataSourceFactory =
                DefaultDataSourceFactory(context, Util.getUserAgent(context, "mediaPlayerSample"))
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val mediaSource = HlsMediaSource.Factory(mediaDataSourceFactory)
                .createMediaSource(mediaItem)
        return mediaSource
    }
}