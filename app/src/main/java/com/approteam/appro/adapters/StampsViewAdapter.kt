package com.approteam.appro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.data_models.Appro
import com.approteam.appro.R

import kotlinx.android.synthetic.main.stamps_list_item.view.*


class StampsViewAdapter(val data: List<Appro.ApproBar>, val ctx: Context, val listener: (Appro.ApproBar)-> Unit):RecyclerView.Adapter<StampsViewHolder>(){

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampsViewHolder {
        return StampsViewHolder(LayoutInflater.from(ctx).inflate(R.layout.stamps_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: StampsViewHolder, position: Int) = holder.bind(ctx,data[position],listener)


}




class StampsViewHolder(view: View):RecyclerView.ViewHolder(view){
    fun bind(ctx: Context, item: Appro.ApproBar, listener: (Appro.ApproBar) -> Unit)= with(itemView){
        gridItemName.text = item.name
        if (item.visited) {
            gridItemName.visibility = View.GONE
            stampApproved.visibility = View.VISIBLE
        }
        setOnClickListener { listener(item) }

    }
}