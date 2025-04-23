package com.example.deliveryapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.deliveryapp.databinding.ActivityHomeBinding
import com.example.deliveryapp.databinding.ActivityOrderBinding
import com.google.firebase.firestore.FirebaseFirestore

class Order : AppCompatActivity() {
    lateinit var binding: ActivityOrderBinding
    var number1 = 1
    private lateinit var restaurantId: String
    private lateinit var categoryId: String
    private lateinit var item: Item
    lateinit var name: String
    lateinit var price: String
    lateinit var imageBase64: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageBase64 = intent.getStringExtra("ITEM_IMAGE_BASE64").toString()
        name = intent.getStringExtra("itemName").toString()
        price = intent.getStringExtra("price").toString()
        val description = intent.getStringExtra("description")
        restaurantId = intent.getStringExtra("restaurantId").toString()
        if (imageBase64 != null) {
            val imageBytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Set the image on an ImageView
            val imageView: ImageView = findViewById(R.id.imageView5)
            imageView.setImageBitmap(bitmap)
        }
        binding.textView6.text = name
        binding.price.text = price
        binding.textView8.text = description
        binding.imageView2.setOnClickListener {
            finish()
        }

        binding.imageView10.setOnClickListener {
//            binding.number.text=number1++.toString()
            Log.d("ActivityMain", number1++.toString())
        }
        binding.imageView11.setOnClickListener {
            if (number1 > 1) {
//                binding.number.text=number1--.toString()
                Log.d("ActivityMain", number1--.toString())
            }
        }

        binding.button.setOnClickListener {
            addToCart()
        }


//            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null)
//            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
//
//            AlertDialog.Builder(this)
//                .setTitle("قيّم تجربتك")
//                .setView(dialogView)
//                .setPositiveButton("إرسال") { dialog, _ ->
//                    val rating = ratingBar.rating
//                    // احسب المتوسط الجديد وحدث في Firebase
//
//                    dialog.dismiss()
//                }
//                .setNegativeButton("لاحقًا", null)
//                .show()
    }


    private fun addToCart() {
        val db = FirebaseFirestore.getInstance()
        val ordersRef = db.collection("restaurants")
            .document(restaurantId)
            .collection("orders")

        // حذف placeholder إن وُجد
        ordersRef.whereEqualTo("placeholder", true)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (doc in snapshot.documents) {
                        Log.d("FIRESTORE", "Doc: ${doc.data}")
                        doc.reference.delete()
                    }
                }

                // بعد حذف الـ placeholder أو في حال لم يكن موجوداً، نضيف الطلب
                val orderData = hashMapOf(
                    "name" to name,
                    "price" to price,
                    "imageBase64" to imageBase64,
                    "paymentType" to "الدفع عند الاستلام",
                    "timestamp" to System.currentTimeMillis()
                )

                ordersRef.add(orderData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "فشل في الإضافة", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "فشل في التحقق من placeholder", Toast.LENGTH_SHORT).show()
            }
    }
}