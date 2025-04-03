package com.example.deliveryapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.deliveryapp.databinding.ActivitySignInBinding
import com.example.deliveryapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {
    lateinit var binding:ActivitySignUpBinding
    var mAut:FirebaseAuth?=null
    var passwordShowing=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAut=FirebaseAuth.getInstance()
        binding.signIn.setOnClickListener {
            var intent= Intent(this,SignIn::class.java)
            startActivity(intent)
        }
        binding.passwordIcon.setOnClickListener {
            passwordShowing=!passwordShowing
            passwordIcon(passwordShowing)
        }
        binding.signUp.setOnClickListener {
            if(binding.emailAddress.text.isNotEmpty() && binding.firstname.text.isNotEmpty() && binding.lastname.text.isNotEmpty() && binding.phone.text.isNotEmpty() && binding.password.text.isNotEmpty()){
                val mProgressDialog = ProgressDialog(this)
                mProgressDialog.setMessage("Loading...")
                mProgressDialog.setCanceledOnTouchOutside(false)
                mProgressDialog.show()
                mAut?.createUserWithEmailAndPassword(binding.emailAddress.text.toString(),binding.password.text.toString())?.addOnCompleteListener {
                    if(it.isSuccessful){
                        var user=mAut?.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener {
                            if(it.isSuccessful){
                                mProgressDialog.dismiss()
                                Toast.makeText(this,"account created,\n check your email for verify your account",Toast.LENGTH_SHORT).show()
                                var intent=Intent(this,SignIn::class.java)
                                startActivity(intent)
                            }
                            else{
                                mProgressDialog.dismiss()
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        mProgressDialog.dismiss()
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private fun passwordIcon(isShow:Boolean){
        if(isShow){
            binding.password.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.passwordIcon.setImageResource(R.drawable.open)
        }else{
            binding.password.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.passwordIcon.setImageResource(R.drawable.closed)
        }
        binding.password.setSelection(binding.password.text.length)
    }
}