package com.pl.cards.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.pl.cards.R
import com.pl.cards.helper.StoresTemplate
import com.pl.cards.ui.adapter.GridAdapter
import com.pl.cards.viewmodel.StoreViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        val storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        val storesArray = StoresTemplate().storesArray


        val grid = findViewById<GridView>(R.id.cardsGrid)
        val add = findViewById<ImageButton>(R.id.gridToolbarAdd)

        val adapter = GridAdapter(this, storesArray)
        grid.adapter = adapter

        storeViewModel.getAllStores().observe(this) { stores ->
            for (i in storesArray.indices) {
                if (!stores.stream()
                        .filter { o -> o.name.lowercase() == storesArray[i].name.lowercase() }
                        .findFirst()
                        .isPresent
                )
                    storeViewModel.insert(storesArray[i])
            }

            adapter.notifyDataSetChanged()
        }

        add.setOnClickListener {
            startActivity(Intent(this, AddCardActivity::class.java))
        }
    }
}