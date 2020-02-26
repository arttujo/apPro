package com.approteam.appro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.data_models.Bar
import com.approteam.appro.R
import kotlinx.android.synthetic.main.bar_list_item.view.*

class CreateApproAdapter(private val data: List<Bar>, private val ctx: Context): RecyclerView.Adapter<CreateApproViewHolder>(){

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateApproViewHolder {
        return CreateApproViewHolder(LayoutInflater.from(ctx).inflate(R.layout.bar_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: CreateApproViewHolder, position: Int) {
        holder.bind(ctx,data[position])
    }


}




class CreateApproViewHolder(view:View) : RecyclerView.ViewHolder(view){

    fun bind(ctx: Context,item: Bar) = with(itemView){
        barNameLabel.text = item.name
        barAddressLabel.text = item.address

    }

}