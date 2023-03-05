package com.example.e_commerce.ui.fragments.cart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.round


class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartAdapter by lazy { CartAdapter() }

    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        setUpCartRecyclerview()
        retrieveAndSetProducts()

        cartAdapter.setOnClickListener(object : CartAdapter.OnItemClickListener{
            override fun onLongClick(product: Product) {
                showAlertDialog(product)
            }

            @SuppressLint("SetTextI18n")
            override fun onPlusClick(price: Int) {
                val preSubTotal = binding.txtSubTotal.text.toString()
                binding.txtSubTotal.text = (preSubTotal.toInt() + price).toString()

                val subTotal = binding.txtSubTotal.text.toString()
                deliveryCharge(subTotal.toInt())
                val totalPrice = subTotal.toInt() + deliveryCharge(subTotal.toInt()).toInt()
                binding.txtTotal.text = totalPrice.toString()
            }

            override fun onMinusClick(price: Int) {
                val preSubTotal = binding.txtSubTotal.text.toString()
                binding.txtSubTotal.text = (preSubTotal.toInt() - price).toString()

                val subTotal = binding.txtSubTotal.text.toString()
                deliveryCharge(subTotal.toInt())
                val totalPrice = subTotal.toInt() + deliveryCharge(subTotal.toInt()).toInt()
                binding.txtTotal.text = totalPrice.toString()
            }
        })

        binding.btnCheckout.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog(){
        var message = ""
        firebaseViewModel.getProfile()
        firebaseViewModel.getProfile.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.SUCCESS->{
                    message = "Your products are ready to be ordered at address: ${resource.data?.address}, " +
                            "contact: ${resource.data?.phone}. Please proceed if everything's ok."

                    AlertDialog.Builder(requireContext())
                        .setTitle("Checkout")
                        .setMessage(message)
                        .setNegativeButton("Cancel"){_,_->}
                        .setPositiveButton("Order"){_,_->
                            removeCartProducts()
                        }
                        .create()
                        .show()

                    binding.pbCart.hide()
                }
                Resource.Status.LOADING->{
                    binding.pbCart.show()
                }
                Resource.Status.ERROR->{
                    binding.pbCart.hide()
                }
            }
        })
    }

    private fun removeCartProducts() {
        firebaseViewModel.getCart()
        firebaseViewModel.getCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource.status){
                Resource.Status.LOADING->{
                    binding.pbCart.show()
                }
                Resource.Status.SUCCESS->{
                    resource.data?.forEach { product ->
                        firebaseViewModel.deleteCart(product)
                    }
                    requireActivity().showToast("Your order has been placed successfully!")
                    binding.pbCart.hide()
                }
                Resource.Status.ERROR->{
                    binding.pbCart.hide()
                }
            }
        })
    }

    private fun showAlertDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to remove '${product.name.toString()}' from the cart.")
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

                    val prices = arrayListOf<Int>()
                    resource.data?.forEach { product ->
                        prices.add(product.price.toString().toInt())
                    }
                    calculateTotal(prices = prices)
                }
                Resource.Status.ERROR ->{
                    binding.pbCart.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }


    private fun calculateTotal(prices: ArrayList<Int> = arrayListOf()) {
        var subTotal = 0
        prices.forEach {
            subTotal += it
        }

        binding.txtSubTotal.text = subTotal.toString()

        deliveryCharge(subTotal)

        val totalPrice = subTotal + deliveryCharge(subTotal).toInt()
        binding.txtTotal.text = totalPrice.toString()
    }
    @SuppressLint("SetTextI18n")
    private fun deliveryCharge(subTotal: Int) : Double{
        val deliveryCharge = (0.8/100.0) * subTotal
        binding.txtDeliveryCharge.text = "%.0f".format(deliveryCharge)
        return deliveryCharge
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