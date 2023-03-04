package com.example.e_commerce.ui.fragments.product.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.databinding.RvHomeShoeBinding
import com.example.e_commerce.model.Product

class ShoeAdapter: RecyclerView.Adapter<ShoeAdapter.ShoeViewHolder>() {

    inner class ShoeViewHolder(val binding: RvHomeShoeBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differCallBack = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {
        return ShoeViewHolder(RvHomeShoeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ShoeViewHolder, position: Int) {
        val shoe = differCallBack.currentList[position]

        holder.binding.apply {
            txtProductName.text = shoe.name
            txtProductPrice.text = shoe.price

            Glide.with(holder.itemView.context)
                .load(shoe.url)
                .placeholder(R.drawable.baseline_snowshoeing)
                .into(imgProduct)

            imgAddWishlist.setOnClickListener {
                onItemClickListener?.onFavIconClick(shoe)
            }
            root.setOnClickListener {
                onItemClickListener?.onProductClick(shoe)
            }
        }

    }

    override fun getItemCount(): Int {
        return differCallBack.currentList.size
    }

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onProductClick(product: Product)
        fun onFavIconClick(product: Product)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }
}