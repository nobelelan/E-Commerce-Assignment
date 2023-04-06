package com.example.e_commerce.ui.fragments.profile


import android.annotation.SuppressLint
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.databinding.RvHomeVarietyBinding
import com.example.e_commerce.databinding.RvProfilerUserListBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.model.Profile

class UserListAdapter: RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    inner class UserListViewHolder(val binding: RvProfilerUserListBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Profile>(){
        override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
            return oldItem.phone == newItem.phone
        }

        override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
            return oldItem == newItem
        }
    }

    val differCallBack = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(RvProfilerUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val profile = differCallBack.currentList[position]

        holder.binding.apply {
            txtUserName.text = profile.name
            txtPhone.text = profile.phone
            txtRole.text = profile.role
            txtRole.setOnClickListener {
                onItemClickListener?.onRoleClick(profile)
            }
        }

    }

    override fun getItemCount(): Int {
        return differCallBack.currentList.size
    }

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onRoleClick(profile: Profile)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }
}