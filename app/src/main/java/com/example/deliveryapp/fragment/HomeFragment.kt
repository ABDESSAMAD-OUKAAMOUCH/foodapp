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
import com.example.deliveryapp.Order
import com.example.deliveryapp.R
import com.example.deliveryapp.Restaurant
import com.example.deliveryapp.RestaurantActivity
import com.example.deliveryapp.RestaurantAdapter
import com.example.deliveryapp.databinding.ActivityHomeBinding
import com.example.deliveryapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    var _binding: FragmentHomeBinding?=null
    val binding get() = _binding!!
    var imageList=ArrayList<SlideModel>()
    var restaurantsList=ArrayList<Restaurant>()
    var categoriesList=ArrayList<Categories>()
    var categoriesList1=ArrayList<Categories1>()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            binding.container1.setPadding(0, statusBarHeight, 0,0)
            insets
        }
        if(restaurantsList.isEmpty()){
            restaurantsList.add(Restaurant(R.drawable.image1,"Restaurants 1","25-30 min"))
            restaurantsList.add(Restaurant(R.drawable.image3,"Restaurants 2","25-30 min"))
            restaurantsList.add(Restaurant(R.drawable.image2,"Restaurants 3","25-30 min"))
        }

        imageList.add(SlideModel(R.drawable.group1))
        imageList.add(SlideModel(R.drawable.group2))
        imageList.add(SlideModel(R.drawable.group3))
        binding.imageSlider.setImageList(imageList)

        binding.recyclerView1.setOnClickListener {
            startActivity(Intent(requireContext(), Order::class.java))
        }

        binding.recyclerView1.layoutManager= LinearLayoutManager(activity, RecyclerView.VERTICAL,false)
        val restaurantAdapter= RestaurantAdapter(restaurantsList)
        binding.recyclerView1.setHasFixedSize(true)
        binding.recyclerView1.setRecycledViewPool(RecyclerView.RecycledViewPool())
        binding.recyclerView1.adapter=restaurantAdapter
        restaurantAdapter.onItemClick={
            var intent=Intent(requireContext(), RestaurantActivity::class.java)
            intent.putExtra("restaurant",it)
            startActivity(intent)
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