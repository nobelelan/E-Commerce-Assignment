package com.example.e_commerce.ui.verifyotp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityVerifyOtpBinding
import com.example.e_commerce.ui.main.MainActivity
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.VerifyInput.verifyCode
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

        editTextInputHandler()

        binding.btnVerify.setOnClickListener {
            val code1 = binding.edtCode1.text.toString().trim()
            val code2 = binding.edtCode2.text.toString().trim()
            val code3 = binding.edtCode3.text.toString().trim()
            val code4 = binding.edtCode4.text.toString().trim()
            val code5 = binding.edtCode5.text.toString().trim()
            val code6 = binding.edtCode6.text.toString().trim()
            if (verifyCode(code1, code2, code3, code4, code5, code6)){
                val finalCode = code1 + code2 + code3 + code4 + code5 + code6
                verificationId?.let {
                    binding.pbOtpVerify.show()
                    binding.btnVerify.hide()
                    val credential = PhoneAuthProvider.getCredential(it, finalCode)
                    signInWithPhoneAuthCredential(credential)
                }
            }else{
                showToast("Invalid Code")
            }
        }
    }

    private fun editTextInputHandler() {
        binding.apply {
            edtCode1.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode2.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode2.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode3.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode3.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode4.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode4.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode5.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode5.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode6.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
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