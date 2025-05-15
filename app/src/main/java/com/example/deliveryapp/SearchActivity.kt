package com.example.deliveryapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliveryapp.databinding.ActivitySearchBinding
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var itemsAdapter: ItemsAdapter
    private val allItems = mutableListOf<Item>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchFunctionality()
        fetchAllItems()
    }
    private fun setupRecyclerView() {
        itemsAdapter = ItemsAdapter(this, emptyList())
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity,2)
            adapter = itemsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchFunctionality() {
        binding.search.apply {
            requestFocus()

            doOnTextChanged { text, _, _, _ ->
                filterItems(text.toString())
            }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    filterItems(text.toString())
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun filterItems(query: String) {
        val filtered = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter { item ->
                item.itemName.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true)
            }
        }
        itemsAdapter.updateList(filtered)
    }

    private fun fetchAllItems() {
        showLoading(true)

        db.collectionGroup("items")
            .get()
            .addOnSuccessListener { querySnapshot ->
                allItems.clear()
                allItems.addAll(querySnapshot.toObjects(Item::class.java))
                itemsAdapter.updateList(allItems)
                showLoading(false)
            }
            .addOnFailureListener { e ->
                showError("Failed to load items: ${e.message}")
                showLoading(false)
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}