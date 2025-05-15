package com.example.deliveryapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(
    private val context: Context,
    private var itemList: List<Item>
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.nameItem)
        val itemPrice: TextView = itemView.findViewById(R.id.price)
        val itemImage: ImageView = itemView.findViewById(R.id.imageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categories1, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            itemName.text = item.itemName
            itemPrice.text = "%.2f MAD".format(item.price)

            try {
                val imageBytes = android.util.Base64.decode(item.imageBase64, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                itemImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                itemImage.setImageResource(R.drawable.pizza2)
            }
        }
    }

    override fun getItemCount() = itemList.size

    fun updateList(newList: List<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }
}