package com.example.deliveryapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OrderAdapter(
    private val context: android.content.Context,
    private val orderList: MutableList<DataOrder>,
    private val restaurantId: String

) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val nameTextView: TextView = view.findViewById(R.id.textView)
        val priceTextView: TextView = view.findViewById(R.id.price)
        val paymentTypeTextView: TextView = view.findViewById(R.id.paymentTypeTextView)
        val deleteIcon: ImageView = view.findViewById(R.id.imageButton2)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_cart, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.nameTextView.text = order.name
        holder.priceTextView.text = "${order.price}"
        holder.paymentTypeTextView.text = order.paymentType

        // فك ترميز الصورة من Base64
        val imageBytes = Base64.decode(order.imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imageView.setImageBitmap(bitmap)

        //delete
        holder.deleteIcon.setOnClickListener {
            val orderId = order.id
            FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(restaurantId)  // مرّر restaurantId من الخارج أو خزنه
                .collection("orders")
                .document(orderId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "تم حذف الطلب", Toast.LENGTH_SHORT).show()
                    orderList.removeAt(position)  // ← نحذف من القائمة
                    notifyItemRemoved(position)   // ← نخبر المحول بالتحديث
                    notifyItemRangeChanged(position, orderList.size) // ← لتحديث الـ index المتبقي
                }
                .addOnFailureListener {
                    Toast.makeText(context, "فشل في الحذف", Toast.LENGTH_SHORT).show()
                }
        }



    }

    override fun getItemCount(): Int = orderList.size
}
