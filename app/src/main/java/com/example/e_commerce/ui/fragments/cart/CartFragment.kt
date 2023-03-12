package com.example.e_commerce.ui.fragments.cart

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.utils.Constants.CHANNEL_DESCRIPTION
import com.example.e_commerce.utils.Constants.CHANNEL_ID
import com.example.e_commerce.utils.Constants.CHANNEL_NAME
import com.example.e_commerce.utils.Constants.NOTIFICATION_ID
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.round
import kotlin.math.roundToInt


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

    @SuppressLint("StringFormatInvalid")
    private fun showAlertDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.checkout)
            .setMessage(getString(R.string.checkout_dialog_message, binding.txtTotal.text.toString()))
            .setNegativeButton(R.string.cancel){_,_->}
            .setPositiveButton(R.string.order){_,_->
                removeCartProducts()
                showNotification()
            }
            .create()
            .show()
    }

    private fun removeCartProducts() {
        firebaseViewModel.getCart()
        firebaseViewModel.getCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading->{
                    binding.pbCart.show()
                }
                is Resource.Success->{
                    resource.data?.forEach { product ->
                        firebaseViewModel.deleteCart(product)
                    }
                    binding.pbCart.hide()
                }
                is Resource.Error->{
                    binding.pbCart.hide()
                }
            }
        })
    }

    private fun showNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            notificationManager().createNotificationChannel(channel)
        }

        // Create intent to launch the application
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireContext().packageName)
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent,0)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_shopping_cart)
            .setContentTitle(getString(R.string.order_status))
            .setContentText(getString(R.string.order_placed_successfully))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification
        notificationManager().notify(NOTIFICATION_ID, builder.build())
    }

    private fun notificationManager(): NotificationManager {
        return requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun showAlertDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_item_checkout, product.name.toString()))
            .setNegativeButton(R.string.no){_,_->}
            .setPositiveButton(R.string.yes){_,_->
                deleteProduct(product)
            }
            .create()
            .show()
    }

    private fun deleteProduct(product: Product) {
        firebaseViewModel.deleteCart(product)

        firebaseViewModel.deleteCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbCart.show()
                }
                is Resource.Success ->{
                    binding.pbCart.hide()
                    requireActivity().showToast(getString(R.string.removed_from_cart))
                }
                is Resource.Error ->{
                    binding.pbCart.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun retrieveAndSetProducts() {
        firebaseViewModel.getCart()

        firebaseViewModel.getCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbCart.show()
                }
                is Resource.Success ->{
                    binding.pbCart.hide()
                    cartAdapter.differCallBack.submitList(resource.data)

                    val prices = arrayListOf<Int>()
                    resource.data?.forEach { product ->
                        prices.add(product.price.toString().toInt())
                    }
                    calculateTotal(prices = prices)
                }
                is Resource.Error ->{
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
        binding.txtDeliveryCharge.text = ((deliveryCharge * 100.0).roundToInt() / 100.0).toString()
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