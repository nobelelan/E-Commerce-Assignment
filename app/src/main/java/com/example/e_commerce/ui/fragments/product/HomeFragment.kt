package com.example.e_commerce.ui.fragments.product

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentHomeBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.ui.fragments.product.adapter.GlassAdapter
import com.example.e_commerce.ui.fragments.product.adapter.ShoeAdapter
import com.example.e_commerce.ui.phoneauth.PhoneAuthActivity
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val shoeAdapter by lazy { ShoeAdapter() }
    private val glassAdapter by lazy { GlassAdapter() }

    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        setUpShoeRecyclerView()
        setUpGlassRecyclerView()

        retrieveAndSetShoes()
        retrieveAndSetGlasses()

        shoeAdapter.setOnClickListener(object : ShoeAdapter.OnItemClickListener{
            override fun onProductClick(product: Product) {
                requireActivity().showToast("Product clicked")
            }

            override fun onFavIconClick(product: Product) {
                requireActivity().showToast("Love clicked")
            }
        })
        glassAdapter.setOnClickListener(object : GlassAdapter.OnItemClickListener{
            override fun onProductClick(product: Product) {
                requireActivity().showToast("Product clicked")
            }

            override fun onFavIconClick(product: Product) {
                requireActivity().showToast("Love clicked")
            }
        })
    }

    private fun retrieveAndSetGlasses() {
        firebaseViewModel.getGlasses.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.LOADING ->{
                    binding.pbHome.show()
                }
                Resource.Status.SUCCESS ->{
                    glassAdapter.differCallBack.submitList(resource.data)
                    binding.pbHome.hide()
                }
                Resource.Status.ERROR ->{
                    requireActivity().showToast(resource.message.toString())
                    binding.pbHome.hide()
                }
            }
        })
        firebaseViewModel.getGlasses()
    }

    private fun retrieveAndSetShoes() {
        firebaseViewModel.getShoes.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.LOADING ->{
                    binding.pbHome.show()
                }
                Resource.Status.SUCCESS ->{
                    shoeAdapter.differCallBack.submitList(resource.data)
                    binding.pbHome.hide()
                }
                Resource.Status.ERROR ->{
                    requireActivity().showToast(resource.message.toString())
                    binding.pbHome.hide()
                }
            }
        })
        firebaseViewModel.getShoes()
    }

    private fun setUpGlassRecyclerView() = binding.rvHomeGlass.apply {
        adapter = glassAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUpShoeRecyclerView() = binding.rvHomeShoe.apply {
        adapter = shoeAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}