package com.pl.cards.repo

import android.app.Application
import android.os.AsyncTask
import com.pl.cards.model.Store

import androidx.lifecycle.LiveData

import com.pl.cards.dao.StoreDao
import com.pl.cards.database.CardsDatabase


class StoreRepo(application: Application?) {

    private var storeDao: StoreDao?
    private var allStores: LiveData<List<Store>>? = null

    init {
        val database: CardsDatabase = CardsDatabase.getInstance(application!!.applicationContext)
        storeDao = database.storeDao()
        allStores = storeDao?.getAllStores()
    }

    fun insert(store: Store) {
        InsertStoreAsync(storeDao!!).execute(store)
    }

    fun update(store: Store) {
        UpdateStoreAsync(storeDao!!).execute(store)
    }

    fun delete(store: Store) {
        DeleteStoreAsync(storeDao!!).execute(store)
    }

    fun getAllStores(): LiveData<List<Store>> {
        return allStores!!
    }

    private class InsertStoreAsync(private val dao: StoreDao) :
        AsyncTask<Store, Void?, Void?>() {
        override fun doInBackground(vararg stores: Store): Void? {
            dao.insert(stores[0])
            return null
        }
    }

    private class UpdateStoreAsync(private val dao: StoreDao) :
        AsyncTask<Store, Void?, Void?>() {
        override fun doInBackground(vararg stores: Store): Void? {
            dao.update(stores[0])
            return null
        }

    }

    private class DeleteStoreAsync(private val dao: StoreDao) :
        AsyncTask<Store, Void?, Void?>() {
        override fun doInBackground(vararg stores: Store): Void? {
            dao.delete(stores[0])
            return null
        }
    }
}