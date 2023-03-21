package com.example.e_commerce.ui.fragments.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentAddProductBinding
import com.example.e_commerce.utils.Constants.ALL_GLASSES
import com.example.e_commerce.utils.Constants.SUN_GLASSES
import com.example.e_commerce.utils.Constants.TRANSPARENT_GLASSES
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth


class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddProductBinding.bind(view)

        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        setUpSpinner()
    }

    private fun setUpSpinner() {
        val products = arrayListOf("Varieties", "Shoes", "Glasses")

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            products
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductType.apply {
            adapter = arrayAdapter
            setSelection(0)
            onItemSelectedListener = object : OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    val selectedProductType = parent?.getItemAtPosition(position).toString()
                    when(selectedProductType){
                        "Varieties" ->{
                            binding.rgShoes.hide()
                            binding.rgGlasses.hide()
                            //add to varieties
                        }
                        "Shoes" ->{
                            binding.rgShoes.show()
                            binding.rgGlasses.hide()
                            when(binding.rgShoes.checkedRadioButtonId){
                                R.id.rb_all_shoes ->{
                                    //add to all shoes
                                }
                                R.id.rb_adidas ->{
                                    //add to adidas
                                }
                                R.id.rb_nike ->{
                                    //add to nike
                                }
                            }
                        }
                        "Glasses" ->{
                            binding.rgGlasses.show()
                            binding.rgShoes.hide()
                            binding.btnSubmitProduct.setOnClickListener {
                                when(binding.rgGlasses.checkedRadioButtonId){
                                    R.id.rb_all_glasses ->{
                                        //add to all glasses
                                        addProduct(ALL_GLASSES)
                                    }
                                    R.id.rb_transparent ->{
                                        //add to transparent
                                        addProduct(TRANSPARENT_GLASSES)
                                    }
                                    R.id.rb_sunglass ->{
                                        //add to sunglass
                                        addProduct(SUN_GLASSES)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun addProduct(type: String) {
        firebaseViewModel.addGlasses(
            hashMapOf(
                "name" to binding.edtProductName.text.toString(),
                "type" to type,
                "url" to binding.edtProductUrl.text.toString(),
                "price" to binding.edtProductPrice.text.toString(),
                "description" to binding.edtProductDescription.text.toString(),
                "rating" to binding.edtProductRating.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}