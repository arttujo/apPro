package com.approteam.appro.adapters

import kotlinx.android.synthetic.main.bar_list_item.view.*
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.R
import com.approteam.appro.data_models.Appro
import com.approteam.appro.fragments.BarListFragment


class ApproBarAdapter(
    private val data: List<Appro.ApproBar>,
    val ctx: Context,
    private val listener: (Appro.ApproBar) -> Unit
) : RecyclerView.Adapter<ApproBarViewHolder>() {


    private var selectedPos = -1
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ApproBarViewHolder, position: Int) {
        holder.bind(ctx, position, data[position], listener)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApproBarViewHolder {
        return ApproBarViewHolder(
            LayoutInflater.from(ctx).inflate(
                R.layout.bar_list_item,
                parent,
                false
            )
        )
    }


}


class ApproBarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(ctx: Context, pos: Int, item: Appro.ApproBar, listener: (Appro.ApproBar) -> Unit) =
        with(itemView) {
            barNameLabel.text = item.name
            barAddressLabel.text = item.address
            setOnClickListener { listener(item) }
        }
}