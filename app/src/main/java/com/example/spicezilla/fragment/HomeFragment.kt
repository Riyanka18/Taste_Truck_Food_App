package com.example.spicezilla.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.spicezilla.adapter.HomeAdapter
import com.example.spicezilla.R
import com.example.spicezilla.model.Restaurants
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import util.Sort
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var recyclerAdapter:HomeAdapter
    lateinit var layoutManage: RecyclerView.LayoutManager
    private var restaurantList = arrayListOf<Restaurants>()
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    var ratingComparator= Comparator<Restaurants> { ob1, ob2 ->
        if (ob1.rating.compareTo(ob2.rating, true) == 0) {
            ob1.name.compareTo(ob2.name, true)
        } else {
            ob1.rating.compareTo(ob2.rating, true)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManage = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.home_progressBar)
        progressLayout = view.findViewById(R.id.Home_progress_layout)
        recyclerAdapter =HomeAdapter(restaurantList,activity as Context)
        setHasOptionsMenu(true)
        setRecycler(view)
        return view
    }

    fun setRecycler(view: View) {
        val recyclerRestaurant = view.findViewById(R.id.recyclerHome) as RecyclerView
        val queue = Volley.newRequestQueue(activity as Context)
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,
                "http://13.235.250.119/v2/restaurants/fetch_result/",
                null,
                Response.Listener<JSONObject> { response ->

                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    try {
                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resObj = resArray.getJSONObject(i)
                                val restaurant = Restaurants(
                                    resObj.getString("id").toInt(),
                                    resObj.getString("name"),
                                    resObj.getString("rating"),
                                    resObj.getString("cost_for_one").toInt(),
                                    resObj.getString("image_url")
                                )
                                restaurantList.add(restaurant)
                                recyclerHome.adapter =
                                    HomeAdapter(restaurantList, activity as Context)
                                recyclerHome.layoutManager = layoutManage
                                if (activity != null) {
                                    var restaurantsAdapter =
                                        HomeAdapter(restaurantList, activity as Context)
                                    val mLayoutManager = LinearLayoutManager(activity)
                                    recyclerRestaurant.layoutManager = mLayoutManager
                                }

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "json parse error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(activity as Context, "json exception", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(activity as Context, "Error listener", Toast.LENGTH_SHORT).show()
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
            val builder = AlertDialog.Builder(activity as Context)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            builder.create()
            builder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.dash_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.action_sort){
            Collections.sort(restaurantList,ratingComparator)
            restaurantList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}
