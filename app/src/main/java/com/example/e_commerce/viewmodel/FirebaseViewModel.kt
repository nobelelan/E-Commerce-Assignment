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

    private val auth = Firebase.auth
    private val profileCollectionRef = Firebase.firestore.collection("users")
        .document(auth.currentUser?.uid!!).collection("profile")
    private val shoesCollectionRef = Firebase.firestore.collection("shoes")


    fun addProfile(profile: HashMap<String, String>){
        _addProfile.value = Resource.loading()
        profileCollectionRef.add(profile)
            .addOnSuccessListener {
                _addProfile.value = Resource.success("Successfully added data!")
            }
            .addOnFailureListener{
                _addProfile.value = Resource.error(message = it.message.toString())
            }
    }

    fun updateProfile(profile: Map<String, String>){
        _updateProfile.value = Resource.loading()
        profileCollectionRef.get()
            .addOnSuccessListener { querySnapshot->
                if (querySnapshot.documents.isNotEmpty()){
                    querySnapshot.documents.forEach { documentSnapshot ->
                        profileCollectionRef.document(documentSnapshot.id)
                            .set(profile, SetOptions.merge())
                            .addOnSuccessListener {
                                _updateProfile.value = Resource.success("Successfully updated profile!")
                            }
                            .addOnFailureListener {
                                _updateProfile.value = Resource.error(message = it.message.toString())
                            }
                    }
                }
            }
    }

    fun getProfile(){
        _getProfile.value = Resource.loading()
        profileCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getProfile.value = Resource.error(message = it.message.toString())
            }
            querySnapshot?.let {
                it.forEach {documentSnapshot->
                    _getProfile.value = Resource.success(documentSnapshot.toObject())
                }
            }
        }
    }

    fun getShoes(){
        _getShoes.value = Resource.loading()
        shoesCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getShoes.value = Resource.error(message = it.message.toString())
            }
            querySnapshot?.let {
                _getShoes.value = Resource.success(it.toObjects())
            }
        }
    }
}