package com.approteam.appro.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.approteam.appro.data_models.Bar
import com.approteam.appro.R
import kotlinx.android.synthetic.main.bar_list_item.view.*

class CreateApproAdapter(private val data: List<Bar>, private val ctx: Context,private val listener: (Bar) -> Unit): RecyclerView.Adapter<CreateApproViewHolder>(){

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateApproViewHolder {
        return CreateApproViewHolder(LayoutInflater.from(ctx).inflate(R.layout.bar_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: CreateApproViewHolder, position: Int) {
        holder.bind(ctx,data[position],listener)
        val item = data[position]
        holder.itemView.barCard.setOnClickListener {
            if (!item.isSelected){
                item.isSelected = true
                Log.d("DBG","Item: ${item.name} was selected")
                notifyItemChanged(position)
            } else {
                item.isSelected = false
                Log.d("DBG","Item: ${item.name} was deselected")
                notifyItemChanged(position)
            }
        }
        if (item.isSelected) holder.itemView.barCard.setBackgroundColor(Color.GREEN)
        if (!item.isSelected) holder.itemView.barCard.setBackgroundColor(Color.WHITE)

        holder.itemView.barCard.setOnLongClickListener {
            Log.d("DBG","LongPress")
            //TODO Implement Map fragment to show the location of the bar
            true
        }
    }


}
class CreateApproViewHolder(view:View) : RecyclerView.ViewHolder(view){

    fun bind(ctx: Context,item: Bar,listener: (Bar) -> Unit) = with(itemView){
        barNameLabel.text = item.name
        barAddressLabel.text = item.address
        setOnClickListener { listener(item) }
    }

}