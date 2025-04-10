package com.example.deliveryapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.models.SlideModel
import com.example.deliveryapp.databinding.ActivityRestaurantBinding

class RestaurantActivity : AppCompatActivity() {
    var categoriesList=ArrayList<Categories>()
    var categoriesList1=ArrayList<Categories1>()
    lateinit var binding: ActivityRestaurantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        var restaurantName=intent.getStringExtra("restaurantName")
        var imageBase64=intent.getStringExtra("imageUrl")
        var restaurantUrl=intent.getStringExtra("restaurantUrl")
//image
        Glide.with(this).load(imageBase64).into(binding.imageView12)
        binding.nameRestaurant.text=restaurantName

//floatingactionbutton
        binding.floatingactionbutton.setOnClickListener {
            val url = restaurantUrl // استبدل بالرابط المطلوب
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        binding.imageView2.setOnClickListener {
            finish()
        }

        if (categoriesList.isEmpty()){
            categoriesList.add(Categories(R.drawable.pizza2,"Pizza"))
            categoriesList.add(Categories(R.drawable.burger2,"Burger"))
            categoriesList.add(Categories(R.drawable.burger2,"Burger"))
            categoriesList.add(Categories(R.drawable.burger2,"Burger"))
            categoriesList.add(Categories(R.drawable.burger2,"Burger"))
        }
        if (categoriesList1.isEmpty()){
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
            categoriesList1.add(Categories1(R.drawable.burger2,"Burger","$15"))
        }
        binding.recyclerView2.layoutManager= LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        val categoriesAdapter= CategoriesAdapter(categoriesList)
        binding.recyclerView2.setHasFixedSize(true)
        binding.recyclerView2.setRecycledViewPool(RecyclerView.RecycledViewPool())
        binding.recyclerView2.adapter=categoriesAdapter
        binding.recyclerView3.layoutManager= GridLayoutManager(this,2)
        val categoriesAdapter1= CategoriesAdapter1(categoriesList1)
        binding.recyclerView3.setHasFixedSize(true)
        binding.recyclerView3.setRecycledViewPool(RecyclerView.RecycledViewPool())
        binding.recyclerView3.adapter=categoriesAdapter1
        categoriesAdapter1.onItemClick={
            var intent= Intent(this, Order::class.java)
            intent.putExtra("restaurant",it)
            startActivity(intent)
        }
    }
}