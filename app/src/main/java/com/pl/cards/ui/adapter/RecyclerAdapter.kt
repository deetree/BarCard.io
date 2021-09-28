package com.pl.cards.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.pl.cards.R
import com.pl.cards.model.Card
import com.pl.cards.ui.AddCardActivity
import com.pl.cards.ui.ShowCardActivity

class RecyclerAdapter(
    private val ctx: Context,
    private var cards: List<Card>
) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: MaterialTextView = view.findViewById(R.id.cardNameTv)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = cards[position].name

        viewHolder.itemView.setOnLongClickListener {
            val i = Intent(ctx, AddCardActivity::class.java)
            i.putExtra(AddCardActivity.CARD_EDIT, true)
            i.putExtra(AddCardActivity.CARD_ID, cards[position].id)
            ctx.startActivity(i)
            (ctx as Activity).overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            true
        }

        viewHolder.itemView.setOnClickListener {
            val i = Intent(ctx, ShowCardActivity::class.java)
            i.putExtra(AddCardActivity.CARD_ID, cards[position].id)
            ctx.startActivity(i)
            (ctx as Activity).overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }

    override fun getItemCount() = cards.size

    fun setCardsList(list: List<Card>) {
        cards = list
    }

}