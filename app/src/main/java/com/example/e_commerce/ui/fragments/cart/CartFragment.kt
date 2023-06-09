package com.example.e_commerce.ui.fragments.cart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.notification.NotificationData
import com.example.e_commerce.notification.PushNotification
import com.example.e_commerce.R
import com.example.e_commerce.notification.RetrofitInstance
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCProductInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCShipmentInfoInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


const val TOPIC = "/topics/myTopic2"
class CartFragment : Fragment(), SSLCTransactionResponseListener {

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
//            paymentGateWay()
        }
    }

    private fun paymentGateWay() {
        val sslCommerzInitialization = SSLCommerzInitialization(
            "elan641ade0b8132c",
            "elan641ade0b8132c@ssl",
            100.0,
            SSLCCurrencyType.BDT,
            "123456789098765",
            "yourProductType",
            SSLCSdkType.TESTBOX
        )
        val customerInfoInitializer = SSLCCustomerInfoInitializer(
            "customer name",
            "customer email",
            "address",
            "dhaka",
            "1214",
            "Bangladesh",
            "123456"
        )
        val productInitializer = SSLCProductInitializer("food", "food",
            SSLCProductInitializer.ProductProfile.TravelVertical("Travel", "10",
                "A", "12", "Dhk-Syl"))
        val shipmentInfoInitializer = SSLCShipmentInfoInitializer("Courier",
            2, SSLCShipmentInfoInitializer.ShipmentDetails("AA", "Address 1",
                "Dhaka", "1000", "BD"))

        IntegrateSSLCommerz
            .getInstance(requireContext())
            .addSSLCommerzInitialization(sslCommerzInitialization)
            .addCustomerInfoInitializer(customerInfoInitializer)
            .addProductInitializer(productInitializer)
            .addShipmentInfoInitializer(shipmentInfoInitializer)
            .buildApiCall(this)
    }
    override fun transactionSuccess(sslcTransactionInfoModel: SSLCTransactionInfoModel?) {
        sslcTransactionInfoModel?.let {
            val transactionStatus = it.apiConnect + "\n" + it.status
            Snackbar.make(binding.root, transactionStatus, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun transactionFail(fail: String?) {
        fail?.let {
            Snackbar.make(binding.root, "failed:$it", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun merchantValidationError(error: String?) {
        error?.let {
            Snackbar.make(binding.root, "error:$it", Snackbar.LENGTH_LONG).show()
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
                PushNotification(
                    NotificationData(getString(R.string.order_status),
                    getString(R.string.order_placed_successfully)),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
                requireActivity().showToast("${R.string.order_placed_successfully}")
            }
            .create()
            .show()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)
    }

    private fun removeCartProducts() {
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
        firebaseViewModel.getCart.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbCart.show()
                }
                is Resource.Success ->{
                    binding.pbCart.hide()
                    if (resource.data?.isEmpty() == true){
                        binding.txtNoItem.show()
                        binding.rvCart.hide()
                    }else{
                        binding.txtNoItem.hide()
                        binding.rvCart.show()
                        cartAdapter.differCallBack.submitList(resource.data)

                        val prices = arrayListOf<Int>()
                        resource.data?.forEach { product ->
                            prices.add(product.price.toString().toInt())
                        }
                        calculateTotal(prices = prices)
                    }
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