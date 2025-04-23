package com.example.deliveryapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliveryapp.databinding.ActivityRestaurantBinding
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantActivity : AppCompatActivity() {
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemAdapter: ItemAdapter
    lateinit var restaurantId:String
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var itemRecycler: RecyclerView

    private var allCategories: List<Category> = listOf()
    lateinit var binding: ActivityRestaurantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        var restaurantName = intent.getStringExtra("restaurantName")
        var imageBase64 = intent.getStringExtra("imageUrl")
        var restaurantUrl = intent.getStringExtra("restaurantUrl")
//image
        Glide.with(this).load(imageBase64).into(binding.imageView12)
        binding.nameRestaurant.text = restaurantName

        categoryRecycler = findViewById(R.id.recyclerView2)
        itemRecycler = findViewById(R.id.recyclerView3)

        // RecyclerView للفئات
        categoryRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // RecyclerView للأصناف
        itemRecycler.layoutManager = GridLayoutManager(this, 2)

        // جلب الفئات من Firebase مع الأصناف الخاصة بها
        val restaurantId = intent.getStringExtra("restaurantId") ?: return
        fetchCategoriesWithItems(restaurantId)
        val sharedPrefs = getSharedPreferences("RestaurantPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("restaurantId", restaurantId).apply()


    }

    private fun fetchCategoriesWithItems(restaurantId: String) {
        FirebaseFirestore.getInstance()
            .collection("restaurants")
            .document(restaurantId)
            .collection("categories")
            .get()
            .addOnSuccessListener { categoryDocs ->
                val categories = mutableListOf<Category>()

                for (doc in categoryDocs) {
                    val category = doc.toObject(Category::class.java).copy(id = doc.id)
                    categories.add(category)
                }

                allCategories = categories
                categoryAdapter = CategoryAdapter(this, categories) { selectedCategory ->
                    loadItemsForCategory(selectedCategory.id)
                }
                categoryRecycler.adapter = categoryAdapter

                if (categories.isNotEmpty()) {
                    loadItemsForCategory(categories[0].id) // أول فئة
                }
            }
    }

    private fun loadItemsForCategory(categoryId: String) {
        val restaurantId = intent.getStringExtra("restaurantId") ?: return

        FirebaseFirestore.getInstance()
            .collection("restaurants")
            .document(restaurantId)
            .collection("categories")
            .document(categoryId)
            .collection("Items")
            .get()
            .addOnSuccessListener { itemDocs ->
                val items = itemDocs.map { it.toObject(Item::class.java) }
                itemAdapter = ItemAdapter(this, items,restaurantId,categoryId)
                itemRecycler.adapter = itemAdapter
            }
    }
}