package com.example.e_commerce.ui.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.databinding.FragmentUserListBinding
import com.example.e_commerce.model.Profile
import com.example.e_commerce.utils.Constants
import com.example.e_commerce.utils.Constants.ROLE_ADMIN
import com.example.e_commerce.utils.Constants.ROLE_USER
import com.example.e_commerce.utils.ExtensionFunctions.gone
import com.example.e_commerce.utils.ExtensionFunctions.show
import com.example.e_commerce.utils.ExtensionFunctions.showToast
import com.example.e_commerce.utils.Util.applyGeneralSharedPref
import com.example.e_commerce.viewmodel.FirebaseViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseViewModel: FirebaseViewModel

    private val userListAdapter by lazy { UserListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserListBinding.bind(view)

        auth = Firebase.auth
        firebaseViewModel = ViewModelProvider(this)[FirebaseViewModel::class.java]

        setUpUserListRecyclerView()
        retrieveAndSetUserListData()

        userListAdapter.setOnClickListener(object : UserListAdapter.OnItemClickListener{
            override fun onRoleClick(profile: Profile) {
                if (profile.role != "admin"){
                    showAlertDialog(profile)
                }else{
                    requireActivity().showToast("You can't demote an Admin.")
                }
            }
        })
    }

    private fun showAlertDialog(profile: Profile){
        AlertDialog.Builder(requireContext())
            .setTitle("Make Admin?")
            .setNegativeButton("Cancel"){_,_->}
            .setPositiveButton("Yes"){_,_->

                val batch = Firebase.firestore.batch()
                Firebase.firestore.collectionGroup("profile")
                    .whereEqualTo("phone", profile.phone)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.documents.isNotEmpty()) {
                            querySnapshot.documents.forEach { documentSnapshot ->
                                val docRef = Firebase.firestore.collection("users")
                                    .document(documentSnapshot.reference.parent.parent!!.id)
                                    .collection("profile").document(documentSnapshot.id)
                                batch.update(docRef, "role", ROLE_ADMIN)
                            }
                            batch.commit()
                        }
                    }

            }.create().show()
    }

    private fun retrieveAndSetUserListData() {
        val profileList = arrayListOf<Profile>()

        Firebase.firestore.collectionGroup("profile")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Loop through the documents in the "profile" collection group
                querySnapshot.documents.forEach { documentSnapshot ->
                    // Convert the document to Profile object
                    val profile = documentSnapshot.toObject(Profile::class.java)
                    // Add the profile to the profileList
                    profile?.let { profileList.add(it) }

                    // Check if this is the last profile document
                    if (profileList.size == querySnapshot.documents.size) {
                        // All profiles have been fetched
                        // You can use profileList here
                        userListAdapter.differCallBack.submitList(profileList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle error if profile collection retrieval fails
                // You may want to handle this case based on your specific use case
            }


    }

    private fun setUpUserListRecyclerView() = binding.rvUserList.apply {
        adapter = userListAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}