package com.example.deliveryapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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
    var adminId:String?=null
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
        var adminId = intent.getStringExtra("adminId")
        val prefs = getSharedPreferences(
            "adminId",
            MODE_PRIVATE
        )
        prefs.edit().apply {
            putString("adminId", adminId)
            apply()
        }
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
        if (adminId != null) {
            fetchCategoriesWithItems(adminId, restaurantId)
        }
        val sharedPrefs = getSharedPreferences("RestaurantPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("restaurantId", restaurantId).apply()


    }

    private fun fetchCategoriesWithItems(adminId: String, restaurantId: String) {
        FirebaseFirestore.getInstance()
            .collection("admins")
            .document(adminId)
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
                    // عند الضغط على فئة
                    loadItemsForCategory(adminId, restaurantId, selectedCategory.id)
                }
                categoryRecycler.adapter = categoryAdapter

                if (categories.isNotEmpty()) {
                    // تحميل عناصر أول فئة تلقائيًا
                    loadItemsForCategory(adminId, restaurantId, categories[0].id)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "فشل تحميل التصنيفات", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadItemsForCategory(adminId: String, restaurantId: String, categoryId: String) {
        FirebaseFirestore.getInstance()
            .collection("admins")
            .document(adminId)
            .collection("restaurants")
            .document(restaurantId)
            .collection("categories")
            .document(categoryId)
            .collection("items")
            .get()
            .addOnSuccessListener { itemDocs ->
                val items = itemDocs.map { it.toObject(Item::class.java) }
                itemAdapter = ItemAdapter(this, items, restaurantId, categoryId)
                itemRecycler.adapter = itemAdapter
                binding.progressBar4.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "فشل تحميل العناصر", Toast.LENGTH_SHORT).show()
            }
    }


}