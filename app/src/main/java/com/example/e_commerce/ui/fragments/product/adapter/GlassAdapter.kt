package com.example.e_commerce.ui.fragments.product.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.databinding.RvHomeShoeBinding
import com.example.e_commerce.model.Product

class GlassAdapter: RecyclerView.Adapter<GlassAdapter.GlassViewHolder>() {

    inner class GlassViewHolder(val binding: RvHomeShoeBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differCallBack = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlassViewHolder {
        return GlassViewHolder(RvHomeShoeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GlassViewHolder, position: Int) {
        val glass = differCallBack.currentList[position]

        holder.binding.apply {
            txtProductName.text = glass.name
            txtProductPrice.text = "${glass.price}/= Taka"

            Glide.with(holder.itemView.context)
                .load(glass.url)
                .placeholder(R.drawable.baseline_snowshoeing)
                .into(imgProduct)

            imgAddWishlist.setOnClickListener {
                onItemClickListener?.onFavIconClick(glass)
            }
            root.setOnClickListener {
                onItemClickListener?.onProductClick(glass)
            }
            imgDeleteProduct.setOnClickListener {
                onItemClickListener?.onDeleteClick(glass)
            }
            onItemClickListener?.onViewCreated(imgDeleteProduct)
        }

    }

    override fun getItemCount(): Int {
        return differCallBack.currentList.size
    }

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onProductClick(product: Product)
        fun onFavIconClick(product: Product)
        fun onDeleteClick(product: Product)
        fun onViewCreated(view: ImageView)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }
}