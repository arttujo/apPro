package com.approteam.appro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.R
import com.approteam.appro.data_models.Appro
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_list_item.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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

    private fun dateParser(date:String): String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val dateTime = LocalDateTime.parse(date,formatter)
        val formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return dateTime.format(formatter2)
    }

    fun bind(ctx: Context, pos: Int, item: Appro, listener: (Appro) -> Unit) = with(itemView) {
        cardTitle.text = item.name
        cardDesc.text = item.description
        picasso.load(item.image).into(cardImage)
        setOnClickListener { listener(item) }
        cardImage.transitionName = item.image
        cardTitle.transitionName = item.name
        cardDesc.transitionName = item.description
        cardPrice.text = item.price.toString() +" €"
        val date = item.date
        cardDate.text = dateParser(date!!)
    }
}