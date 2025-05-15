package com.example.deliveryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RestaurantAdapter(
    private val context: Context,
    private val restaurantList: List<Restaurant>,
    private val listener: OnItemClickListener // 👈 نضيف listener هنا
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(restaurant: Restaurant)
    }

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.nameRestaurant)
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)

        fun bind(restaurant: Restaurant) {
            restaurantName.text = restaurant.restaurantName

            Glide.with(context)
                .load(restaurant.imageBase64)
                .into(restaurantImage)

            itemView.setOnClickListener {
                listener.onItemClick(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.restaurants, parent, false)
        return RestaurantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentRestaurant = restaurantList[position]
        holder.bind(currentRestaurant)
    }

    override fun getItemCount(): Int = restaurantList.size
}