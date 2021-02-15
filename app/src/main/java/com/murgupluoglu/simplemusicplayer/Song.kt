package com.murgupluoglu.simplemusicplayer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(val streamLink: String, val imageLink: String, val title: String) : Parcelable