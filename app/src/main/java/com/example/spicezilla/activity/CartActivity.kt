package com.example.spicezilla.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import com.example.spicezilla.adapter.CartAdapter
import com.example.spicezilla.adapter.MenuAdapter
import com.example.spicezilla.database.HomeDatabase
import com.example.spicezilla.database.OrderEntity
import com.example.spicezilla.fragment.RestaurantMenuFragment
import com.example.spicezilla.model.FoodItems
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var cartItemAdapter: CartAdapter
    var orderList = ArrayList<FoodItems>()
    lateinit var txtResName: TextView
    lateinit var rlLoading: RelativeLayout
    lateinit var rlCart: RelativeLayout
    lateinit var btnPlaceOrder:Button
    var resId: Int = 0
    var resName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rlLoading = findViewById(R.id.rlLoading)
        rlCart = findViewById(R.id.relativeCart)
        txtResName = findViewById(R.id.txtCartResName)
        txtResName.text=RestaurantMenuFragment.resName
        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId", 0) as Int
        resName = bundle.getString("resName", "") as String

        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerCart = findViewById(R.id.recyclerCartItems)
        val dbList = GetItemsFromDBAsync(applicationContext).execute().get()
        for (element in dbList) {
            orderList.addAll(Gson().fromJson(element.foodItems, Array<FoodItems>::class.java).asList()
            )
        }
        if (orderList.isEmpty()) {
            rlCart.visibility = View.GONE
            rlLoading.visibility = View.VISIBLE
        }
        else
        {
            rlCart.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
        }
        cartItemAdapter = CartAdapter(orderList, this@CartActivity)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCart.layoutManager = mLayoutManager
        recyclerCart.adapter = cartItemAdapter
        btnPlaceOrder = findViewById(R.id.btnConfirmOrder)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        val total = "Amount of the Order Placed (Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            rlLoading.visibility = View.VISIBLE
            rlCart.visibility = View.INVISIBLE
            sendServerRequest()
        }
    }
    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
        val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this@CartActivity.getSharedPreferences("Food_preference",Context.MODE_PRIVATE).getString(
                "user_id",
                null
            ) as String
        )
        jsonParams.put("restaurant_id",RestaurantMenuFragment.resId?.toString() as String)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        jsonParams.put("total_cost", sum.toString())
        val foodArray = JSONArray()
        for (a in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[a].id)
            foodArray.put(a, foodId)
        }
        jsonParams.put("food", foodArray)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST,"http://13.235.250.119/v2/place_order/fetch_result/", jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val clearCart =
                            ClearDBAsync(applicationContext, resId.toString()).execute().get()
                        MenuAdapter.isCartEmpty = true
                        val dialog = Dialog(
                            this@CartActivity,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        dialog.setContentView(R.layout.order_placed)
                        dialog.show()
                        dialog.setCancelable(false)
                        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                            startActivity(Intent(this@CartActivity,MainActivity::class.java))
                            ActivityCompat.finishAffinity(this@CartActivity)
                        }
                    }
                    else {
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(this@CartActivity, "Some Error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    rlCart.visibility = View.VISIBLE
                    e.printStackTrace()
                }
            },Response.ErrorListener {
                rlCart.visibility = View.VISIBLE
                Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
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
    class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, HomeDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }
    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context,HomeDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        if (ClearDBAsync(applicationContext, resId.toString()).execute().get()) {
            MenuAdapter.isCartEmpty = true
            onBackPressed()
            return true
        }
        return false
    }
    override fun onBackPressed() {
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        MenuAdapter.isCartEmpty = true
        super.onBackPressed()
    }
}