package com.example.deliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.deliveryapp.databinding.ActivityLocation1Binding
import com.example.deliveryapp.databinding.ActivityLocation2Binding

class Location2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding: ActivityLocation2Binding
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLocation2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.currentLocation.setOnClickListener {
            var intent= Intent(this,Home::class.java)
            startActivity(intent)
        }
    }
}