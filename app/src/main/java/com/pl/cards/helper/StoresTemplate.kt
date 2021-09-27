package com.pl.cards.helper

import android.content.Context
import com.pl.cards.R
import com.pl.cards.model.Store

class StoresTemplate(private val ctx: Context) {
    private val biedronka = Store(1, "Biedronka", "#ffbe00", R.drawable.biedronka)
    private val lidl = Store(2, "Lidl", "#1b2f91", R.drawable.lidl)
    private val zabka = Store(3, "Żabka", "#00631f", R.drawable.zabka)
    private val delikatesy = Store(4, "Delikatesy Centrum", "#fefefe", R.drawable.delikatesy)
    private val payback = Store(5, "Payback", "#185ca5", R.drawable.payback)
    private val orlen = Store(6, "Orlen", "#d81e05", R.drawable.orlen)
    private val leclerc = Store(7, "E. Leclerc", "#fefefe", R.drawable.leclerc)

    private val rossmann = Store(8, "Rossmann", "#fefefe", R.drawable.rossmann)
    private val hebe = Store(9, "Hebe", "#ec008b", R.drawable.hebe)
    private val ccc = Store(10, "CCC", "#f26a21", R.drawable.ccc)
    private val deichmann = Store(11, "Deichmann", "#008e54", R.drawable.deichmann)
    private val moya = Store(12, "Moya", "#24346b", R.drawable.moya)
    private val auchan = Store(13, "Auchan", "#ff0015", R.drawable.auchan)
    private val empik = Store(14, "Empik", "#2b2724", R.drawable.empik)
    private val lotos = Store(15, "Lotos", "#fefefe", R.drawable.lotos)
    private val natura = Store(16, "Natura", "#7eba02", R.drawable.natura)
    private val different = Store(17, ctx.getString(R.string.other), "#379bd4", R.drawable.different)
    private val esotiq = Store(18, "Esotiq", "#fefefe", R.drawable.esotiq)

    val storesList = listOf(
        biedronka,
        lidl,
        zabka,
        delikatesy,
        payback,
        orlen,
        leclerc,
        rossmann,
        hebe,
        ccc,
        deichmann,
        moya,
        auchan,
        empik,
        lotos,
        natura,
        different,
        esotiq
    )
        .sortedBy { it.name }

}