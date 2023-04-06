package com.example.e_commerce.ui.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            }
        })
    }

    private fun showAlertDialog(role: String){
        AlertDialog.Builder(requireContext())
            .setTitle("Shift Access?")
            .setNegativeButton("Cancel"){_,_->}
            .setPositiveButton("Yes"){_,_->

            }.create().show()
    }

    private fun retrieveAndSetUserListData() {

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