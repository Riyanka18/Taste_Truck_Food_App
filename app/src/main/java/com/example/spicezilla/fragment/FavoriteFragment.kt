package com.example.spicezilla.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.spicezilla.R
import com.example.spicezilla.adapter.HomeAdapter
import com.example.spicezilla.database.HomeDatabase
import com.example.spicezilla.database.HomeEntity
import com.example.spicezilla.model.Restaurants

class FavoriteFragment : Fragment() {
    lateinit var recyclerRestaurant: RecyclerView
    lateinit var allRestaurantsAdapter: HomeAdapter
    var restaurantList = arrayListOf<Restaurants>()
    lateinit var rlLoading: RelativeLayout
    lateinit var rlFav: RelativeLayout
    lateinit var rlNoFav: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        rlFav = view.findViewById(R.id.rlFavorites)
        rlNoFav = view.findViewById(R.id.rlNoFavorites)
        rlLoading = view.findViewById(R.id.rlLoading)
        rlLoading.visibility = View.VISIBLE
        setUpRecycler(view)
        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurants)
        val backgroundList=FavouritesAsync(activity as Context).execute().get()
        if (backgroundList.isEmpty()) {
            rlLoading.visibility = View.GONE
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            rlFav.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFav.visibility = View.GONE
            for (i in backgroundList) {
                restaurantList.add(
                    Restaurants(
                        i.Id,
                        i.name,
                        i.rating,
                        i.costForTwo.toInt(),
                        i.imageUrl
                    )
                )
            }
            allRestaurantsAdapter = HomeAdapter(restaurantList, activity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerRestaurant.layoutManager = mLayoutManager
            recyclerRestaurant.adapter = allRestaurantsAdapter
            recyclerRestaurant.setHasFixedSize(true)
        }
    }
    class FavouritesAsync(context: Context) : AsyncTask<Void, Void, List<HomeEntity>>() {
        val db = Room.databaseBuilder(context, HomeDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<HomeEntity> {
            return db.restaurantDao().getAllRestaurants()
        }
    }
}