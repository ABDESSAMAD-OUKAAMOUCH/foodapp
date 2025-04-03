package com.example.deliveryapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.deliveryapp.databinding.ActivityForgetPassword3Binding
import com.google.firebase.auth.FirebaseAuth

class ForgetPassword3 : AppCompatActivity() {
    lateinit var binding:ActivityForgetPassword3Binding
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityForgetPassword3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        binding.finish.setOnClickListener {
            if(binding.newPassword.text.isNotEmpty() && binding.confirmNewPassword.text.isNotEmpty()) {
                    if(binding.newPassword.text.toString() == binding.confirmNewPassword.text.toString()){
                        val mProgressDialog = ProgressDialog(this)
                        mProgressDialog.setMessage("Loading...")
                        mProgressDialog.setCanceledOnTouchOutside(false)
                        mProgressDialog.show()
                        var newPassword=binding.newPassword.text.toString()
                        var user = mAuth.currentUser
                        user?.updatePassword(newPassword)?.addOnCompleteListener { 
                            if(it.isSuccessful){
                                mProgressDialog.dismiss()
                                Toast.makeText(this, "your Password is updated ", Toast.LENGTH_SHORT).show();
                                var intent = Intent(this, SignIn::class.java)
                                startActivity(intent)
                            }else{
                                mProgressDialog.dismiss()
                                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show();
                            }
                        
                        }
                    }else{
                            Toast.makeText(this, "confirm password", Toast.LENGTH_SHORT).show();
                        }

            }
        }
    }
}