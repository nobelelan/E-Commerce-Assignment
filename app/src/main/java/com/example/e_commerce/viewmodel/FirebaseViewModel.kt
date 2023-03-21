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

    private val _setProfile = MutableLiveData<Resource<String>>()
    val setProfile: LiveData<Resource<String>>
        get() = _setProfile

    private val _updateProfile = MutableLiveData<Resource<String>>()
    val updateProfile: LiveData<Resource<String>>
        get() = _updateProfile

    private val _getProfile = MutableLiveData<Resource<Profile>>()
    val getProfile: LiveData<Resource<Profile>>
        get() = _getProfile

    private val _getShoes = MutableLiveData<Resource<List<Product>>>()
    val getShoes: LiveData<Resource<List<Product>>>
        get() = _getShoes

    private val _deleteShoes = MutableLiveData<Resource<String>>()
    val deleteShoes: LiveData<Resource<String>>
        get() = _deleteShoes

    private val _getAdidasShoes = MutableLiveData<Resource<List<Product>>>()
    val getAdidasShoes: LiveData<Resource<List<Product>>>
        get() = _getAdidasShoes

    private val _deleteAdidasShoes = MutableLiveData<Resource<String>>()
    val deleteAdidasShoes: LiveData<Resource<String>>
        get() = _deleteAdidasShoes

    private val _getNikeShoes = MutableLiveData<Resource<List<Product>>>()
    val getNikeShoes: LiveData<Resource<List<Product>>>
        get() = _getNikeShoes

    private val _deleteNikeShoes = MutableLiveData<Resource<String>>()
    val deleteNikeShoes: LiveData<Resource<String>>
        get() = _deleteNikeShoes

    private val _getGlasses = MutableLiveData<Resource<List<Product>>>()
    val getGlasses: LiveData<Resource<List<Product>>>
        get() = _getGlasses

    private val _deleteGlasses = MutableLiveData<Resource<String>>()
    val deleteGlasses: LiveData<Resource<String>>
        get() = _deleteGlasses

    private val _getTransparentGlasses = MutableLiveData<Resource<List<Product>>>()
    val getTransparentGlasses: LiveData<Resource<List<Product>>>
        get() = _getTransparentGlasses

    private val _deleteTransparentGlasses = MutableLiveData<Resource<String>>()
    val deleteTransparentGlasses: LiveData<Resource<String>>
        get() = _deleteTransparentGlasses

    private val _getSunGlasses = MutableLiveData<Resource<List<Product>>>()
    val getSunGlasses: LiveData<Resource<List<Product>>>
        get() = _getSunGlasses

    private val _deleteSunglasses = MutableLiveData<Resource<String>>()
    val deleteSunglasses: LiveData<Resource<String>>
        get() = _deleteSunglasses

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
//    private val addProfileCollectionRef = Firebase.firestore.collection("users")
//        .document(auth.currentUser?.uid!!).collection("profile")

    private val shoesCollectionRef = Firebase.firestore.collection("shoes")
    private val adidasCategoryCollectionRef = Firebase.firestore.collection("shoesCategory")
        .document("1").collection("adidas")
    private val nikeCategoryCollectionRef = Firebase.firestore.collection("shoesCategory")
        .document("2").collection("nike")

    private val glassesCollectionRef = Firebase.firestore.collection("glasses")
    private val transparentCategoryCollectionRef = Firebase.firestore.collection("glassesCategory")
        .document("1").collection("transparent")
    private val sunglassCategoryCollectionRef = Firebase.firestore.collection("glassesCategory")
        .document("2").collection("sunglass")

    private val varietiesCollectionRef = Firebase.firestore.collection("varieties")

    private val wishlistCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("wishlist")
    private val cartCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("cart")

//    fun addProfile(profile: HashMap<String, String>){
//        _addProfile.value = Resource.Loading()
//        addProfileCollectionRef.add(profile)
//            .addOnSuccessListener {
//                _addProfile.value = Resource.Success("Successfully added data!")
//            }
//            .addOnFailureListener{
//                _addProfile.value = Resource.Error(message = it.message.toString())
//            }
//    }

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

    fun getAdidasShoes(){
        _getAdidasShoes.value = Resource.Loading()
        adidasCategoryCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getAdidasShoes.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getAdidasShoes.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteAdidasShoes(product: Product){
        _deleteAdidasShoes.value = Resource.Loading()
        adidasCategoryCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        adidasCategoryCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteAdidasShoes.value = Resource.Success("Removed from Adidas shoes!")
                            }
                            .addOnFailureListener { error->
                                _deleteAdidasShoes.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
            }
    }

    fun getNikeShoes(){
        _getNikeShoes.value = Resource.Loading()
        nikeCategoryCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getNikeShoes.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getNikeShoes.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteNikeShoes(product: Product){
        _deleteNikeShoes.value = Resource.Loading()
        nikeCategoryCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        nikeCategoryCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteNikeShoes.value = Resource.Success("Removed from Nike shoes!")
                            }
                            .addOnFailureListener { error->
                                _deleteNikeShoes.value = Resource.Error(message = error.message.toString())
                            }
                    }
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

    fun getTransparentGlasses(){
        _getTransparentGlasses.value = Resource.Loading()
        transparentCategoryCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getTransparentGlasses.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getTransparentGlasses.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteTransparentGlasses(product: Product){
        _deleteTransparentGlasses.value = Resource.Loading()
        transparentCategoryCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        transparentCategoryCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteTransparentGlasses.value = Resource.Success("Removed from transparent glasses!")
                            }
                            .addOnFailureListener { error->
                                _deleteTransparentGlasses.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
            }
    }

    fun getSunGlasses(){
        _getSunGlasses.value = Resource.Loading()
        sunglassCategoryCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getSunGlasses.value = Resource.Error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getSunGlasses.value = Resource.Success(it.toObjects())
            }
        }
    }

    fun deleteSunGlasses(product: Product){
        _deleteSunglasses.value = Resource.Loading()
        sunglassCategoryCollectionRef
            .whereEqualTo("url", product.url)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach { document->
                        sunglassCategoryCollectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deleteSunglasses.value = Resource.Success("Removed from sunglasses!")
                            }
                            .addOnFailureListener { error->
                                _deleteSunglasses.value = Resource.Error(message = error.message.toString())
                            }
                    }
                }
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