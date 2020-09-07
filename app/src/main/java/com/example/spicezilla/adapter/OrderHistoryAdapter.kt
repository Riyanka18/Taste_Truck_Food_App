package com.example.spicezilla.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spicezilla.R
import com.example.spicezilla.model.FoodItems
import com.example.spicezilla.model.Orders
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter(val context: Context,private val orderList: ArrayList<Orders>):RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>()
{
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):OrderHistoryViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.order_history_single_row,parent,false)
        return OrderHistoryViewHolder(view)
    }
    override fun getItemCount(): Int {
        return orderList.size
    }
    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resName= view.findViewById(R.id.historyResName) as TextView
        val date= view.findViewById(R.id.date) as TextView
        val recyclerResHistory=view.findViewById(R.id.HistoryRecycler) as RecyclerView
    }
    override fun onBindViewHolder(holder:OrderHistoryViewHolder,position:Int) {
        val obj = orderList[position]
        holder.resName.text=obj.resName
        holder.date.text = formatDate(obj.orderDate)
        setRecycler(holder.recyclerResHistory,obj)
    }
    private fun setRecycler(recyclerResHistory:RecyclerView,orderList:Orders) {
        val foodItemsList = ArrayList<FoodItems>()
        for (a in 0 until orderList.foodItem.length()) {
            val foodJson=orderList.foodItem.getJSONObject(a)
            foodItemsList.add(FoodItems(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost").toInt()))
        }
        val cartItemAdapter = CartAdapter(foodItemsList,context)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.adapter = cartItemAdapter
    }
    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}
