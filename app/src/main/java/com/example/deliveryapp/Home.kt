package com.example.deliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.deliveryapp.databinding.ActivityHomeBinding
import com.example.deliveryapp.databinding.ActivityLocation1Binding
import com.example.deliveryapp.fragment.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController=navHostFragment.navController
        binding.bottomnavigation.setupWithNavController(navController)


//        binding.bottomnavigation.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.cart-> {
//                    Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
//                    var fragmentTransaction=supportFragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.frameLayout,HomeFragment())
//                    fragmentTransaction.addToBackStack(null)
//                    fragmentTransaction.commit()
//                }
//
//            }
//            true
//        }
    }


}