package com.example.deliveryapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.DataOrder
import com.example.deliveryapp.OrderAdapter
import com.example.deliveryapp.R
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: OrderAdapter
    private val cartItems = mutableListOf<DataOrder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        val restaurantId =
            requireContext().getSharedPreferences("RestaurantPrefs", Context.MODE_PRIVATE)
                .getString("restaurantId", null)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = restaurantId?.let { OrderAdapter(requireContext(), cartItems, it) }!!
        recyclerView.adapter = cartAdapter

        fetchCartItems()

    }

    private fun fetchCartItems() {
        val restaurantId = requireContext()
            .getSharedPreferences("RestaurantPrefs", Context.MODE_PRIVATE)
            .getString("restaurantId", null) ?: return

        FirebaseFirestore.getInstance()
            .collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .get()
            .addOnSuccessListener { documents ->
                cartItems.clear()
                for (doc in documents) {
                    val item = doc.toObject(DataOrder::class.java)
                    item.id = doc.id  // <-- نحفظ الـ ID هنا
                    cartItems.add(item)
                }
                cartAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "فشل تحميل الطلبات", Toast.LENGTH_SHORT).show()
            }
    }

}

