package com.example.deliveryapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.deliveryapp.databinding.ActivityHomeBinding
import com.example.deliveryapp.databinding.ActivityOrderBinding

class Order : AppCompatActivity() {
    lateinit var binding: ActivityOrderBinding
    var number1=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView2.setOnClickListener {
            finish()
        }
        binding.imageView10.setOnClickListener {
//            binding.number.text=number1++.toString()
            Log.d("ActivityMain",number1++.toString())
        }
        binding.imageView11.setOnClickListener {
            if(number1>1){
//                binding.number.text=number1--.toString()
                Log.d("ActivityMain",number1--.toString())
            }
        }
    }
}