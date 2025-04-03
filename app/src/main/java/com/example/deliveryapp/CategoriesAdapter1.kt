package com.example.deliveryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class CategoriesAdapter1(var categoriesList1:ArrayList<Categories1>):RecyclerView.Adapter<CategoriesAdapter1.ViewHolder>(){
    lateinit var onItemClick:((Categories1)->Unit)
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var image=itemView.findViewById<ImageView>(R.id.imageCategories1)
        var name=itemView.findViewById<TextView>(R.id.nameCategories1)
        var price=itemView.findViewById<TextView>(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =LayoutInflater.from(parent.context).inflate(R.layout.categories1,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoriesList1.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var categoriesInfo=categoriesList1[position]
        holder.image.setImageResource(categoriesInfo.image)
        holder.name.text=categoriesInfo.name
        holder.price.text=categoriesInfo.price
        holder.itemView.setOnClickListener {
            onItemClick.invoke(categoriesInfo)
        }
    }

}