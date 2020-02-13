package com.approteam.appro

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView





data class sample(val name:String,val desc: String, val pic: Uri)


class HomeViewAdapter(val data: List<sample>,val ctx:Context, val listener: (sample)->Unit):RecyclerView.Adapter<HomeViewHolder>(){

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) = holder.bind(ctx,data[position],listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(ctx).inflate(R.layout.home_list_item,parent,false))
    }


}

class HomeViewHolder(view:View):RecyclerView.ViewHolder(view){

    fun bind(ctx: Context,item:sample,listener: (sample) -> Unit) = with(itemView){

    }
}