package com.approteam.appro

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_list_item.view.*
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.net.URL


data class Sample(val name: String, val desc: String, val pic: String)
//Test data for Home.
//TODO Choose a data source. Either Inner database or a backend server
object Testdata {
    val homeTestData: MutableList<Sample> = java.util.ArrayList()

    init {
        homeTestData.add(
            Sample(
                "Idän Munajahti",
                "Itään mennään",
                "https://images.pexels.com/photos/1190298/pexels-photo-1190298.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
            )
        )
        homeTestData.add(
            Sample(
                "Hesari",
                "Tere tulemasta stadiin",
                "https://www.sqoop.co.ug/wp-content/uploads/2016/08/Modern-fashion-party.jpeg"
            )
        )
        homeTestData.add(
            Sample(
                "Lännen Nopein",
                "Mennää länteen",
                "https://images.cdn.yle.fi/image/upload//w_1200,h_800,f_auto,fl_lossy,q_auto:eco/39-5146485bb73d66ddebe.jpg"
            )
        )
        homeTestData.add(
            Sample(
                "Rata Appro",
                "Kehärata appro",
                "https://www.viisykkonen.fi/sites/default/files/field/image/lahijuna_uusi_1.jpg"
            )
        )
    }
}


class HomeViewAdapter(private val data: MutableList<Sample>, private val ctx: Context, private val listener: (Sample) -> Unit
) : RecyclerView.Adapter<HomeViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(ctx,position,data[position], listener)

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

    fun bind(ctx: Context,pos:Int ,item: Sample, listener: (Sample) -> Unit) = with(itemView) {

        cardTitle.text = item.name
        cardDesc.text = item.desc
        picasso.load(item.pic).into(cardImage)
        setOnClickListener { listener(item) }
        cardImage.transitionName = item.name
    }
}