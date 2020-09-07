package com.example.spicezilla.fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.R
import com.example.spicezilla.activity.CartActivity
import com.example.spicezilla.adapter.MenuAdapter
import com.example.spicezilla.database.HomeDatabase
import com.example.spicezilla.database.OrderEntity
import com.example.spicezilla.model.FoodItems
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_restaurant_menu.*
import util.ConnectionManager

class RestaurantMenuFragment : Fragment() {
    lateinit var recyclerMenu: RecyclerView
    lateinit var restaurantMenuAdapter: MenuAdapter
    var menuList = arrayListOf<FoodItems>()
    lateinit var menu_progress_layout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var orderList = arrayListOf<FoodItems>()
    lateinit var sharedPreferences: SharedPreferences

    companion object {
        lateinit var goToCart: Button
        var resId: Int? = 0
        var resName: String? = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false)
        sharedPreferences = (activity as Context).getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE) as SharedPreferences
        menu_progress_layout = view.findViewById(R.id.menu_progress_layout) as RelativeLayout
        progressBar = view.findViewById(R.id.menu_progressBar) as ProgressBar
        resId = arguments?.getInt("id", 0)
        resName = arguments?.getString("name", "")
        goToCart = view.findViewById(R.id.btnGoToCart) as Button
        goToCart.visibility = View.GONE
        progressBar.visibility=View.VISIBLE
        goToCart.setOnClickListener {
            val gson= Gson()
            val foodItems = gson.toJson(orderList)
            val async = ItemsOfCart(activity as Context, resId.toString(),foodItems,1).execute()
            val result = async.get()
            if (result) {
                val data = Bundle()
                data.putInt("resId", resId as Int)
                data.putString("resName", resName)
                val intent = Intent(activity,CartActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            } else {
                Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        recyclerMenu = view.findViewById(R.id.recyclerMenuItems)
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET,"http://13.235.250.119/v2/restaurants/fetch_result/"+ resId,null, Response.Listener {
                    menu_progress_layout.visibility=View.GONE
                    progressBar.visibility=View.GONE
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray=data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val menuObject = resArray.getJSONObject(i)
                                val foodItem = FoodItems(menuObject.getString("id"),menuObject.getString("name"),
                                    menuObject.getString("cost_for_one").toInt())
                                menuList.add(foodItem)
                                restaurantMenuAdapter=MenuAdapter(activity as Context,menuList,
                                    object : MenuAdapter.OnItemClickListener{
                                        override fun onAddItemClick(foodItem: FoodItems) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {
                                                goToCart.visibility=View.VISIBLE
                                                MenuAdapter.isCartEmpty=false
                                            }
                                        }

                                        override fun onRemoveItemClick(foodItem: FoodItems) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                goToCart.visibility = View.GONE
                                                MenuAdapter.isCartEmpty=true
                                            }
                                        }
                                    })
                                val mLayoutManager = LinearLayoutManager(activity)
                                recyclerMenu.layoutManager = mLayoutManager
                                recyclerMenu.adapter = restaurantMenuAdapter
                            }
                        }

                    } catch (e: Exception) {
                        Toast.makeText(activity as Context,"Exception Occurred", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(activity as Context,"error listener",Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "e59d83ed325595"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            Toast.makeText(activity as Context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    class ItemsOfCart(
    context: Context,
    val restaurantId: String,
    val foodItems: String,
    val mode: Int
): AsyncTask<Void, Void, Boolean>() {
    val db = Room.databaseBuilder(context, HomeDatabase::class.java, "res-db").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {
            1 -> {
                db.orderDao().insertOrder(OrderEntity(restaurantId,foodItems))
                db.close()
                return true
            }
            2 -> {
                db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                db.close()
                return true
            }
        }
        return false
    }
}
}