package com.example.e_commerce.ui.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.AdChangeLanguageBinding
import com.example.e_commerce.databinding.AdEditProfileBinding
import com.example.e_commerce.databinding.FragmentProfileBinding
import com.example.e_commerce.utils.Constants.BANGLA_LANG_CODE
import com.example.e_commerce.utils.Constants.ENGLISH_LANG_CODE
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.utils.Util.applySharedPref
import com.example.e_commerce.utils.Util.setLocal
import com.example.e_commerce.utils.VerifyInput.verifyProfileInfo
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        auth = Firebase.auth

        if (auth.currentUser == null){
            activity?.finish()
        }

        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        getProfileInfo()

        binding.imgEditProfile.setOnClickListener {
            editProfile()
        }

        binding.btnSignOut.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.signing_out)
                .setNegativeButton(R.string.cancel){_,_->}
                .setPositiveButton(R.string.proceed){_,_->
                    auth.signOut()
                    if (auth.currentUser == null){
                        activity?.finish()
                    }
                }
                .create()
                .show()
        }
        binding.btnChangeLanguage.setOnClickListener {
            val changeLanguageBinding = AdChangeLanguageBinding.inflate(LayoutInflater.from(requireContext()))
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.change_language)
                .setView(changeLanguageBinding.root)
                .setNegativeButton(R.string.cancel){_,_->}
                .setPositiveButton(R.string.done){_,_->
                    if (changeLanguageBinding.radioBtnBangla.isChecked){
                        applySharedPref(requireContext(), BANGLA_LANG_CODE)
                        setLocal(requireActivity(), BANGLA_LANG_CODE)
                    }else if (changeLanguageBinding.radioBtnEnglish.isChecked){
                        applySharedPref(requireContext(), ENGLISH_LANG_CODE)
                        setLocal(requireActivity(), ENGLISH_LANG_CODE)
                    }
                }
                .create()
                .show()
        }
    }

    private fun getProfileInfo() {
        firebaseViewModel.getProfile.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading -> {
                    binding.pbProfile.show()
                }
                is Resource.Success -> {
                    binding.apply {
                        txtUserName.text = resource.data?.name.toString()
                        txtPhoneNumber.text = resource.data?.phone.toString()
                        txtAddress.text = resource.data?.address.toString()
                    }
                    binding.pbProfile.hide()
                }
                is Resource.Error -> {
                    requireActivity().showToast(resource.message.toString())
                    binding.pbProfile.hide()
                }
            }
        })
        firebaseViewModel.getProfile()
    }

    private fun editProfile() {
        val profileBinding = AdEditProfileBinding.inflate(LayoutInflater.from(requireContext()))
        profileBinding.editUserName.setText(binding.txtUserName.text.toString())
        profileBinding.edtPhone.setText(binding.txtPhoneNumber.text.toString())
        profileBinding.edtAddress.setText(binding.txtAddress.text.toString())

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(profileBinding.root)
            .setNegativeButton(R.string.cancel){_,_->}
            .setPositiveButton(R.string.update){_,_->
                val name = profileBinding.editUserName.text.toString()
                val phone = profileBinding.edtPhone.text.toString()
                val address = profileBinding.edtAddress.text.toString()
                if (verifyProfileInfo(name, phone, address)){
                    binding.apply {
                        txtUserName.text = name
                        txtPhoneNumber.text = phone
                        txtAddress.text = address
                    }
                    updateProfile(name, phone, address)
                }else{
                    requireContext().showToast("Fields can't be empty!")
                }
            }.create().show()
    }

    private fun updateProfile(name: String, phone: String, address: String) {

        val profile = mapOf(
            "name" to name,
            "phone" to phone,
            "address" to address
        )
        firebaseViewModel.updateProfile.observe(viewLifecycleOwner, Observer { resource ->
            when(resource){
                is Resource.Loading -> {
                    binding.pbProfile.show()
                }
                is Resource.Success -> {
                    requireActivity().showToast(resource.data.toString())
                    binding.pbProfile.hide()
                }
                is Resource.Error -> {
                    requireActivity().showToast(resource.message.toString())
                    binding.pbProfile.hide()
                }
            }
        })
        firebaseViewModel.updateProfile(profile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}