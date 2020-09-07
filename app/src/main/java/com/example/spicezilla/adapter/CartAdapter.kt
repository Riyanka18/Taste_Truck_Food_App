package com.example.spicezilla.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spicezilla.R
import com.example.spicezilla.model.FoodItems

class CartAdapter(private val cartList: ArrayList<FoodItems>, val context: Context):RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_row,parent,false)
        return CartViewHolder(view)
    }
    override fun getItemCount(): Int {
        return cartList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onBindViewHolder(holder:CartViewHolder, pos: Int) {
        val cartobj = cartList[pos]
        holder.itemName.text=cartobj.name
        val price="Rs.${cartobj.cost?.toString()}"
        holder.itemCost.text=price
    }
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.txtCartItemName)
        val itemCost: TextView = view.findViewById(R.id.txtCartPrice)
    }
}
