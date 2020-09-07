package com.example.spicezilla.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import com.example.spicezilla.adapter.OrderHistoryAdapter
import com.example.spicezilla.model.Orders

class OrderHistoryFragment : Fragment() {
    lateinit var recycler: RecyclerView
    lateinit var orderHistoryAdapter:OrderHistoryAdapter
    var orderList=ArrayList<Orders>()
    lateinit var linarLayoutManger:RecyclerView.LayoutManager
    lateinit var linearOrders: LinearLayout
    lateinit var NoOrders: TextView
    lateinit var sharedPreference: SharedPreferences
    lateinit var progress_layout: RelativeLayout
    lateinit var progress_Bar:ProgressBar
    private var userId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        linearOrders=view.findViewById(R.id.linearOrders)
        NoOrders = view.findViewById(R.id.no_order)
        linarLayoutManger=LinearLayoutManager(activity)
        recycler=view.findViewById(R.id.recyclerOrderHistory)
        progress_Bar=view.findViewById(R.id.order_progressBar)
        progress_layout=view.findViewById(R.id.order_progress_layout)
        progress_layout.visibility = View.VISIBLE
        sharedPreference=(activity as Context).getSharedPreferences("Food_preference",Context.MODE_PRIVATE)
        userId = sharedPreference.getString("user_id", null) as String
        if(userId!="99")
        {
            userId="99"
        }
        sendRequest(userId)
        return view
    }
    private fun sendRequest(userid: String) {
        val queue = Volley.newRequestQueue(activity as Context)
        val jsonObjectRequest = object:JsonObjectRequest(Method.GET,"http://13.235.250.119/v2/orders/fetch_result/$userid",null,Response.Listener {
                progress_Bar.visibility = View.VISIBLE
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progress_Bar.visibility=View.GONE
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            recycler.visibility=View.GONE
                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = Orders(
                                    orderObject.getInt("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderList.add(orderDetails)
                                if (orderList.isEmpty()) {
                                    recycler.visibility=View.GONE
                                    NoOrders.visibility = View.VISIBLE
                                } else {
                                    recycler.visibility-View.VISIBLE
                                    NoOrders.visibility = View.GONE
                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(activity as Context,orderList)
                                        recycler.layoutManager=linarLayoutManger
                                        recycler.adapter=orderHistoryAdapter
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity as Context,"JSON Exception",Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                println(it.message)
                Toast.makeText(activity as Context,it.message,Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "e59d83ed325595"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}
