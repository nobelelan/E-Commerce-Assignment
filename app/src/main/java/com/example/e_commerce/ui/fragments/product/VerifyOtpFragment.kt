package com.example.e_commerce.ui.fragments.product

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.e_commerce.R
import com.example.e_commerce.broadcast.SmsBroadcastReceiver
import com.example.e_commerce.broadcast.SmsBroadcastReceiverListener
import com.example.e_commerce.databinding.FragmentVerifyOtpBinding
import com.example.e_commerce.utils.Constants.BANGLA_LANG_CODE
import com.example.e_commerce.utils.Constants.ENGLISH_LANG_CODE
import com.example.e_commerce.utils.Constants.LANGUAGE_CODE
import com.example.e_commerce.utils.Constants.PHONE_AUTH_SHARED_PREF_KEY
import com.example.e_commerce.utils.Constants.SHARED_PREF_KEY
import com.example.e_commerce.utils.ExtensionFunctions.disable
import com.example.e_commerce.utils.ExtensionFunctions.enable
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.utils.Util.applySharedPref
import com.example.e_commerce.utils.Util.setLocal
import com.example.e_commerce.utils.VerifyInput
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class VerifyOtpFragment : Fragment() {

    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseViewModel: FirebaseViewModel

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private val args by navArgs<VerifyOtpFragmentArgs>()

    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private val REQ_USER_CONSENT = 200


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

        binding.txtOtpSentNumTxt.text = "Code was sent to ${args.codeSentData.phone}"

        startSmartUserConsent()

        editTextInputHandler()

        binding.apply {
            btnVerify.enable()
            spinnerOtp.hide()
        }
        binding.txtResendOtp.setOnClickListener {
            resendOtp()
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
                val credential =
                    args.codeSentData.verificationId.let { it1 -> PhoneAuthProvider.getCredential(it1, finalCode) }
                signInWithPhoneAuthCredential(credential)
            }else{
                requireActivity().showToast("Invalid Code")
            }
        }
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(requireContext())
        client.startSmsUserConsent(null)
    }

    private fun registerBroadcastReceiver(){
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener = object : SmsBroadcastReceiverListener{
            override fun onSuccess(intent: Intent?) {
                startActivityForResult(intent, REQ_USER_CONSENT)
            }

            override fun onFailure() {}
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT){
            if (resultCode == RESULT_OK && data != null){
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                retrieveOtpFromMessage(message)
            }
        }
    }

    private fun retrieveOtpFromMessage(message: String?) {
        val otpPattern = Pattern.compile("(|^)\\d{6}")
        val matcher = otpPattern.matcher(message)
        if (matcher.find()){
            val otp = matcher.group(0)
            val editTexts = listOf(binding.edtCode1, binding.edtCode2, binding.edtCode3,
                binding.edtCode4, binding.edtCode5, binding.edtCode6)
            val otps = arrayListOf<Int>()
            otp?.forEach {
                otps.add(it.digitToInt())
            }
            otps.zip(editTexts){otpInt,editText->
                editText.setText(otpInt.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }

    private fun resendOtp() {
        binding.pbOtpVerify.show()
        binding.btnVerify.hide()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.pbOtpVerify.hide()
                binding.btnVerify.show()

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
                verification: String,
                tokenn: PhoneAuthProvider.ForceResendingToken
            ) {

                binding.pbOtpVerify.hide()
                binding.btnVerify.show()

                requireActivity().showToast("Code sent.")
            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+880${args.codeSentData.phone}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(args.codeSentData.token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
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

                    firebaseViewModel.getProfile()
                    if (firebaseViewModel.getProfile.value == null){
                        firebaseViewModel.setProfile(
                            hashMapOf(
                                "name" to "User Name",
                                "phone" to auth.currentUser!!.phoneNumber!!,
                                "address" to "Address",
                                "role" to "User"
                            )
                        )
                    }else{
                        firebaseViewModel.getProfile()
                        firebaseViewModel.getProfile.observe(viewLifecycleOwner, Observer { resource ->
                            when(resource){
                                is Resource.Success ->{
                                    resource.data?.let { profile->
                                        if (profile.name != null && profile.address != null && profile.role != null){
                                            firebaseViewModel.setProfile(
                                                hashMapOf(
                                                    "name" to profile.name,
                                                    "phone" to profile.phone,
                                                    "address" to profile.address,
                                                    "role" to profile.role
                                                )
                                            )
                                        }
                                    }
//                                        ?:firebaseViewModel.setProfile(
//                                        hashMapOf(
//                                            "name" to "User Name",
//                                            "phone" to auth.currentUser?.phoneNumber.toString(),
//                                            "address" to "Address",
//                                            "role" to "user"
//                                        )
//                                    )
                                }
                                is Resource.Loading ->{}
                                is Resource.Error ->{}
                            }
                        })
                    }

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