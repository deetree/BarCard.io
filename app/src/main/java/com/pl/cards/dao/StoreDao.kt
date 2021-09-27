package com.pl.cards.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pl.cards.model.Store

@Dao
interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(store: Store)

    @Update
    fun update(store: Store)

    @Delete
    fun delete(store: Store)

    @Query("SELECT * FROM store WHERE (SELECT COUNT(*) FROM card WHERE card.store = store.id) > 0 ORDER BY name ASC")
    fun getAllStores(): LiveData<List<Store>>

}