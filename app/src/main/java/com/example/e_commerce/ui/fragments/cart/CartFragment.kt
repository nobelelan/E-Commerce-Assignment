package com.example.e_commerce.ui.fragments.cart

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel


class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartAdapter by lazy { CartAdapter() }

    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)

        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        setUpCartRecyclerview()
        retrieveAndSetProducts()

        cartAdapter.setOnClickListener(object : CartAdapter.OnItemClickListener{
            override fun onLongClick(product: Product) {
                showAlertDialog(product)
            }
        })
    }

    private fun showAlertDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to remove this item from the cart.")
            .setNegativeButton("No"){_,_->}
            .setPositiveButton("Yes"){_,_->
                deleteProduct(product)
            }
            .create()
            .show()
    }

    private fun deleteProduct(product: Product) {
        firebaseViewModel.deleteCart(product)

        firebaseViewModel.deleteCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.LOADING ->{
                    binding.pbCart.show()
                }
                Resource.Status.SUCCESS ->{
                    binding.pbCart.hide()
                    requireActivity().showToast(resource.data.toString())
                }
                Resource.Status.ERROR ->{
                    binding.pbCart.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun retrieveAndSetProducts() {
        firebaseViewModel.getCart()

        firebaseViewModel.getCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.LOADING ->{
                    binding.pbCart.show()
                }
                Resource.Status.SUCCESS ->{
                    binding.pbCart.hide()
                    cartAdapter.differCallBack.submitList(resource.data)
                }
                Resource.Status.ERROR ->{
                    binding.pbCart.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun setUpCartRecyclerview() = binding.rvCart.apply {
        adapter = cartAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}