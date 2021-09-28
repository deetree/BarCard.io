package com.pl.cards.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.google.android.material.card.MaterialCardView
import com.pl.cards.R
import com.pl.cards.model.Store
import com.pl.cards.ui.StoreCardsActivity

class GridAdapter(private val ctx: Context, private var stores: List<Store>) : BaseAdapter() {

    fun setStores(stores: List<Store>) {
        this.stores = stores
    }

    override fun getCount(): Int {
        return stores.size
    }

    override fun getItem(p0: Int): Store {
        return stores[p0]
    }

    override fun getItemId(p0: Int): Long {
        return stores[p0].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val store = getItem(position)
        var myView = convertView
        val holder: ViewHolder

        if (myView == null) {
            val mInflater = (ctx as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.grid_item, parent, false)
            holder = ViewHolder()

            holder.logo = myView!!.findViewById(R.id.gridItemLogoImg) as ImageView
            holder.card = myView.findViewById(R.id.gridCard)

            myView.tag = holder
        } else {
            holder = myView.tag as ViewHolder
        }

        holder.logo!!.setImageResource(store.image)
        holder.card!!.setCardBackgroundColor(Color.parseColor(store.color))

        holder.card!!.setOnClickListener {
            val i = Intent(ctx, StoreCardsActivity::class.java)
            i.putExtra(StoreCardsActivity.STORE_ID, store.id)
            i.putExtra(StoreCardsActivity.STORE_NAME, store.name)
            ctx.startActivity(i)
            (ctx as Activity).overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }

        return myView
    }

    class ViewHolder {
        var logo: ImageView? = null
        var card: MaterialCardView? = null
    }
}