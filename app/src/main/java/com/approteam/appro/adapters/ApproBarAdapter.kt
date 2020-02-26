package com.approteam.appro.adapters

import kotlinx.android.synthetic.main.bar_list_item.view.*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.R
import com.approteam.appro.data_models.Appro
import com.approteam.appro.fragments.BarListFragment


class ApproBarAdapter(private val data: List<Appro.ApproBar>, val ctx: Context) :RecyclerView.Adapter<ApproBarViewHolder>(){

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApproBarViewHolder {
        return ApproBarViewHolder(LayoutInflater.from(ctx).inflate(R.layout.bar_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ApproBarViewHolder, position: Int) = holder.bind(ctx,data[position])


}




class ApproBarViewHolder(view: View):RecyclerView.ViewHolder(view){
    fun bind(ctx: Context,item:Appro.ApproBar) = with(itemView){
        barNameLabel.text = item.name
        barAddressLabel.text = item.address
    }
}