package com.example.e_commerce.ui.fragments.product

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
import com.example.e_commerce.ui.fragments.product.adapter.VarietiesAdapter
import com.example.e_commerce.utils.Constants.ADIDAS_SHOES
import com.example.e_commerce.utils.Constants.CONVERSE_SHOES
import com.example.e_commerce.utils.Constants.NIKE_SHOES
import com.example.e_commerce.utils.Constants.ROUND_GLASSES
import com.example.e_commerce.utils.Constants.SUN_GLASSES
import com.example.e_commerce.utils.Constants.TRANSPARENT_GLASSES
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
    private val varietiesAdapter by lazy { VarietiesAdapter() }

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

        decideUserAccess()

        setUpShoeRecyclerView()
        setUpGlassRecyclerView()
        setUpVarietiesRecyclerView()

        retrieveAndSetShoes()
        retrieveAndSetGlasses()
        retrieveAndSetVarieties()

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_addProductFragment)
        }

        shoeAdapter.setOnClickListener(object : ShoeAdapter.OnItemClickListener{
            override fun onProductClick(product: Product) {
                navigateToDetails(product)
            }

            override fun onFavIconClick(product: Product) {
                addProductToWishList(product)
            }

            override fun onDeleteClick(product: Product) {
                firebaseViewModel.deleteShoes(product)
            }

            override fun onViewCreated(view: ImageView) {
                retrieveProfileData(view)
            }
        })
        glassAdapter.setOnClickListener(object : GlassAdapter.OnItemClickListener{
            override fun onProductClick(product: Product) {
                navigateToDetails(product)
            }

            override fun onFavIconClick(product: Product) {
                addProductToWishList(product)
            }

            override fun onDeleteClick(product: Product) {
                firebaseViewModel.deleteGlasses(product)
            }

            override fun onViewCreated(view: ImageView) {
                retrieveProfileData(view)
            }
        })
        varietiesAdapter.setOnClickListener(object : VarietiesAdapter.OnItemClickListener{
            override fun onProductClick(product: Product) {
                navigateToDetails(product)
            }

            override fun onFavIconClick(product: Product) {
                addProductToWishList(product)
            }

            override fun onDeleteClick(product: Product) {
                firebaseViewModel.deleteVarieties(product)
            }

            override fun onViewCreated(view: ImageView) {
                retrieveProfileData(view)
            }
        })
    }

    private fun decideUserAccess() {
        retrieveProfileData()
    }

    private fun retrieveProfileData(view: View? = null) {
        firebaseViewModel.getProfile()
        firebaseViewModel.getProfile.observe(viewLifecycleOwner, Observer{resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbHome.show()
                }
                is Resource.Success ->{
                    binding.pbHome.hide()
                    resource.data?.role.let {
                        hideAndShowView(it, view)
                    }

                }
                is Resource.Error ->{
                    binding.pbHome.hide()
                }
            }
        })
    }

    private fun hideAndShowView(it: String?, view: View? = null) {
        it?.let { role->
            if (role == "user"){
                binding.fabAddProduct.hide()
                view?.hide()
            }else if (role == "admin"){
                binding.fabAddProduct.show()
                view?.show()
            } else {}
        }
    }

    private fun navigateToDetails(product: Product) {
        val action = HomeFragmentDirections.actionProductFragmentToDetailsFragment(product)
        findNavController().navigate(action)
    }

    private fun addProductToWishList(product: Product) {
        firebaseViewModel.addWishlist(
            hashMapOf(
                "name" to product.name.toString(),
                "url" to product.url.toString(),
                "price" to product.price.toString(),
                "details" to product.description.toString(),
                "rating" to product.rating.toString()
            )
        )
        firebaseViewModel.addWishlist.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbHome.show()
                }
                is Resource.Success ->{
                    binding.pbHome.hide()
                    requireActivity().showToast(getString(R.string.added_to_wishlist))
                }
                is Resource.Error ->{
                    binding.pbHome.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun retrieveAndSetVarieties() {
        firebaseViewModel.getVarieties()

        firebaseViewModel.getVarieties.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbHome.show()
                }
                is Resource.Success ->{
                    if (resource.data?.isEmpty() == true){
                        binding.txtHomeVarietiesNoItems.show()
                        binding.rvHomeOtherProducts.hide()
                    }else{
                        binding.rvHomeOtherProducts.show()
                        binding.txtHomeVarietiesNoItems.hide()
                    }
                    binding.pbHome.hide()
                    varietiesAdapter.differCallBack.submitList(resource.data)
                }
                is Resource.Error ->{
                    binding.pbHome.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun retrieveAndSetGlasses() {
        getGlasses(ROUND_GLASSES)
        binding.chipGroupGlasses.setOnCheckedStateChangeListener { group, checkedIds ->
            checkedIds.forEach {
                when(it){
                    R.id.chip_round_glasses ->{
                        getGlasses(ROUND_GLASSES)
                    }
                    R.id.chip_transparent ->{
                        getGlasses(TRANSPARENT_GLASSES)
                    }
                    R.id.chip_sunglass ->{
                        getGlasses(SUN_GLASSES)
                    }
                }
            }
        }
    }

    private fun getGlasses(type: String) {
        firebaseViewModel.getGlasses(type)

        firebaseViewModel.getGlasses.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbHome.show()
                }
                is Resource.Success ->{
                    if (resource.data?.isEmpty() == true){
                        binding.txtHomeGlassNoItems.show()
                        binding.rvHomeGlass.hide()
                    }else{
                        binding.rvHomeGlass.show()
                        binding.txtHomeGlassNoItems.hide()
                    }
                    glassAdapter.differCallBack.submitList(resource.data)
                    binding.pbHome.hide()
                }
                is Resource.Error ->{
                    binding.pbHome.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun retrieveAndSetShoes() {
        getShoes(CONVERSE_SHOES)
        binding.chipGroupShoes.setOnCheckedStateChangeListener { group, checkedIds ->
            checkedIds.forEach {
                when(it){
                    R.id.chip_converse_shoes ->{
                        getShoes(CONVERSE_SHOES)
                    }
                    R.id.chip_adidas ->{
                        getShoes(ADIDAS_SHOES)
                    }
                    R.id.chip_nike ->{
                        getShoes(NIKE_SHOES)
                    }
                }
            }
        }
    }

    private fun getShoes(type: String) {
        firebaseViewModel.getShoes(type)

        firebaseViewModel.getShoes.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbHome.show()
                }
                is Resource.Success ->{
                    if (resource.data?.isEmpty() == true){
                        binding.txtHomeShoeNoItems.show()
                        binding.rvHomeShoe.hide()
                    }else{
                        binding.rvHomeShoe.show()
                        binding.txtHomeShoeNoItems.hide()
                    }
                    shoeAdapter.differCallBack.submitList(resource.data)
                    binding.pbHome.hide()
                }
                is Resource.Error ->{
                    binding.pbHome.hide()
                    requireActivity().showToast(resource.message.toString())
                }
            }
        })
    }

    private fun setUpVarietiesRecyclerView() = binding.rvHomeOtherProducts.apply {
        adapter = varietiesAdapter
        layoutManager = LinearLayoutManager(requireContext())
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