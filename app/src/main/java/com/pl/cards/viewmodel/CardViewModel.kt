package com.pl.cards.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.pl.cards.model.Card
import com.pl.cards.repo.CardRepo

class CardViewModel (application: Application) : AndroidViewModel(application) {
    private val repo: CardRepo = CardRepo(application)
    private val allCards: LiveData<List<Card>> = repo.getAllCards()

    fun insert(card: Card) {
        repo.insert(card)
    }

    fun update(card: Card) {
        repo.update(card)
    }

    fun delete(card: Card) {
        repo.delete(card)
    }

    fun getAllCards(): LiveData<List<Card>> {
        return allCards
    }

    fun getStoreCards(storeId: Long): LiveData<List<Card>> {
        return repo.getStoreCards(storeId)
    }

    fun getCard(id: Long): Card {
        return repo.getCard(id)
    }

    fun getCardsCountByNumber(value: String) : Int {
        return repo.getCardsCountByNumber(value)
    }
}