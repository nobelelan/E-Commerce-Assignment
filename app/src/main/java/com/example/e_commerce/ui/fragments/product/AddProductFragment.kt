package com.example.e_commerce.ui.fragments.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentAddProductBinding
import com.example.e_commerce.utils.Constants.ADIDAS_SHOES
import com.example.e_commerce.utils.Constants.CONVERSE_SHOES
import com.example.e_commerce.utils.Constants.NIKE_SHOES
import com.example.e_commerce.utils.Constants.ROUND_GLASSES
import com.example.e_commerce.utils.Constants.SUN_GLASSES
import com.example.e_commerce.utils.Constants.TRANSPARENT_GLASSES
import com.example.e_commerce.utils.Constants.VARIETIES
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel


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
                            binding.btnSubmitProduct.setOnClickListener {
                                addVarieties()
                            }
                        }
                        "Shoes" ->{
                            binding.rgShoes.show()
                            binding.rgGlasses.hide()
                            binding.btnSubmitProduct.setOnClickListener {
                                when(binding.rgShoes.checkedRadioButtonId){
                                    R.id.rb_converse_shoes ->{
                                        addShoes(CONVERSE_SHOES)
                                    }
                                    R.id.rb_adidas ->{
                                        addShoes(ADIDAS_SHOES)
                                    }
                                    R.id.rb_nike ->{
                                        addShoes(NIKE_SHOES)
                                    }
                                }
                            }
                        }
                        "Glasses" ->{
                            binding.rgGlasses.show()
                            binding.rgShoes.hide()
                            binding.btnSubmitProduct.setOnClickListener {
                                when(binding.rgGlasses.checkedRadioButtonId){
                                    R.id.rb_round_glasses ->{
                                        addGlass(ROUND_GLASSES)
                                    }
                                    R.id.rb_transparent ->{
                                        addGlass(TRANSPARENT_GLASSES)
                                    }
                                    R.id.rb_sunglass ->{
                                        addGlass(SUN_GLASSES)
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

    private fun addShoes(type: String) {
        firebaseViewModel.addShoes(
            hashMapOf(
                "name" to binding.edtProductName.text.toString(),
                "type" to type,
                "url" to binding.edtProductUrl.text.toString(),
                "price" to binding.edtProductPrice.text.toString(),
                "description" to binding.edtProductDescription.text.toString(),
                "rating" to binding.edtProductRating.text.toString()
            )
        )
        firebaseViewModel.addShoes.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Success ->{
                    requireActivity().showToast("Submitted Successfully!")
                    binding.pbAddProduct.hide()
                }
                is Resource.Loading ->{
                    binding.pbAddProduct.show()
                }
                is Resource.Error ->{
                    requireActivity().showToast("Something went wrong!")
                    binding.pbAddProduct.hide()
                }
            }
        })
    }
    private fun addVarieties() {
        firebaseViewModel.addVarieties(
            hashMapOf(
                "name" to binding.edtProductName.text.toString(),
                "type" to VARIETIES,
                "url" to binding.edtProductUrl.text.toString(),
                "price" to binding.edtProductPrice.text.toString(),
                "description" to binding.edtProductDescription.text.toString(),
                "rating" to binding.edtProductRating.text.toString()
            )
        )
        firebaseViewModel.addVarieties.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Success ->{
                    requireActivity().showToast("Submitted Successfully!")
                    binding.pbAddProduct.hide()
                }
                is Resource.Loading ->{
                    binding.pbAddProduct.show()
                }
                is Resource.Error ->{
                    requireActivity().showToast("Something went wrong!")
                    binding.pbAddProduct.hide()
                }
            }
        })
    }

    private fun addGlass(type: String) {
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
        firebaseViewModel.addGlasses.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Success ->{
                    requireActivity().showToast("Submitted Successfully!")
                    binding.pbAddProduct.hide()
                }
                is Resource.Loading ->{
                    binding.pbAddProduct.show()
                }
                is Resource.Error ->{
                    requireActivity().showToast("Something went wrong!")
                    binding.pbAddProduct.hide()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}