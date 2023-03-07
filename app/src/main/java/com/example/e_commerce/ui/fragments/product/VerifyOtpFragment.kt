package com.example.e_commerce.ui.fragments.product

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentVerifyOtpBinding
import com.example.e_commerce.utils.Constants.BANGLA_LANG_CODE
import com.example.e_commerce.utils.Constants.ENGLISH_LANG_CODE
import com.example.e_commerce.utils.Constants.LANGUAGE_CODE
import com.example.e_commerce.utils.Constants.SHARED_PREF_KEY
import com.example.e_commerce.utils.ExtensionFunctions.disable
import com.example.e_commerce.utils.ExtensionFunctions.enable
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Util.applySharedPref
import com.example.e_commerce.utils.Util.setLocal
import com.example.e_commerce.utils.VerifyInput
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale


class VerifyOtpFragment : Fragment() {

    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseViewModel: FirebaseViewModel

    private val args by navArgs<VerifyOtpFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_otp, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVerifyOtpBinding.bind(view)

        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.hide()

        auth = Firebase.auth

        val phone = args.phone
        val verificationId = args.verificationId

        binding.txtOtpSentNumTxt.text = "Code was sent to $phone"

        editTextInputHandler()

        binding.apply {
            btnVerify.enable()
            spinnerOtp.hide()
        }
        binding.btnVerify.setOnClickListener {
            val code1 = binding.edtCode1.text.toString().trim()
            val code2 = binding.edtCode2.text.toString().trim()
            val code3 = binding.edtCode3.text.toString().trim()
            val code4 = binding.edtCode4.text.toString().trim()
            val code5 = binding.edtCode5.text.toString().trim()
            val code6 = binding.edtCode6.text.toString().trim()
            if (VerifyInput.verifyCode(code1, code2, code3, code4, code5, code6)){
                val finalCode = code1 + code2 + code3 + code4 + code5 + code6
                binding.pbOtpVerify.show()
                binding.btnVerify.hide()
                val credential = PhoneAuthProvider.getCredential(verificationId, finalCode)
                signInWithPhoneAuthCredential(credential)
            }else{
                requireActivity().showToast("Invalid Code")
            }
        }
    }

    private fun editTextInputHandler() {
        binding.apply {
            edtCode1.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode2.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode2.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode3.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode3.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode4.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode4.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    edtCode5.requestFocus()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            edtCode5.addTextChangedListener(object: TextWatcher {
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
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.pbOtpVerify.hide()
                    binding.btnVerify.show()
                    binding.btnVerify.disable()
                    requireActivity().showToast("Verification successful!")
                    firebaseViewModel = ViewModelProvider(this)[FirebaseViewModel::class.java]
                    firebaseViewModel.addProfile(
                        hashMapOf(
                            "name" to "User Name",
                            "phone" to auth.currentUser?.phoneNumber.toString(),
                            "address" to "Address"
                        )
                    )
                    binding.apply {
                        spinnerOtp.show()
                        setUpAndRetrieveLangFromSpinner()
                    }

                } else {
                    // Sign in failed, display a message and update the UI
                    binding.pbOtpVerify.hide()
                    binding.btnVerify.show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        requireActivity().showToast("Verification failed!")
                    }else{
                        requireActivity().showToast("Something went wrong!")
                    }
                }
            }
    }

    private fun setUpAndRetrieveLangFromSpinner() {
        val languages = arrayListOf("Select Language", "English", "বাংলা")

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOtp.apply {
            adapter = arrayAdapter
            setSelection(0)
            onItemSelectedListener = object : OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    val selectedLang = parent?.getItemAtPosition(position).toString()
                    when(selectedLang){
                        "English" ->{
                            navigateToProductFragment(requireActivity(), ENGLISH_LANG_CODE)
                            applySharedPref(requireContext(),ENGLISH_LANG_CODE)
                        }
                        "বাংলা" ->{
                            navigateToProductFragment(requireActivity(), BANGLA_LANG_CODE)
                            applySharedPref(requireContext(),BANGLA_LANG_CODE)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun navigateToProductFragment(activity: Activity?, langCode: String) {
        activity?.let {
            setLocal(requireActivity(), langCode)
            findNavController().navigate(R.id.action_verifyOtpFragment_to_productFragment)
            activity.findViewById<BottomNavigationView>(R.id.bottom_nav)?.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}