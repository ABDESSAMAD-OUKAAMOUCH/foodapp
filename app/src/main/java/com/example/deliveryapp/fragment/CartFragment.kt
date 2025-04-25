package com.example.deliveryapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.DataOrder
import com.example.deliveryapp.OrderAdapter
import com.example.deliveryapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var cartAdapter: OrderAdapter
    private val cartItems = mutableListOf<DataOrder>()
    private lateinit var adminId: String


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
        progressBar = view.findViewById(R.id.progressBar3)
        val prefs =requireContext().getSharedPreferences("adminId", MODE_PRIVATE)
        adminId = prefs.getString("adminId", "").toString()
        val restaurantId =
            requireContext().getSharedPreferences("RestaurantPrefs", Context.MODE_PRIVATE)
                .getString("restaurantId", null)

        val image=view.findViewById<ImageView>(R.id.back)
        image.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = restaurantId?.let { OrderAdapter(requireContext(), cartItems, it,adminId) }!!
        recyclerView.adapter = cartAdapter

        fetchCartItems()

    }

    private fun fetchCartItems() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid

        progressBar.visibility = View.VISIBLE
        cartItems.clear()

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("orders")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    val item = doc.toObject(DataOrder::class.java)
                    if (item != null) {
                        item.id = doc.id
                        cartItems.add(item)
                    }
                }

                progressBar.visibility = View.GONE
                cartAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "فشل تحميل الطلبات: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




}

