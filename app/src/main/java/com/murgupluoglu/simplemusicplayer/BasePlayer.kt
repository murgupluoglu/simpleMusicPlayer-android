package com.murgupluoglu.simplemusicplayer

/*
*  Created by Mustafa Ürgüplüoğlu on 11.02.2021.
*  Copyright © 2021 Mustafa Ürgüplüoğlu. All rights reserved.
*/

abstract class BasePlayer {

    abstract fun play(song : Song)
    abstract fun pause()
    abstract fun stop()
    abstract fun resume()
}