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
import com.google.firebase.firestore.FirebaseFirestore

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
                mAut?.createUserWithEmailAndPassword(
                    binding.emailAddress.text.toString(),
                    binding.password.text.toString()
                )?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = mAut?.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // ✅ إضافة المستخدم إلى Firestore
                                val db = FirebaseFirestore.getInstance()
                                val userId = user.uid
                                val userData = hashMapOf(
                                    "firstName" to binding.firstname.text.toString(),
                                    "lastName" to binding.lastname.text.toString(),
                                    "fullName" to "${binding.firstname.text} ${binding.lastname.text}",
                                    "phone" to binding.phone.text.toString(),
                                    "email" to binding.emailAddress.text.toString(),
                                    "location" to null // حقل الموقع فارغ حالياً
                                )

                                db.collection("users")
                                    .document(userId)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        mProgressDialog.dismiss()
                                        Toast.makeText(
                                            this,
                                            "Account created.\nCheck your email to verify your account.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, SignIn::class.java))
                                    }
                                    .addOnFailureListener { e ->
                                        mProgressDialog.dismiss()
                                        Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                            } else {
                                mProgressDialog.dismiss()
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        mProgressDialog.dismiss()
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
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