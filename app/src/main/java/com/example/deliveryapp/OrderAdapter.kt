package com.example.deliveryapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OrderAdapter(
    private val context: android.content.Context,
    private val orderList: MutableList<DataOrder>,
    private val restaurantId: String,
    private val adminId: String // تمرير الـ adminId من الـ Activity أو Fragment
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val nameTextView: TextView = view.findViewById(R.id.textView)
        val priceTextView: TextView = view.findViewById(R.id.price)
        val paymentTypeTextView: TextView = view.findViewById(R.id.paymentTypeTextView)
        val deleteIcon: ImageView = view.findViewById(R.id.imageButton2)
        val quantity: TextView = view.findViewById(R.id.number)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_cart, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.nameTextView.text = order.name
        holder.priceTextView.text = "${order.price}$"
        holder.paymentTypeTextView.text = order.paymentType
        holder.quantity.text = "${order.quantity}"
        holder.status.text = "Status: ${order.status}"
        if (order.status=="Delivered"){
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green))
        }

        // Decode Base64 image
        val imageBytes = Base64.decode(order.imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imageView.setImageBitmap(bitmap)

        holder.deleteIcon.setOnClickListener {
            val orderId = order.id
            val userId = order.userId

            if (userId != null && orderId != null && adminId != null) {
                // حذف من مجلد المستخدم
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("orders")
                    .document(orderId)
                    .delete()
                    .addOnSuccessListener {
                        // بعد الحذف من مجلد المستخدم، حذف الطلب من مجلد admins
                        FirebaseFirestore.getInstance()
                            .collection("admins")
                            .document(adminId)
                            .collection("restaurants")
                            .document(restaurantId ?: "")  // استخدم restaurantId من الطلب
                            .collection("orders")
                            .document(orderId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                orderList.removeAt(holder.adapterPosition)
                                notifyItemRemoved(holder.adapterPosition)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "فشل الحذف من قسم المطعم", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "فشل الحذف من قسم المستخدم", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    override fun getItemCount(): Int = orderList.size
}
