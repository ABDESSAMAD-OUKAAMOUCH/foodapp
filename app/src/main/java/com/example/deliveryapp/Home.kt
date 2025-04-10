package com.example.deliveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomnavigation.setupWithNavController(navController)
        val location = intent.getStringExtra("country")
        val locationCity = intent.getStringExtra("city")

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val accuracy = intent.getFloatExtra("accuracy", 0.0f)
        val city = intent.getStringExtra("city")
        val country = intent.getStringExtra("country")
        val fullAddress = intent.getStringExtra("full_address")

        // تخزين البيانات في ViewModel
        locationViewModel.setLocation(latitude, longitude, accuracy, city, country, fullAddress)
    }
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


class LocationViewModel : ViewModel() {
    private val _latitude = MutableLiveData<Double>()
    private val _longitude = MutableLiveData<Double>()
    private val _accuracy = MutableLiveData<Float>()
    private val _city = MutableLiveData<String?>()
    private val _country = MutableLiveData<String?>()
    private val _fullAddress = MutableLiveData<String?>()

    val latitude: LiveData<Double> get() = _latitude
    val longitude: LiveData<Double> get() = _longitude
    val accuracy: LiveData<Float> get() = _accuracy
    val city: MutableLiveData<String?> get() = _city
    val country: MutableLiveData<String?> get() = _country
    val fullAddress: MutableLiveData<String?> get() = _fullAddress

    fun setLocation(
        latitude: Double,
        longitude: Double,
        accuracy: Float,
        city: String?,
        country: String?,
        fullAddress: String?
    ) {
        _latitude.value = latitude
        _longitude.value = longitude
        _accuracy.value = accuracy
        _city.value = city
        _country.value = country
        _fullAddress.value = fullAddress
    }
}
