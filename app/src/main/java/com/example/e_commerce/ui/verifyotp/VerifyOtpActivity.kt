package com.example.e_commerce.ui.verifyotp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityVerifyOtpBinding
import com.example.e_commerce.ui.main.MainActivity
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding

    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val phone = intent.getStringExtra("phone")
        val verificationId = intent.getStringExtra("verificationId")

        binding.txtOtpSentNumTxt.text = "Code was sent to $phone"

        binding.btnVerify.setOnClickListener {
            val code = binding.edtCode.text.toString().trim()
            if (code.isEmpty() && code.length < 6){
                showToast("Invalid Code")
            }else{
                verificationId?.let {
                    binding.pbOtpVerify.show()
                    binding.btnVerify.hide()
                    val credential = PhoneAuthProvider.getCredential(it, code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.pbOtpVerify.hide()
                    binding.btnVerify.show()
                    showToast("Verification successful!")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Sign in failed, display a message and update the UI
                    binding.pbOtpVerify.hide()
                    binding.btnVerify.show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showToast("Verification failed!")
                    }else{
                        showToast("Something went wrong!")
                    }
                }
            }
    }
}