package com.example.e_commerce.ui.phoneauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_commerce.databinding.ActivityPhoneAuthBinding
import com.example.e_commerce.ui.main.MainActivity
import com.example.e_commerce.ui.verifyotp.VerifyOtpActivity
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneAuthBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnSendOtp.setOnClickListener {
            val phone = binding.edtPhone.text.toString().trim()
            if (phone.isEmpty()){
                showToast("Invalid phone number")
            }else if (phone.length != 10){
                showToast("Type valid phone number")
            }else{
                sendOtp()
            }
        }
    }

    private fun sendOtp() {

        binding.pbPhoneAuth.show()
        binding.btnSendOtp.hide()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.pbPhoneAuth.hide()
                binding.btnSendOtp.show()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    e.localizedMessage?.let {
                        showToast(it)
                    }
                } else if (e is FirebaseTooManyRequestsException) {
                    e.localizedMessage?.let {
                        showToast(it)
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.pbPhoneAuth.hide()
                binding.btnSendOtp.show()

                showToast("Code sent.")

                val intent = Intent(this@PhoneAuthActivity, VerifyOtpActivity::class.java)
                intent.putExtra("phone", binding.edtPhone.text.toString().trim())
                intent.putExtra("verificationId", verificationId)
                startActivity(intent)
            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+880${binding.edtPhone.text.toString().trim()}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.pbPhoneAuth.hide()
                    binding.btnSendOtp.show()
                    showToast("Verification successful!")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    binding.pbPhoneAuth.hide()
                    binding.btnSendOtp.show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showToast("Verification failed!")
                    }else{
                        showToast("Something went wrong!")
                    }
                }
            }
    }
}