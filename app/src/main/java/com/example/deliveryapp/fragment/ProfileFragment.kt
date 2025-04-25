package com.example.deliveryapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.deliveryapp.R
import com.example.deliveryapp.SignIn
import com.example.deliveryapp.databinding.FragmentHomeBinding
import com.example.deliveryapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    var _binding: FragmentProfileBinding?=null
    val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName")
                        val email = document.getString("email")
                        val phone = document.getString("phone")

                        // استرجاع الـ Map الخاصة بالموقع
                        val locationMap = document.get("location") as? Map<*, *>
                        val city = locationMap?.get("city") as? String

                        // عرض البيانات في الواجهة
                        binding.name.setText(fullName)
                        binding.email.setText(email)
                        binding.phone.setText(phone)
                        binding.city.setText(city) // عرض اسم المدينة مثلاً
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "فشل في جلب البيانات", Toast.LENGTH_SHORT).show()
                }
        }
        var isEditMode = false
        binding.name.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        binding.city.isEnabled = false

        binding.editProfile.setOnClickListener {
            if (!isEditMode) {
                // دخول وضع التعديل
                isEditMode = true
                binding.editProfile.text = "Save"
                binding.editProfile.setBackgroundColor(R.color.Raspberry)
                binding.name.isEnabled = true
                binding.phone.isEnabled = true
            } else {
                // حفظ التعديلات
                isEditMode = false
                binding.editProfile.setBackgroundColor(R.color.stroke)
                binding.editProfile.text = "Edit Profile"
                binding.name.isEnabled = false
                binding.phone.isEnabled = false

                val newName = binding.name.text.toString()
                val newEmail = binding.email.text.toString()
                val newPhone = binding.phone.text.toString()
                val newCity = binding.city.text.toString()

                val updates = hashMapOf<String, Any>(
                    "fullName" to newName,
                    "email" to newEmail,
                    "location.city" to newCity,
                    "phone" to newPhone  // إذا كنت تحفظ رقم الهاتف داخل location، أو غير المفتاح حسب مكانه
                )

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    db.collection("users").document(userId)
                        .update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "تم تحديث البيانات بنجاح", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "فشل التحديث", Toast.LENGTH_SHORT).show()
                        }
                }


            }
        }
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // اذهب إلى شاشة تسجيل الدخول أو البداية
            val intent = Intent(requireContext(), SignIn::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }





    }
}