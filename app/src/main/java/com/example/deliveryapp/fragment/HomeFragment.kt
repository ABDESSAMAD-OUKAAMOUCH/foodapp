package com.example.deliveryapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.deliveryapp.Categories
import com.example.deliveryapp.Categories1
import com.example.deliveryapp.CategoriesAdapter
import com.example.deliveryapp.CategoriesAdapter1
import com.example.deliveryapp.LocationViewModel
import com.example.deliveryapp.Order
import com.example.deliveryapp.R
import com.example.deliveryapp.Restaurant
import com.example.deliveryapp.RestaurantActivity
import com.example.deliveryapp.RestaurantAdapter
import com.example.deliveryapp.databinding.ActivityHomeBinding
import com.example.deliveryapp.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    var _binding: FragmentHomeBinding?=null
    val binding get() = _binding!!
    var imageList=ArrayList<SlideModel>()
    private lateinit var restaurantList: MutableList<Restaurant>
    private lateinit var restaurantAdapter: RestaurantAdapter
    var categoriesList=ArrayList<Categories>()
    var categoriesList1=ArrayList<Categories1>()
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
                startActivity(intent)
            }
        })
        binding.recyclerView1.layoutManager= LinearLayoutManager(activity, RecyclerView.VERTICAL,false)
        binding.recyclerView1.adapter = restaurantAdapter

        binding.recyclerView1.setHasFixedSize(true)
        binding.recyclerView1.setRecycledViewPool(RecyclerView.RecycledViewPool())

        // قراءة البيانات من Firestore
        FirebaseFirestore.getInstance().collection("restaurants")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val restaurant = document.toObject(Restaurant::class.java)
                    restaurantList.add(restaurant)
                }
                restaurantAdapter.notifyDataSetChanged() // تحديث الـ RecyclerView بعد إضافة البيانات
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting restaurants: $exception", Toast.LENGTH_SHORT).show()
            }
//        restaurantAdapter.onItemClick={
//            var intent=Intent(requireContext(), RestaurantActivity::class.java)
//            intent.putExtra("restaurant",it)
//            startActivity(intent)
//        }


        imageList.add(SlideModel(R.drawable.group1))
        imageList.add(SlideModel(R.drawable.group2))
        imageList.add(SlideModel(R.drawable.group3))
        binding.imageSlider.setImageList(imageList)

        binding.recyclerView1.setOnClickListener {
            startActivity(Intent(requireContext(), Order::class.java))
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