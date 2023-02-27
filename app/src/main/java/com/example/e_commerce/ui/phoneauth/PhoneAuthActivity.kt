package com.example.e_commerce.ui.phoneauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_commerce.databinding.ActivityPhoneAuthBinding
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

        binding.btnSendOtp.setOnClickListener {
            val phone = binding.edtPhone.text.toString().trim()
            if (phone.isEmpty()){
                showToast("Invalid phone number")
            }else if (phone.length < 10){
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
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

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
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
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
            .setPhoneNumber("+880${binding.edtPhone.text.toString().trim()}")      // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
            .setActivity(this)                          // Activity (for callback binding)
            .setCallbacks(callbacks)                    // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}