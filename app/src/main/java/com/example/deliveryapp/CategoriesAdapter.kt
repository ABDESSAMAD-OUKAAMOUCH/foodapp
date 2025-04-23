package com.example.deliveryapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Base64

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageCategories)
        val nameText: TextView = itemView.findViewById(R.id.nameCategories)
        val container: CardView = itemView.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.categories, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category = categories[position]
        holder.nameText.text = category.name

        // عرض الصورة من base64
        val imageBytes = android.util.Base64.decode(category.imageBase64, android.util.Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imageView.setImageBitmap(bitmap)

        // تمييز الفئة المختارة
        holder.container.setCardBackgroundColor(
            if (position == selectedPosition) ContextCompat.getColor(context, R.color.orange)
            else ContextCompat.getColor(context, R.color.white)
        )
        holder.nameText.setTextColor(
            if (position == selectedPosition) ContextCompat.getColor(context, R.color.white)
            else ContextCompat.getColor(context, R.color.black)
        )

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
