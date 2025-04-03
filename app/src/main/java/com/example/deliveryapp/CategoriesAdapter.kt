package com.example.deliveryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class CategoriesAdapter(var categoriesList:ArrayList<Categories>):RecyclerView.Adapter<CategoriesAdapter.ViewHolder>(){
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var image=itemView.findViewById<CircleImageView>(R.id.imageCategories)
        var name=itemView.findViewById<TextView>(R.id.nameCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =LayoutInflater.from(parent.context).inflate(R.layout.categories,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var categoriesInfo=categoriesList[position]
        holder.image.setImageResource(categoriesInfo.image)
        holder.name.text=categoriesInfo.name
    }

}