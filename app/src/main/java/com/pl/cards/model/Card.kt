package com.pl.cards.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("store")],
    foreignKeys = [ForeignKey(
        entity = Store::class,
        parentColumns = ["id"],
        childColumns = ["store"]
    )]
)
data class Card(
    val uniqueId: Long,
    var store: Long,
    var name: String,
    var value: String,
    var type: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
