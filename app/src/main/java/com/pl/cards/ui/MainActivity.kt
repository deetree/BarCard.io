package com.pl.cards.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.pl.cards.BuildConfig
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

        val storesList = StoresTemplate(this).storesList

        val grid = findViewById<GridView>(R.id.cardsGrid)
        val add = findViewById<FloatingActionButton>(R.id.gridAdd)
        val noCards = findViewById<MaterialTextView>(R.id.cardsNoCardsTv)

        grid.isNestedScrollingEnabled = true

        val adapter = GridAdapter(this, emptyList())
        grid.adapter = adapter

        storeViewModel.getAllStores().observe(this) { stores ->
            if (stores.isEmpty())
                noCards.visibility = View.VISIBLE
            else
                noCards.visibility = View.GONE

            adapter.setStores(stores)
            adapter.notifyDataSetChanged()
        }

        if (isAfterUpdate())
            storesList.forEach { s -> storeViewModel.insert(s) }

        add.setOnClickListener {
            startActivity(Intent(this, AddCardActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun isAfterUpdate(): Boolean {
        val VERSION_CODE = "VERSION_CODE"
        val DOESNT_EXIST = -1

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val savedVersion = sharedPref.getInt(VERSION_CODE, DOESNT_EXIST)
        val currentVersion = BuildConfig.VERSION_CODE

        /*currentVersion == savedVersion -> return false
        savedVersion == DOESNT_EXIST -> save & return true
        currentVersion > savedVersion -> save & return true*/

        return if (currentVersion == savedVersion)
            false
        else {
            sharedPref.edit().putInt(VERSION_CODE, currentVersion).apply()
            true
        }
    }
}