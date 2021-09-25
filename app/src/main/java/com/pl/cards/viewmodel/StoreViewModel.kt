package com.pl.cards.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pl.cards.model.Store

import androidx.lifecycle.LiveData
import com.pl.cards.repo.StoreRepo


class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: StoreRepo = StoreRepo(application)
    private val allStores: LiveData<List<Store>> = repo.getAllStores()

    fun insert(store: Store) {
        repo.insert(store)
    }

    fun update(store: Store) {
        repo.update(store)
    }

    fun delete(store: Store) {
        repo.delete(store)
    }

    fun getAllStores(): LiveData<List<Store>> {
        return allStores
    }
}