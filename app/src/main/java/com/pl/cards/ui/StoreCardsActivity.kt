package com.pl.cards.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pl.cards.R
import com.pl.cards.ui.adapter.RecyclerAdapter
import com.pl.cards.viewmodel.CardViewModel

class StoreCardsActivity : AppCompatActivity() {
    companion object {
        const val STORE_ID = "com.pl.cards.store_id"
        const val STORE_NAME = "com.pl.cards.store_name"
    }

    lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_cards)

        val cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

        val storeId = intent.getLongExtra(STORE_ID, -1)
        val storeName = intent.getStringExtra(STORE_NAME)

        supportActionBar?.title = storeName

        //val cards = cardViewModel.getStoreCards(storeId)
        adapter = RecyclerAdapter(this, emptyList(), cardViewModel)

        val recycler = findViewById<RecyclerView>(R.id.cardsRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(false)

        cardViewModel.getStoreCards(storeId).observe(this) { cards ->
            adapter.setCardsList(cards)
            adapter.notifyDataSetChanged()
        }



        recycler.adapter = adapter
    }

}