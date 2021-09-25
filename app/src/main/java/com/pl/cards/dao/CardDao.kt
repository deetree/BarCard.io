package com.pl.cards.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pl.cards.model.Card

@Dao
interface CardDao {

    @Insert
    fun insert(card: Card)

    @Update
    fun update(card: Card)

    @Delete
    fun delete(card: Card)

    @Query("SELECT * FROM card")
    fun getAllCards(): LiveData<List<Card>>

    @Query("SELECT * FROM card WHERE card.store = :storeId ORDER BY card.name ASC")
    fun getStoreCards(storeId: Long): LiveData<List<Card>>

    @Query("SELECT * FROM card WHERE card.id = :id")
    fun getCard(id: Long): Card
}