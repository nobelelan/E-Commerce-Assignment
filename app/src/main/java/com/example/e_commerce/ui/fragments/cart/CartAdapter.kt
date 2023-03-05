package com.example.e_commerce.ui.fragments.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.databinding.RvCartBinding
import com.example.e_commerce.model.Product
import com.example.e_commerce.utils.ExtensionFunctions.disable
import com.example.e_commerce.utils.ExtensionFunctions.enable

class CartAdapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: RvCartBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differCallBack = AsyncListDiffer(this, diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(RvCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = differCallBack.currentList[position]

        holder.binding.apply {
            txtProductName.text = product.name
            txtProductPrice.text = "${product.price}/= Taka"

            Glide.with(holder.itemView.context)
                .load(product.url)
                .placeholder(R.drawable.baseline_snowshoeing)
                .into(imgProduct)

            root.setOnLongClickListener(object : OnLongClickListener{
                override fun onLongClick(p0: View?): Boolean {
                    onItemClickListener?.onLongClick(product)
                    return true
                }
            })
            btnMinus.setOnClickListener {
                var counter = holder.binding.txtCounter.text.toString().toInt()
                if (counter != 0){
                    onItemClickListener?.onMinusClick(product.price.toString().toInt())
                    counter -= 1
                    holder.binding.txtCounter.text = counter.toString()
                }
            }
            btnPlus.setOnClickListener {
                onItemClickListener?.onPlusClick(product.price.toString().toInt())
                var counter = holder.binding.txtCounter.text.toString().toInt()
                counter += 1
                holder.binding.txtCounter.text = counter.toString()
            }
        }

    }

    override fun getItemCount(): Int {
        return differCallBack.currentList.size
    }

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onLongClick(product: Product)
        fun onPlusClick(price: Int)
        fun onMinusClick(price: Int)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }
}