package com.approteam.appro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.data_models.Appro
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_list_item.view.*


class HomeViewAdapter(
    private val data: List<Appro>,
    private val ctx: Context,
    private val listener: (Appro) -> Unit
) : RecyclerView.Adapter<HomeViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(ctx, position, data[position], listener)

    }

    fun test() {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(ctx).inflate(
                R.layout.home_list_item,
                parent,
                false
            )
        )
    }
}

//View holder for the recycler view card items
class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val picasso = Picasso.get()

    fun bind(ctx: Context, pos: Int, item: Appro, listener: (Appro) -> Unit) = with(itemView) {
        cardTitle.text = item.name
        cardDesc.text = item.description
        picasso.load(item.image).into(cardImage)
        setOnClickListener { listener(item) }
        cardImage.transitionName = item.image
        cardTitle.transitionName = item.name
        cardDesc.transitionName = item.description
    }
}