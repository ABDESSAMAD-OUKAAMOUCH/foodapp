package com.example.deliveryapp.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.deliveryapp.LocationViewModel
import com.example.deliveryapp.Order
import com.example.deliveryapp.R
import com.example.deliveryapp.Restaurant
import com.example.deliveryapp.RestaurantActivity
import com.example.deliveryapp.RestaurantAdapter
import com.example.deliveryapp.SearchActivity
import com.example.deliveryapp.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLEncoder

class HomeFragment : Fragment() {
    var _binding: FragmentHomeBinding?=null
    val binding get() = _binding!!
    var imageList=ArrayList<SlideModel>()
    private lateinit var restaurantList: MutableList<Restaurant>
    private lateinit var restaurantAdapter: RestaurantAdapter
    private val locationViewModel: LocationViewModel by activityViewModels()
    lateinit var mNavController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController=findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentHomeBinding.inflate(inflater, container, false)
        locationViewModel.fullAddress.observe(viewLifecycleOwner) { address ->
            val city = locationViewModel.city.value ?: "Unknown City"
            val country = locationViewModel.country.value ?: "Unknown Country"
            val latitude = locationViewModel.latitude.value ?: 0.0
            val longitude = locationViewModel.longitude.value ?: 0.0

            binding.textCity.text = city
            binding.location.text = address
            binding.address.setOnClickListener {
                try {
                    // 1. التحقق من وجود القيم
                        // 2. إنشاء رابط الخرائط بعدة صيغ (لزيادة التوافق)
                        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${URLEncoder.encode("موقعك الحالي", "UTF-8")})")
                        val intent = Intent(Intent.ACTION_VIEW, uri)

                        // 3. محاولة فتح تطبيق خرائط جوجل أولاً
                        intent.setPackage("com.google.android.apps.maps")

                        // 4. إذا لم يكن تطبيق خرائط جوجل مثبتاً، استخدم أي تطبيق خرائط آخر
                        if (intent.resolveActivity(requireActivity().packageManager) == null) {
                            intent.setPackage(null) // إزالة التقييد بنوع التطبيق
                        }

                        // 5. بدء النشاط
                        startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(context, "حدث خطأ أثناء فتح الخرائط", Toast.LENGTH_SHORT).show()
                    Log.e("MapsError", "Error opening maps", e)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            binding.container1.setPadding(0, statusBarHeight, 0,0)
            insets
        }



        // إعداد الـ Adapter
        restaurantList = mutableListOf()
        restaurantAdapter = RestaurantAdapter(requireContext(), restaurantList, object : RestaurantAdapter.OnItemClickListener {
            override fun onItemClick(restaurant: Restaurant) {

                // مثال: فتح صفحة تفاصيل أو تمرير البيانات
                val intent = Intent(requireContext(), RestaurantActivity::class.java)
                intent.putExtra("restaurantName", restaurant.restaurantName)
                intent.putExtra("imageUrl", restaurant.imageBase64)
                intent.putExtra("restaurantUrl", restaurant.restaurantUrl)
                intent.putExtra("restaurantId", restaurant.id) // ← أضف هذا السطر
                intent.putExtra("adminId", restaurant.adminId) // ← أضف هذا السطر
                val prefs = requireContext().getSharedPreferences(
                    "adminId",
                    Context.MODE_PRIVATE
                )
                prefs.edit().apply {
                    putString("adminId", restaurant.adminId) // تخزين adminId
                    apply()
                }

                startActivity(intent)
            }
        })
        binding.recyclerView1.layoutManager= LinearLayoutManager(activity, RecyclerView.VERTICAL,false)
        binding.recyclerView1.adapter = restaurantAdapter

        binding.recyclerView1.setHasFixedSize(true)
        binding.recyclerView1.setRecycledViewPool(RecyclerView.RecycledViewPool())

        // قراءة البيانات من Firestore
        FirebaseFirestore.getInstance().collection("admins")
            .get()
            .addOnSuccessListener { adminsSnapshot ->
                restaurantList.clear()
                var remainingAdmins = adminsSnapshot.size()

                if (remainingAdmins == 0) {
                    binding.progressBar.visibility = View.GONE
                    restaurantAdapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                for (adminDoc in adminsSnapshot) {
                    FirebaseFirestore.getInstance().collection("admins")
                        .document(adminDoc.id)
                        .collection("restaurants")
                        .get()
                        .addOnSuccessListener { restaurantSnapshot ->
                            for (restaurantDoc in restaurantSnapshot) {
                                val restaurant = restaurantDoc.toObject(Restaurant::class.java)
                                restaurant.id = restaurantDoc.id
                                restaurant.adminId = adminDoc.id // 🟢 تخزين adminId داخل الكائن
                                restaurantList.add(restaurant)
                            }

                            remainingAdmins--
                            if (remainingAdmins == 0) {
                                binding.progressBar.visibility = View.GONE
                                restaurantAdapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener {
                            remainingAdmins--
                            if (remainingAdmins == 0) {
                                binding.progressBar.visibility = View.GONE
                                restaurantAdapter.notifyDataSetChanged()
                            }
                            Toast.makeText(requireContext(), "Error loading restaurants for an admin", Toast.LENGTH_SHORT).show()
                        }
                }

            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error loading admins: $exception", Toast.LENGTH_SHORT).show()
            }

        imageList.add(SlideModel(R.drawable.group1))
        imageList.add(SlideModel(R.drawable.group2))
        imageList.add(SlideModel(R.drawable.group3))
        binding.imageSlider.setImageList(imageList)

        binding.recyclerView1.setOnClickListener {

            startActivity(Intent(requireContext(), Order::class.java))
        }

        binding.searchlocation.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
                view.clearFocus() // لإزالة التركيز بعد الانتقال
            }
        }




//        binding.bottomnavigation.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.cart->startActivity(Intent(this, Order::class.java))
//
//            }
//            true
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}