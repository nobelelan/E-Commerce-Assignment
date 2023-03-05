package com.example.e_commerce.ui.fragments.product

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentDetailsBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel


class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailsFragmentArgs>()

    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        val product = args.currentProduct

        binding.apply {
            txtProductPrice.text = "${product.price}/= Taka"
            rbDetails.rating = product.rating!!.toFloat()
            txtProductName.text = product.name
            txtProductDetails.text = product.details
            Glide.with(requireContext()).load(product.url).into(imgProduct)

            btnAddToCart.setOnClickListener {
                addToCart(product)
            }
        }
    }

    private fun addToCart(product: Product) {
        firebaseViewModel.addCart(
            hashMapOf(
                "name" to product.name.toString(),
                "url" to product.url.toString(),
                "price" to product.price.toString(),
                "details" to product.details.toString(),
                "rating" to product.rating.toString()
            )
        )

        firebaseViewModel.addCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.LOADING ->{
                    binding.pbDetails.show()
                }
                Resource.Status.SUCCESS ->{
                    requireActivity().showToast(resource.data.toString())
                    binding.pbDetails.hide()
                }
                Resource.Status.ERROR ->{
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}