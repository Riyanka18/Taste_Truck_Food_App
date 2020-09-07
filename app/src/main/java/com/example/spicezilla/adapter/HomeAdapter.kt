package com.example.spicezilla.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.spicezilla.R
import com.example.spicezilla.database.HomeDatabase
import com.example.spicezilla.database.HomeEntity
import com.example.spicezilla.fragment.HomeFragment
import com.example.spicezilla.fragment.RestaurantMenuFragment
import com.example.spicezilla.model.Restaurants
import com.squareup.picasso.Picasso

class HomeAdapter(var restaurants: ArrayList<Restaurants>, val context: Context):RecyclerView.Adapter<HomeAdapter.RestaurantsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RestaurantsViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.home_single_row, parent, false)
        return RestaurantsViewHolder(view)
    }
    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onBindViewHolder(holder:RestaurantsViewHolder, position: Int) {
        val res=restaurants[position]
        holder.restaurantName.text=res.name
        val c=res.costForTwo.toString()
        holder.cost.text=c+" /person"
        holder.rating.text=res.rating
        Picasso.get().load(res.imageUrl).error(R.drawable.res_image).into(holder.resimg)
        holder.cardRestaurant.setOnClickListener{
            val fragment = RestaurantMenuFragment()
            val args=Bundle()
            args.putInt("id",res.id)
            args.putString("name",res.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title=holder.restaurantName.text.toString()
        }
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(res.id.toString())) {
            holder.favImage.setImageResource(R.drawable.fav)
        } else {
            holder.favImage.setImageResource(R.drawable.favorite)
        }
        holder.favImage.setOnClickListener {
            val restaurantEntity =HomeEntity(
                res.id,
                res.name,
                res.rating,
                res.costForTwo.toString(),
                res.imageUrl
            )
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.favImage.setImageResource(R.drawable.fav)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.favImage.setImageResource(R.drawable.favorite)
                }
            }
        }
    }
    class RestaurantsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resimg = view.findViewById(R.id.imgRestaurant) as ImageView
        val restaurantName = view.findViewById(R.id.restaurantName) as TextView
        val rating = view.findViewById(R.id.restaurantRating) as TextView
        val cost = view.findViewById(R.id.priceforTwo) as TextView
        val cardRestaurant = view.findViewById(R.id.cardRestaurant) as CardView
        val favImage = view.findViewById(R.id.like) as ImageView
    }
    class DBAsyncTask(context: Context, val restaurantEntity: HomeEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = databaseBuilder(context, HomeDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val res: HomeEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.Id.toString())
                    db.close()
                    return res != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db= Room.databaseBuilder(context, HomeDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {
            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.Id.toString())
            }
            return listOfIds
        }
    }
}
