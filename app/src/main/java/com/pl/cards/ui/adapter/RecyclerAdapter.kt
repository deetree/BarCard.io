package com.pl.cards.ui.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.pl.cards.R
import com.pl.cards.model.Card
import com.pl.cards.ui.AddCardActivity
import com.pl.cards.ui.ShowCardActivity
import com.pl.cards.viewmodel.CardViewModel

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
            true
        }

        viewHolder.itemView.setOnClickListener {
            val i = Intent(ctx, ShowCardActivity::class.java)
            i.putExtra(AddCardActivity.CARD_ID, cards[position].id)
            ctx.startActivity(i)
        }
    }

    override fun getItemCount() = cards.size

    fun setCardsList(list: List<Card>) {
        cards = list
    }

}