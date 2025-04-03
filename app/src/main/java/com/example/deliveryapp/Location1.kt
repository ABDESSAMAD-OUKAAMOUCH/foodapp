package com.example.deliveryapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.deliveryapp.databinding.ActivityLocation1Binding
import com.google.firebase.auth.FirebaseAuth

class Location1 : AppCompatActivity() {
    lateinit var binding:ActivityLocation1Binding
    var mAuth: FirebaseAuth?=null
    var requestId=100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLocation1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth=FirebaseAuth.getInstance()
        binding.currentLocation.setOnClickListener {
            myPermission(requestId,Manifest.permission.ACCESS_FINE_LOCATION)
        }
        binding.Searchlocation.setOnClickListener {
            myPermission(requestId,Manifest.permission.ACCESS_FINE_LOCATION)
        }
        binding.Searchlocation.setOnClickListener {
            var intent= Intent(this,Location2::class.java)
            startActivity(intent)
        }
    }
    fun myPermission(requestId:Int,permissionName:String):Boolean{
        if(android.os.Build.VERSION.SDK_INT>=23){
            var permission=ActivityCompat.checkSelfPermission(applicationContext,permissionName)
            if(permission!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(permissionName),requestId)
                return false
            }
        }
        return true
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        mAuth?.signOut()
//    }
}