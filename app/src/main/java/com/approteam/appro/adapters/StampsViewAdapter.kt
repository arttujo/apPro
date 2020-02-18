package com.approteam.appro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.R
import com.approteam.appro.Sample
import kotlinx.android.synthetic.main.stamps_list_item.view.*


class StampsViewAdapter(val data: MutableList<Sample>,val ctx: Context, val listener: (Sample)-> Unit):RecyclerView.Adapter<StampsViewHolder>(){

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampsViewHolder {
        return StampsViewHolder(LayoutInflater.from(ctx).inflate(R.layout.stamps_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: StampsViewHolder, position: Int) = holder.bind(ctx,data[position],listener)


}




class StampsViewHolder(view: View):RecyclerView.ViewHolder(view){
    fun bind(ctx: Context,item:Sample,listener: (Sample) -> Unit)= with(itemView){
        gridItemName.text = item.name
        setOnClickListener { listener(item) }

    }
}