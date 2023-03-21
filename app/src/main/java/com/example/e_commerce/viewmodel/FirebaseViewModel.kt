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

    private val _setProfile = MutableLiveData<Resource<String>>()
    val setProfile: LiveData<Resource<String>>
        get() = _setProfile

    private val _updateProfile = MutableLiveData<Resource<String>>()
    val updateProfile: LiveData<Resource<String>>
        get() = _updateProfile

    private val _getProfile = MutableLiveData<Resource<Profile>>()
    val getProfile: LiveData<Resource<Profile>>
        get() = _getProfile

    private val _addShoes = MutableLiveData<Resource<String>>()
    val addShoes: LiveData<Resource<String>>
        get() = _addShoes

    private val _getShoes = MutableLiveData<Resource<List<Product>>>()
    val getShoes: LiveData<Resource<List<Product>>>
        get() = _getShoes

    private val _deleteShoes = MutableLiveData<Resource<String>>()
    val deleteShoes: LiveData<Resource<String>>
        get() = _deleteShoes

    private val _addGlasses = MutableLiveData<Resource<String>>()
    val addGlasses: LiveData<Resource<String>>
        get() = _addGlasses

    private val _getGlasses = MutableLiveData<Resource<List<Product>>>()
    val getGlasses: LiveData<Resource<List<Product>>>
        get() = _getGlasses

    private val _deleteGlasses = MutableLiveData<Resource<String>>()
    val deleteGlasses: LiveData<Resource<String>>
        get() = _deleteGlasses

    private val _addVarieties = MutableLiveData<Resource<String>>()
    val addVarieties: LiveData<Resource<String>>
        get() = _addVarieties

    private val _getVarieties = MutableLiveData<Resource<List<Product>>>()
    val getVarieties: LiveData<Resource<List<Product>>>
        get() = _getVarieties

    private val _deleteVarieties = MutableLiveData<Resource<String>>()
    val deleteVarieties: LiveData<Resource<String>>
        get() = _deleteVarieties

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
        .document(auth.currentUser?.uid!!).collection("profile").document(auth.currentUser?.uid!!)

    private val shoesCollectionRef = Firebase.firestore.collection("shoes")
    private val adidasCategoryCollectionRef = Firebase.firestore.collection("shoesCategory")
        .document("1").collection("adidas")
    private val nikeCategoryCollectionRef = Firebase.firestore.collection("shoesCategory")
        .document("2").collection("nike")

    private val glassesCollectionRef = Firebase.firestore.collection("glasses")
//    private val transparentCategoryCollectionRef = Firebase.firestore.collection("glassesCategory")
//        .document("1").collection("transparent")
//    private val sunglassCategoryCollectionRef = Firebase.firestore.collection("glassesCategory")
//        .document("2").collection("sunglass")

    private val varietiesCollectionRef = Firebase.firestore.collection("varieties")

    private val wishlistCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("wishlist")
    private val cartCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("cart")

    fun setProfile(profile: HashMap<String, String>){
        _setProfile.value = Resource.Loading()
        profileCollectionRef.set(profile)
            .addOnSuccessListener {
                _setProfile.value = Resource.Success("Successfully set data!")
            }
            .addOnFailureListener{
                _setProfile.value = Resource.Error(message = it.message.toString())
            }
    }

    fun updateProfile(profile: Map<String, String>){
        _updateProfile.value = Resource.Loading()
        profileCollectionRef.get()
            .addOnSuccessListener { documentSnapshot->
                profileCollectionRef
                    .set(profile, SetOptions.merge())
                    .addOnSuccessListener {
                        _updateProfile.value = Resource.Success("Successfully updated profile!")
                    }
                    .addOnFailureListener {
                        _updateProfile.value = Resource.Error(message = it.message.toString())
                    }
            }
    }

    fun getProfile(){
        _getProfile.value = Resource.Loading()
        profileCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getProfile.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let { documentSnapshot->
                documentSnapshot.toObject<Profile>()?.let {
                    _getProfile.value = Resource.Success(it)
                }
            }
        }
    }

    fun addShoes(product: HashMap<String, String>){
        _addShoes.value = Resource.Loading()
        shoesCollectionRef.add(product)
            .addOnSuccessListener {
                _addShoes.value = Resource.Success("Successfully added data!")
            }
            .addOnFailureListener{
                _addShoes.value = Resource.Error(message = it.message.toString())
            }
    }

    fun getShoes(type: String){
        _getShoes.value = Resource.Loading()
        shoesCollectionRef
            .whereEqualTo("type",type).addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getShoes.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getShoes.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteShoes(product: Product){
        _deleteShoes.value = Resource.Loading()
        shoesCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        shoesCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteShoes.value = Resource.Success("Removed from shoes!")
                            }
                            .addOnFailureListener { error->
                                _deleteShoes.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
            }
    }

    fun addGlasses(product: HashMap<String, String>){
        _addGlasses.value = Resource.Loading()
        glassesCollectionRef.add(product)
            .addOnSuccessListener {
                _addGlasses.value = Resource.Success("Successfully added data!")
            }
            .addOnFailureListener{
                _addGlasses.value = Resource.Error(message = it.message.toString())
            }
    }

    fun getGlasses(type: String){
        _getGlasses.value = Resource.Loading()
        glassesCollectionRef
            .whereEqualTo("type", type).addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getGlasses.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getGlasses.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteGlasses(product: Product){
        _deleteGlasses.value = Resource.Loading()
        glassesCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        glassesCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteGlasses.value = Resource.Success("Removed from glasses!")
                            }
                            .addOnFailureListener { error->
                                _deleteGlasses.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
            }
    }

    fun addVarieties(product: HashMap<String, String>){
        _addVarieties.value = Resource.Loading()
        varietiesCollectionRef.add(product)
            .addOnSuccessListener {
                _addVarieties.value = Resource.Success("Successfully added data!")
            }
            .addOnFailureListener{
                _addVarieties.value = Resource.Error(message = it.message.toString())
            }
    }

    fun getVarieties(){
        _getVarieties.value = Resource.Loading()
        varietiesCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getVarieties.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getVarieties.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteVarieties(product: Product){
        _deleteVarieties.value = Resource.Loading()
        varietiesCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        varietiesCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteVarieties.value = Resource.Success("Removed from varieties!")
                            }
                            .addOnFailureListener { error->
                                _deleteVarieties.value = Resource.Error(message = error.message.toString())
                            }
                    }
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