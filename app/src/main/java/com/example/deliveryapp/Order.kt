package com.example.deliveryapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.deliveryapp.databinding.ActivityHomeBinding
import com.example.deliveryapp.databinding.ActivityOrderBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Order : AppCompatActivity() {
    lateinit var binding: ActivityOrderBinding
    private lateinit var restaurantId: String
    private lateinit var adminId: String
    private lateinit var categoryId: String
    private lateinit var item: Item
    lateinit var name: String
    lateinit var price: String
    lateinit var imageBase64: String
    var total = 0.0
    var quantity = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageBase64 = intent.getStringExtra("ITEM_IMAGE_BASE64").toString()
        name = intent.getStringExtra("itemName").toString()
        price = intent.getStringExtra("price").toString()
        val description = intent.getStringExtra("description")
        total = price.toDouble()
        restaurantId = intent.getStringExtra("restaurantId").toString()
        val prefs = getSharedPreferences("adminId", MODE_PRIVATE)
        adminId = prefs.getString("adminId", "").toString()



        if (imageBase64 != null) {
            val imageBytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Set the image on an ImageView
            val imageView: ImageView = findViewById(R.id.imageView5)
            imageView.setImageBitmap(bitmap)
        }

        total = price.toDouble()
        binding.textView6.text = name
        binding.price.text = "${price}$"
        binding.textView8.text = description
        binding.textView2.text = "${total}$"
        binding.imageView2.setOnClickListener {
            finish()
        }

        binding.imageView10.setOnClickListener {
            if (quantity < 10) {
                quantity++
                binding.number.text = quantity.toString()
                total = price.toDouble() * quantity // إعادة حساب الإجمالي بناء على السعر الأصلي
                binding.textView2.text =
                    "${String.format("%.1f", total)}$" // تنسيق العدد مع خانتين عشريتين
            }
        }

        binding.imageView11.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.number.text = quantity.toString()
                total = price.toDouble() * quantity // إعادة حساب الإجمالي بناء على السعر الأصلي
                binding.textView2.text = "${String.format("%.1f", total)}$"
            }
        }

        binding.button.setOnClickListener {
            binding.progressBar2.visibility = View.VISIBLE
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar2.visibility = View.VISIBLE

        // 1. First delete any placeholder orders
        db.collection("admins")
            .document(adminId)  // Make sure adminId is available
            .collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .whereEqualTo("placeholder", true)
            .get()
            .addOnSuccessListener { snapshot ->
                val deleteTasks = snapshot.documents.map { it.reference.delete() }

                Tasks.whenAll(deleteTasks)
                    .continueWithTask {
                        db.collection("users").document(userId).get()
                    }
                    .addOnSuccessListener { userDoc ->
                        // ===== 2. Prepare Order Data =====
                        val orderData = hashMapOf<String, Any>(
                            "name" to name,
                            "price" to total,
                            "imageBase64" to imageBase64,
                            "paymentType" to "Cash on Delivery",
                            "timestamp" to FieldValue.serverTimestamp(),
                            "quantity" to quantity,
                            "userId" to userId,
                            "status" to "pending",
                            "adminId" to adminId // Add adminId here
                        )

                        try {
                            (userDoc.get("location") as? Map<String, Any>)?.let { locationData ->
                                val validLocation = hashMapOf<String, Any>()
                                locationData["lat"]?.let { if (it is Double) validLocation["lat"] = it }
                                locationData["lng"]?.let { if (it is Double) validLocation["lng"] = it }
                                locationData["city"]?.let { if (it is String) validLocation["city"] = it }
                                locationData["country"]?.let { if (it is String) validLocation["country"] = it }

                                if (validLocation.isNotEmpty()) {
                                    orderData["location"] = validLocation
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("LocationError", "Failed to process location", e)
                        }

                        // ===== 3. Create orderId manually =====
                        val adminOrderRef = db.collection("admins")
                            .document(adminId)
                            .collection("restaurants")
                            .document(restaurantId)
                            .collection("orders")
                            .document() // generate ref without writing yet

                        val orderId = adminOrderRef.id // get same orderId for both paths

                        val userOrderRef = db.collection("users")
                            .document(userId)
                            .collection("orders")
                            .document(orderId)

                        // ===== 4. Write to both locations =====
                        val writeToAdmin = adminOrderRef.set(orderData)
                        val writeToUser = userOrderRef.set(orderData)

                        Tasks.whenAll(writeToAdmin, writeToUser)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Added to cart successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save order: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to get user data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to check placeholders: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                binding.progressBar2.visibility = View.GONE
            }
    }

}