package com.example.deliveryapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.deliveryapp.databinding.ActivityForgetPassword2Binding
import com.google.firebase.auth.FirebaseAuth
import papaya.`in`.sendmail.SendMail
import kotlin.random.Random
import kotlin.random.nextInt

class ForgetPassword2 : AppCompatActivity() {
    lateinit var binding:ActivityForgetPassword2Binding
    var startTime:Long=60*1000
    var time:Long=startTime
    var isTimerRunning:Boolean=true
    lateinit var email1:String
    lateinit var mAuth: FirebaseAuth
    var random:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityForgetPassword2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        email1= intent.getStringExtra("email").toString()
        binding.email.text="${email1?.substring(0,1)}****@gmail.com"
        binding.otp1.doOnTextChanged { text, start, before, count ->
            if (binding.otp1.text.toString().isNotEmpty()){
                binding.otp2.requestFocus()
            }
        }
        binding.otp2.doOnTextChanged { text, start, before, count ->
            if (binding.otp2.text.toString().isNotEmpty()){
                binding.otp3.requestFocus()
            }
        }
        binding.otp3.doOnTextChanged { text, start, before, count ->
            if (binding.otp3.text.toString().isNotEmpty()){
                binding.otp4.requestFocus()
            }
        }
        binding.otp4.doOnTextChanged { text, start, before, count ->
            if (binding.otp4.text.toString().isNotEmpty()){
                binding.otp5.requestFocus()
            }
        }
        binding.otp5.doOnTextChanged { text, start, before, count ->
            if (binding.otp5.text.toString().isNotEmpty()){
                binding.otp6.requestFocus()
            }
        }
        binding.signUp.setOnClickListener {
            var otp="${binding.otp1.text}${binding.otp2.text}${binding.otp3.text}${binding.otp4.text}${binding.otp5.text}${binding.otp6.text}"
            if(binding.otp1.text.isEmpty()){
                Toast.makeText(this, "enter code", Toast.LENGTH_SHORT).show();
//                var intent= Intent(this,ForgetPassword3::class.java)
//                startActivity(intent)
            }
            else if(!otp.equals(random.toString())){
                Toast.makeText(this, "Wornd OTP", Toast.LENGTH_SHORT).show();
            }

        }
        binding.resend.setOnClickListener {
            if(isTimerRunning){
                startTimer()
            }
        }
    }
    fun random(){
         random=Random.nextInt(100000..999999)
        var email=SendMail("abdessamadoukaamouch@gmail.com","abdessamad@12345@",email1,"Login ","Your OTP: $random")
        email.execute()
    }




    fun startTimer(){
        var timer= object : CountDownTimer(startTime,1*1000) {
            override fun onTick(millisUntilFinished: Long) {
                time=millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                binding.resend.text="Resend"
                isTimerRunning=true
                mAuth.sendPasswordResetEmail(email1).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this@ForgetPassword2, "Email sent", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this@ForgetPassword2, "${it.exception?.message}", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }.start()
        isTimerRunning=false
    }
    fun updateTimerText(){
        var minute=time.div(1000).div(60)
        var second=time.div(1000) % 60
        var formattedTime=String.format("%02d:%02d",minute,second)
        binding.resend.text=formattedTime
    }
}