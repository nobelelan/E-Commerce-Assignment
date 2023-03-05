package com.example.e_commerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commerce.model.Product
import com.example.e_commerce.model.Profile
import com.example.e_commerce.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FirebaseViewModel: ViewModel() {
    private val _addProfile = MutableLiveData<Resource<String>>()
    val addProfile: LiveData<Resource<String>>
        get() = _addProfile

    private val _updateProfile = MutableLiveData<Resource<String>>()
    val updateProfile: LiveData<Resource<String>>
        get() = _updateProfile

    private val _getProfile = MutableLiveData<Resource<Profile>>()
    val getProfile: LiveData<Resource<Profile>>
        get() = _getProfile

    private val _getShoes = MutableLiveData<Resource<List<Product>>>()
    val getShoes: LiveData<Resource<List<Product>>>
        get() = _getShoes

    private val _getGlasses = MutableLiveData<Resource<List<Product>>>()
    val getGlasses: LiveData<Resource<List<Product>>>
        get() = _getGlasses

    private val _addWishlist = MutableLiveData<Resource<String>>()
    val addWishlist: LiveData<Resource<String>>
        get() = _addWishlist

    private val _getWishlist = MutableLiveData<Resource<List<Product>>>()
    val getWishlist: LiveData<Resource<List<Product>>>
        get() = _getWishlist

    private val _deleteWishlist = MutableLiveData<Resource<String>>()
    val deleteWishlist: LiveData<Resource<String>>
        get() = _deleteWishlist

    private val _addCart = MutableLiveData<Resource<String>>()
    val addCart: LiveData<Resource<String>>
        get() = _addCart

    private val _getCart = MutableLiveData<Resource<List<Product>>>()
    val getCart: LiveData<Resource<List<Product>>>
        get() = _getCart

    private val _deleteCart = MutableLiveData<Resource<String>>()
    val deleteCart: LiveData<Resource<String>>
        get() = _deleteCart

    private val auth = Firebase.auth
    private val profileCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("profile")
    private val shoesCollectionRef = Firebase.firestore.collection("shoes")
    private val glassesCollectionRef = Firebase.firestore.collection("glasses")
    private val wishlistCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("wishlist")
    private val cartCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("cart")


    fun addProfile(profile: HashMap<String, String>){
        _addProfile.value = Resource.Loading()
        profileCollectionRef.add(profile)
            .addOnSuccessListener {
                _addProfile.value = Resource.Success("Successfully added data!")
            }
            .addOnFailureListener{
                _addProfile.value = Resource.Error(message = it.message.toString())
            }
    }

    fun updateProfile(profile: Map<String, String>){
        _updateProfile.value = Resource.Loading()
        profileCollectionRef.get()
            .addOnSuccessListener { querySnapshot->
                if (querySnapshot.documents.isNotEmpty()){
                    querySnapshot.documents.forEach { documentSnapshot ->
                        profileCollectionRef.document(documentSnapshot.id)
                            .set(profile, SetOptions.merge())
                            .addOnSuccessListener {
                                _updateProfile.value = Resource.Success("Successfully updated profile!")
                            }
                            .addOnFailureListener {
                                _updateProfile.value = Resource.Error(message = it.message.toString())
                            }
                    }
                }
            }
    }

    fun getProfile(){
        _getProfile.value = Resource.Loading()
        profileCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getProfile.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                it.forEach {documentSnapshot->
                    _getProfile.value = Resource.Success(documentSnapshot.toObject())
                }
            }
        }
    }

    fun getShoes(){
        _getShoes.value = Resource.Loading()
        shoesCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getShoes.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getShoes.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun getGlasses(){
        _getGlasses.value = Resource.Loading()
        glassesCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getGlasses.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getGlasses.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun addWishlist(product: HashMap<String, String>){
        _addWishlist.value = Resource.Loading()
        wishlistCollectionRef.add(product)
            .addOnSuccessListener {
                _addWishlist.value = Resource.Success("Added to wishlist!")
            }
            .addOnFailureListener{
                _addWishlist.value = Resource.Error(message = it.message.toString())
            }
    }

    fun getWishlist(){
        _getWishlist.value = Resource.Loading()
        wishlistCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getWishlist.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getWishlist.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteWishlist(product: Product){
        _deleteWishlist.value = Resource.Loading()
        wishlistCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        wishlistCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteWishlist.value = Resource.Success("Removed from wishlist!")
                            }
                            .addOnFailureListener { error->
                                _deleteWishlist.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
            }
    }

    fun addCart(product: HashMap<String, String>){
        _addCart.value = Resource.Loading()
        cartCollectionRef.add(product)
            .addOnSuccessListener {
                _addCart.value = Resource.Success("Added to cart!")
            }
            .addOnFailureListener{
                _addCart.value = Resource.Error(message = it.message.toString())
            }
    }

    fun getCart(){
        _getCart.value = Resource.Loading()
        cartCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getCart.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getCart.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteCart(product: Product){
        _deleteCart.value = Resource.Loading()
        cartCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        cartCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteCart.value = Resource.Success("Removed from cart!")
                            }
                            .addOnFailureListener { error->
                                _deleteCart.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
            }
    }
}