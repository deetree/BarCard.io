package com.pl.cards.helper

import com.pl.cards.R
import com.pl.cards.model.Store

class StoresTemplate {
    private val biedronka = Store(1, "Biedronka", "#f7b800", R.drawable.biedronka)
    private val lidl = Store(2, "Lidl", "#f7b800", R.drawable.lidl)

    val storesList = listOf(biedronka, lidl)

}