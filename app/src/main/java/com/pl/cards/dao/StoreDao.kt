package com.pl.cards.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pl.cards.model.Store

@Dao
interface StoreDao {

    @Insert
    fun insert(store: Store)

    @Update
    fun update(store: Store)

    @Delete
    fun delete(store: Store)

    @Query("SELECT * FROM store ORDER BY name ASC")
    fun getAllStores(): LiveData<List<Store>>
}