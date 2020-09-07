package com.example.spicezilla.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.spicezilla.model.FoodItems
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.spicezilla.R

class MenuAdapter(
    val context: Context,
    private val menuList: ArrayList<FoodItems>,
    private val listener: OnItemClickListener
):RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    companion object {
        var isCartEmpty: Boolean=true
    }
    override fun onCreateViewHolder(p0:ViewGroup, p1: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.menu_single_row, p0, false)
        return MenuViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return menuList.size
    }
    interface OnItemClickListener {
        fun onAddItemClick(foodItem:FoodItems)
        fun onRemoveItemClick(foodItem:FoodItems)
    }
    override fun onBindViewHolder(p0: MenuViewHolder, p1: Int) {
        val menuObject = menuList[p1]
        p0.foodItemName.text = menuObject.name
        val cost = "Rs. ${menuObject.cost?.toString()}"
        p0.foodItemCost.text = cost
        p0.sno.text = (p1 + 1).toString()
        p0.addToCart.setOnClickListener {
            p0.addToCart.visibility = View.GONE
            p0.removeFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menuObject)
        }
        p0.removeFromCart.setOnClickListener {
            p0.removeFromCart.visibility = View.GONE
            p0.addToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuObject)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodItemName: TextView = view.findViewById(R.id.txtItemName)
        val foodItemCost: TextView = view.findViewById(R.id.itemCost)
        val sno: TextView = view.findViewById(R.id.SNo)
        val addToCart: Button = view.findViewById(R.id.btnAddToCart)
        val removeFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }
}