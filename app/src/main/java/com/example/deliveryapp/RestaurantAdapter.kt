package com.example.deliveryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RestaurantAdapter(var list:ArrayList<Restaurant>):RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    lateinit var onItemClick:((Restaurant)->Unit)
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var image=itemView.findViewById<ImageView>(R.id.restaurantImage)
        var name:TextView=itemView.findViewById(R.id.nameRestaurant)
        var time:TextView=itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.restaurants,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var restaurantInfo=list[position]
        holder.image.setImageResource(restaurantInfo.image)
        holder.name.text=restaurantInfo.name
        holder.time.text=restaurantInfo.time
        holder.itemView.setOnClickListener {
            onItemClick.invoke(restaurantInfo)
        }
    }
}