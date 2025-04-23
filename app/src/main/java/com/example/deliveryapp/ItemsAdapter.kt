package com.example.deliveryapp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64

class ItemAdapter(
    private val context: Context,
    private val itemList: List<Item>,
    private val restaurantId: String,
    private val categoryId: String
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.imageItem)
        val itemName: TextView = itemView.findViewById(R.id.nameItem)
        val itemPrice: TextView = itemView.findViewById(R.id.price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.categories1, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.itemName
        holder.itemPrice.text = "${item.price}$"

        val imageBytes = android.util.Base64.decode(item.imageBase64, android.util.Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.itemImage.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            // Create an intent to navigate to the details page (replace `ItemDetailsActivity::class.java` with your target Activity)
            val intent = Intent(context, Order::class.java)
            intent.putExtra("ITEM_IMAGE_BASE64", item.imageBase64)
            intent.putExtra("itemName", item.itemName)  // Pass the item data (e.g., item ID) to the next page
            intent.putExtra("price", "${item.price}$")  // Pass the item data (e.g., item ID) to the next page
            intent.putExtra("description", item.description)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("categoryId", categoryId)
            // Pass the item data (e.g., item ID) to the next page
            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int = itemList.size
}