package com.pl.cards.repo

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.pl.cards.dao.CardDao
import com.pl.cards.database.CardsDatabase
import com.pl.cards.model.Card

class CardRepo(application: Application?) {
    private var cardDao: CardDao?
    private var allCards: LiveData<List<Card>>? = null

    init {
        val database: CardsDatabase = CardsDatabase.getInstance(application!!.applicationContext)
        cardDao = database.cardDao()
        allCards = cardDao?.getAllCards()
    }

    fun insert(card: Card) {
        InsertCardAsync(cardDao!!).execute(card)
    }

    fun update(card: Card) {
        UpdateCardAsync(cardDao!!).execute(card)
    }

    fun delete(card: Card) {
        DeleteCardAsync(cardDao!!).execute(card)
    }

    fun getAllCards(): LiveData<List<Card>> {
        return allCards!!
    }

    fun getStoreCards(storeId: Long): LiveData<List<Card>> {
        return GetStoreCardsAsync(cardDao!!).execute(storeId).get()
    }

    fun getCard(id: Long): Card {
        return GetCardAsync(cardDao!!).execute(id).get()
    }

    private class InsertCardAsync(private val dao: CardDao) :
        AsyncTask<Card, Void?, Void?>() {
        override fun doInBackground(vararg cards: Card): Void? {
            dao.insert(cards[0])
            return null
        }
    }

    private class UpdateCardAsync(private val dao: CardDao) :
        AsyncTask<Card, Void?, Void?>() {
        override fun doInBackground(vararg cards: Card): Void? {
            dao.update(cards[0])
            return null
        }

    }

    private class DeleteCardAsync(private val dao: CardDao) :
        AsyncTask<Card, Void?, Void?>() {
        override fun doInBackground(vararg cards: Card): Void? {
            dao.delete(cards[0])
            return null
        }
    }

    private class GetStoreCardsAsync(private val dao: CardDao) :
        AsyncTask<Long, Void?, LiveData<List<Card>>>() {
        override fun doInBackground(vararg p0: Long?): LiveData<List<Card>> {
            return dao.getStoreCards(p0[0]!!)
        }
    }

    private class GetCardAsync(private val dao: CardDao) :
        AsyncTask<Long, Void?, Card>() {
        override fun doInBackground(vararg p0: Long?): Card {
            return dao.getCard(p0[0]!!)
        }
    }
}