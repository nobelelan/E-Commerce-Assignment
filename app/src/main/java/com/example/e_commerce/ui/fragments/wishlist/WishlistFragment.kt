package com.example.e_commerce.ui.fragments.wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentWishlistBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.utils.ExtensionFunctions.hide
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Resource
import com.example.e_commerce.viewmodel.FirebaseViewModel


class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseViewModel: FirebaseViewModel

    private val wishlistAdapter by lazy { WishlistAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWishlistBinding.bind(view)

        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseViewModel::class.java]

        setUpWishlistAdapter()
        retrieveAndSetWishlist()

        wishlistAdapter.setOnClickListener(object : WishlistAdapter.OnItemClickListener{
            override fun onProductClick(product: Product) {
                val action = WishlistFragmentDirections.actionFavFragmentToDetailsFragment2(product)
                findNavController().navigate(action)
            }

            override fun onFavIconClick(product: Product) {
                firebaseViewModel.deleteWishlist(product)

                firebaseViewModel.deleteWishlist.observe(viewLifecycleOwner, Observer { resource->
                    when(resource){
                        is Resource.Loading ->{
                            binding.pbWishlist.show()
                        }
                        is Resource.Success ->{
                            requireActivity().showToast(getString(R.string.removed_from_wishlist))
                            binding.pbWishlist.hide()
                        }
                        is Resource.Error ->{
                            requireActivity().showToast(resource.message.toString())
                            binding.pbWishlist.hide()
                        }
                    }
                })
            }
        })
    }

    private fun retrieveAndSetWishlist() {
        firebaseViewModel.getWishlist.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Loading ->{
                    binding.pbWishlist.show()
                }
                is Resource.Success ->{
                    if (resource.data?.isEmpty() == true){
                        binding.txtNoItem.show()
                        binding.rvWishlist.hide()
                    }else{
                        binding.txtNoItem.hide()
                        binding.rvWishlist.show()
                        wishlistAdapter.differCallBack.submitList(resource.data)
                    }
                    binding.pbWishlist.hide()
                }
                is Resource.Error ->{
                    requireActivity().showToast(resource.message.toString())
                    binding.pbWishlist.hide()
                }
            }
        })
    }

    private fun setUpWishlistAdapter() = binding.rvWishlist.apply {
        adapter = wishlistAdapter
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}