package com.pl.cards.model

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.pl.cards.R

@Entity
data class Store(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var color: String,
    var image: Int
)
