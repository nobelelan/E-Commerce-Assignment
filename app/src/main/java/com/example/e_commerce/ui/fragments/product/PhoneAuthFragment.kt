package com.example.e_commerce.ui.fragments.product

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentPhoneAuthBinding
import com.example.e_commerce.utils.Constants.BANGLA_LANG_CODE
import com.example.e_commerce.utils.Constants.ENGLISH_LANG_CODE
import com.example.e_commerce.utils.Constants.LANGUAGE_CODE
import com.example.e_commerce.utils.Constants.SHARED_PREF_KEY
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Util.setLocal
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class PhoneAuthFragment : Fragment() {

    private var _binding: FragmentPhoneAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPhoneAuthBinding.bind(view)

        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.hide()

        auth = Firebase.auth

        if (auth.currentUser != null){
            findNavController().navigate(R.id.action_phoneAuthFragment_to_productFragment)
            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.show()

            val sharedPref = context?.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
            val langCode = sharedPref?.getString(LANGUAGE_CODE, ENGLISH_LANG_CODE)
            langCode?.let {
                setLocal(requireActivity(),it)
            }
        }

        binding.btnSendOtp.setOnClickListener {
            val phone = binding.edtPhone.text.toString().trim()
            if (phone.isEmpty()){
                requireActivity().showToast("Invalid phone number")
            }else if (phone.length != 10){
                requireActivity().showToast("Type valid phone number")
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
                        requireActivity().showToast(it)
                    }
                } else if (e is FirebaseTooManyRequestsException) {
                    e.localizedMessage?.let {
                        requireActivity().showToast(it)
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.pbPhoneAuth.hide()
                binding.btnSendOtp.show()

                requireActivity().showToast("Code sent.")

                val action = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToVerifyOtpFragment(
                    binding.edtPhone.text.toString().trim(),
                    verificationId
                )
                findNavController().navigate(action)
            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+880${binding.edtPhone.text.toString().trim()}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.pbPhoneAuth.hide()
                    binding.btnSendOtp.show()
                    requireActivity().showToast("Verification successful!")
                    findNavController().navigate(R.id.action_phoneAuthFragment_to_productFragment)
                } else {
                    binding.pbPhoneAuth.hide()
                    binding.btnSendOtp.show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        requireActivity().showToast("Verification failed!")
                    }else{
                        requireActivity().showToast("Something went wrong!")
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}